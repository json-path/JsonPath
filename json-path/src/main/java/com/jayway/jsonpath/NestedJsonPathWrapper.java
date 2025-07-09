package com.jayway.jsonpath;

import java.io.Serializable;
import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;
import java.util.Objects;


/**
 * JSON path wrapper that supports accessing nested JSON strings using delimiter syntax.
 *
 * <p>Extends {@link DocumentContext} functionality to handle JSON strings embedded within JSON objects.
 * Uses special delimiter syntax {@code {field}} to access nested JSON content.</p>
 *
 * <h3>Key Features</h3>
 * <ul>
 *   <li>Nested JSON access with delimiter syntax</li>
 *   <li>Lazy loading for performance optimization</li>
 *   <li>Configurable delimiters to avoid conflicts</li>
 *   <li>Thread-safe operations</li>
 *   <li>Automatic caching of parsed documents</li>
 * </ul>
 *
 * <h3>Usage Examples</h3>
 * <pre>
 * // Basic usage
 * // Data example: {"user": {"name": "Alice"}}
 * Map&lt;String, Object&gt; jsonData = new HashMap&lt;&gt;();
 * NestedJsonPathWrapper wrapper = NestedJsonPathWrapper.wrapJsonOrMap(jsonData);
 * wrapper.putValueByPath("$.user.name", "Alice");
 * Object name = wrapper.getValueByPath("$.user.name");        // Gets "Alice"
 *
 * // Nested JSON access with default delimiters
 * Map&lt;String, Object&gt; nestedData = new HashMap&lt;&gt;();
 * nestedData.put("profile", "{\"name\":\"John\",\"age\":30,\"address\":{\"city\":\"NYC\",\"zip\":\"10001\"}}");
 * nestedData.put("items", "[{\"id\":1,\"name\":\"item1\"},{\"id\":2,\"name\":\"item2\"}]");
 * nestedData.put("tags", "[\"java\",\"json\",\"api\"]");
 *
 * NestedJsonPathWrapper nestedWrapper = NestedJsonPathWrapper.wrapJsonOrMap(nestedData);
 *
 * // Nested object access
 * String name = (String) nestedWrapper.getValueByPath("$.{profile}.name");           // Gets "John"
 * String city = (String) nestedWrapper.getValueByPath("$.{profile}.address.city");  // Gets "NYC"
 * nestedWrapper.putValueByPath("$.{profile}.age", 35);                              // Update age
 *
 * // Nested array access
 * String itemName = (String) nestedWrapper.getValueByPath("$.{items}[0].name");     // Gets "item1"
 * String tag = (String) nestedWrapper.getValueByPath("$.{tags}[1]");                // Gets "json"
 * nestedWrapper.putValueByPath("$.{items}[0].name", "newItem1");                    // Update item name
 *
 * // JSONPath functions on nested data
 * Integer itemCount = (Integer) nestedWrapper.getValueByPath("$.{items}.length()"); // Gets array length
 * Object allIds = nestedWrapper.getValueByPath("$.{items}[*].id");                  // Gets all IDs
 *
 * // Custom delimiters - when default {} conflicts with JSON keys
 * // Data example: {"profile": "{\"name\":\"John\"}", "{profile}": "{\"name\":\"Jane\"}"}
 * Map&lt;String, Object&gt; data = new HashMap&lt;&gt;();
 * data.put("profile", "{\"name\":\"John\"}");        // Normal key
 * data.put("{profile}", "{\"name\":\"Jane\"}");      // Key contains {}
 *
 * NestedJsonPathWrapper customWrapper = NestedJsonPathWrapper.wrapJsonOrMap(data, true, "«", "»");
 * Object value1 = customWrapper.getValueByPath("$.«profile».name");      // Gets "John"
 * Object value2 = customWrapper.getValueByPath("$.«{profile}».name");    // Gets "Jane"
 * </pre>
 *
 * @author Hercules
 * @version 1.0
 * @see DocumentContext
 * @see JsonPath
 * @since 1.0
 */
public class NestedJsonPathWrapper implements Serializable {

    /** The wrapped DocumentContext instance for JSON operations */
    private transient DocumentContext __self;

    /** Default opening delimiter for nested JSON access */
    private static final String DEFAULT_OPEN_DELIMITER = "{";

    /** Default closing delimiter for nested JSON access */
    private static final String DEFAULT_CLOSE_DELIMITER = "}";

    /** Default JsonPath configuration with safe path operations */
    private static final Configuration DEFAULT_CONFIGURATION = Configuration.defaultConfiguration()
            .addOptions(Option.DEFAULT_PATH_LEAF_TO_NULL, Option.SUPPRESS_EXCEPTIONS);

    /** Opening delimiter for nested JSON field identification */
    private transient final String openDelimiter;

    /** Closing delimiter for nested JSON field identification */
    private transient final String closeDelimiter;

    /** Whether lazy loading is enabled for nested document changes */
    private transient final boolean lazyLoading;

    /** JsonPath configuration for parsing behavior */
    private transient final Configuration configuration;

    /**
     * When you using lazyLoading mode,this cache will storage all child Document Path which it‘s has changed.
     * This cache will destroy when user invokes the toJson() Or toJsonString() method
     */
    private transient final Set<String> changedSecondaryDocumentPath = new HashSet<>();

    /**
     * Cache for secondary document context wrappers.
     *
     * <p>This map stores {@link NestedJsonPathWrapper} instances for nested JSON
     * strings, keyed by their path expressions. This allows for efficient reuse
     * of parsed nested documents.</p>
     */
    private transient final Map<String, NestedJsonPathWrapper> __dic = new HashMap<>();

    private NestedJsonPathWrapper(boolean lazyLoading, String openDelimiter, String closeDelimiter, Configuration configuration) {
        this.lazyLoading = lazyLoading;
        this.configuration = configuration != null ? configuration : DEFAULT_CONFIGURATION;
        if(!Objects.equals(openDelimiter,closeDelimiter)) {
            this.openDelimiter = openDelimiter;
            this.closeDelimiter = closeDelimiter;
        }else{
            throw new IllegalArgumentException("openDelimiter and closeDelimiter must be different");
        }
    }

    private void setDocumentContext(DocumentContext documentContext) {
        __self = documentContext;
    }


    /** Returns the wrapped DocumentContext instance */
    private DocumentContext getDocumentContext() {
        return __self;
    }

    /** Returns the document as JSON string, processing lazy loading changes first */
    public String toJsonString() {
        processLazyLoadingContext();
        return __self != null ? __self.jsonString() : null;
    }

    /** Returns the document as Java object, processing lazy loading changes first */
    public Object toJson() {
        processLazyLoadingContext();
        return __self != null ? __self.json() : null;
    }

    private void processLazyLoadingContext() {
        if (lazyLoading) {
            synchronized (changedSecondaryDocumentPath) {
                for (String secondaryPath : changedSecondaryDocumentPath) {
                    NestedJsonPathWrapper child = __dic.get(secondaryPath);
                    if (child != null) {
                        String value = child.toJsonString();
                        String originalPath = secondaryPath.replace(openDelimiter, "")
                                .replace(closeDelimiter, "");
                        __self.set(originalPath, value);
                    }
                }
                changedSecondaryDocumentPath.clear();
            }
        }
    }

    /**
     * Creates a wrapper from JSON string or Map with lazy loading enabled.
     *
     * @param param JSON string or Map instance
     * @return new NestedJsonPathWrapper with lazy loading, or null if param is null
     * @throws UnsupportedOperationException if parameter type is not supported
     */
    public static NestedJsonPathWrapper wrapJsonOrMap(Object param) {
        return wrapJsonOrMap(param, true);
    }

    /**
     * Creates a wrapper from JSON string or Map with configurable lazy loading.
     *
     * @param param JSON string or Map instance
     * @param lazyLoading true for lazy loading, false for immediate updates
     * @return new NestedJsonPathWrapper, or null if param is null
     * @throws UnsupportedOperationException if parameter type is not supported
     */
    public static NestedJsonPathWrapper wrapJsonOrMap(Object param, boolean lazyLoading) {
        return wrapJsonOrMap(param, lazyLoading, DEFAULT_OPEN_DELIMITER, DEFAULT_CLOSE_DELIMITER);
    }

    /**
     * Creates a wrapper with custom JsonPath configuration.
     *
     * @param param JSON string or Map instance
     * @param configuration JsonPath configuration, or null for default
     * @return new NestedJsonPathWrapper, or null if param is null
     * @throws UnsupportedOperationException if parameter type is not supported
     */
    public static NestedJsonPathWrapper wrapJsonOrMap(Object param, Configuration configuration) {
        return wrapJsonOrMap(param, true, DEFAULT_OPEN_DELIMITER, DEFAULT_CLOSE_DELIMITER, configuration);
    }

    /**
     * Creates a wrapper with configurable lazy loading and custom JsonPath configuration.
     *
     * @param param JSON string or Map instance
     * @param lazyLoading true for lazy loading, false for immediate updates
     * @param configuration JsonPath configuration, or null for default
     * @return new NestedJsonPathWrapper, or null if param is null
     * @throws UnsupportedOperationException if parameter type is not supported
     */
    public static NestedJsonPathWrapper wrapJsonOrMap(Object param, boolean lazyLoading, Configuration configuration) {
        return wrapJsonOrMap(param, lazyLoading, DEFAULT_OPEN_DELIMITER, DEFAULT_CLOSE_DELIMITER, configuration);
    }

    /**
     * Creates a wrapper with custom delimiters for nested JSON access.
     *
     * @param param JSON string or Map instance
     * @param lazyLoading true for lazy loading, false for immediate updates
     * @param openDelimiter opening delimiter for nested JSON fields
     * @param closeDelimiter closing delimiter for nested JSON fields
     * @return new NestedJsonPathWrapper, or null if param is null
     * @throws UnsupportedOperationException if parameter type is not supported
     * @throws IllegalArgumentException if delimiters are the same
     */
    public static NestedJsonPathWrapper wrapJsonOrMap(Object param, boolean lazyLoading, String openDelimiter, String closeDelimiter) {
        return wrapJsonOrMap(param, lazyLoading, openDelimiter, closeDelimiter, null);
    }

    /**
     * Creates a wrapper with full configuration options including custom delimiters and JsonPath configuration.
     *
     * @param param JSON string or Map instance
     * @param lazyLoading true for lazy loading, false for immediate updates
     * @param openDelimiter opening delimiter for nested JSON fields
     * @param closeDelimiter closing delimiter for nested JSON fields
     * @param configuration JsonPath configuration, or null for default
     * @return new NestedJsonPathWrapper, or null if param is null
     * @throws UnsupportedOperationException if parameter type is not supported
     * @throws IllegalArgumentException if delimiters are the same
     */
    public static NestedJsonPathWrapper wrapJsonOrMap(Object param, boolean lazyLoading, String openDelimiter, String closeDelimiter, Configuration configuration) {
        if (param == null) {
            return null;
        }
        if (param instanceof Map) {
            return wrapDocumentContext(parseMap((Map) param, configuration), lazyLoading, openDelimiter, closeDelimiter, configuration);
        } else if (param instanceof String) {
            return wrapDocumentContext(parseJson(Objects.toString(param), configuration), lazyLoading, openDelimiter, closeDelimiter, configuration);
        }
        throw new UnsupportedOperationException("unsupported data type:" + param.getClass().getName());
    }

    /** Parses JSON string with default safe configuration */
    private static DocumentContext parseJson(String json) {
        return parseJson(json, null);
    }

    /** Parses JSON string with specified configuration */
    private static DocumentContext parseJson(String json, Configuration configuration) {
        Configuration config = configuration != null ? configuration : DEFAULT_CONFIGURATION;
        return JsonPath.using(config).parse(json);
    }

    /** Parses Map with default safe configuration */
    private static DocumentContext parseMap(Map data) {
        return parseMap(data, null);
    }

    /** Parses Map with specified configuration */
    private static DocumentContext parseMap(Map data, Configuration configuration) {
        Configuration config = configuration != null ? configuration : DEFAULT_CONFIGURATION;
        return JsonPath.using(config).parse(data);
    }

    /** Wraps DocumentContext with specified configuration */
    public static NestedJsonPathWrapper wrapDocumentContext(DocumentContext documentContext, boolean lazyLoading, String openDelimiter, String closeDelimiter) {
        return wrapDocumentContext(documentContext, lazyLoading, openDelimiter, closeDelimiter, null);
    }

    /** Wraps DocumentContext with full configuration including JsonPath settings */
    public static NestedJsonPathWrapper wrapDocumentContext(DocumentContext documentContext, boolean lazyLoading, String openDelimiter, String closeDelimiter, Configuration configuration) {
        NestedJsonPathWrapper wrapper = new NestedJsonPathWrapper(lazyLoading, openDelimiter, closeDelimiter, configuration);
        wrapper.setDocumentContext(documentContext);
        return wrapper;
    }

    /**
     * Sets a value at the specified JSON path, supporting nested JSON access with delimiter syntax.
     *
     * @param path JSON path (e.g., "$.user.name" or "$.{profile}.age")
     * @param value value to set
     * @return true if successful, false if path is invalid
     */
    public synchronized boolean putValueByPath(String path, Object value) {
        if (strIsBlank(path)) {
            return false;
        }
        if (path.contains(openDelimiter)) {
            int index = path.indexOf(closeDelimiter);
            String childPath = "$" + path.substring(index + 1);
            NestedJsonPathWrapper childPathWrapper = discoverChildDocumentContextWrapper(path);
            if(childPathWrapper == null){
                return false;
            }
            if(!childPathWrapper.putValueByPath(childPath, value)){
                return false;
            }
            String rootPath = path.substring(0, index + 1);
            String originalRootPath = rootPath.replace(openDelimiter, "")
                    .replace(closeDelimiter, "");
            if (lazyLoading) {
                changedSecondaryDocumentPath.add(rootPath);
            } else {
                __self.set(originalRootPath, __dic.get(rootPath).getDocumentContext().jsonString());
            }
        } else {
            int index = path.lastIndexOf('.');
            String rootPath = path.substring(0, index);
            String key = path.substring(index + 1);
            __self.put(rootPath, key, value);
        }
        return true;
    }

    /**
     * Gets a value from the specified JSON path, supporting nested JSON access with delimiter syntax.
     *
     * @param path JSON path (e.g., "$.user.name" or "$.{profile}.age")
     * @return value at the path, or null if not found
     */
    public Object getValueByPath(String path) {
        if (strIsBlank(path)) {
            return null;
        }
        if (path.contains(openDelimiter)) {
            int index = path.indexOf(closeDelimiter);
            String childPath = "$" + path.substring(index + 1);
            NestedJsonPathWrapper childPathWrapper = discoverChildDocumentContextWrapper(path);
            return childPathWrapper != null ? childPathWrapper.getValueByPath(childPath) : null;
        } else {
            return __self != null ? __self.read(path) : null;
        }
    }


    /** Discovers and caches child document wrapper for nested JSON access */
    private NestedJsonPathWrapper discoverChildDocumentContextWrapper(String path) {
        int index = path.indexOf(closeDelimiter);
        String rootPath = path.substring(0, index + 1);
        String originalRootPath = rootPath.replace(openDelimiter, "")
                .replace(closeDelimiter, "");
        if (!__dic.containsKey(rootPath)) {
            synchronized (__dic) {
                if (!__dic.containsKey(rootPath)) {
                    String insideValue = __self.read(originalRootPath);
                    if (strIsBlank(insideValue)) {
                        __dic.put(rootPath, null);
                    } else {
                        DocumentContext documentContext = parseJson(insideValue, this.configuration);
                        NestedJsonPathWrapper wrapper = wrapDocumentContext(documentContext, this.lazyLoading, this.openDelimiter, this.closeDelimiter, this.configuration);
                        __dic.put(rootPath, wrapper);
                    }
                }
            }
        }
        return __dic.get(rootPath);
    }

    private boolean strIsBlank(CharSequence cs) {
        int strLen = (cs == null ? 0 : cs.length());
        if (strLen == 0) {
            return true;
        } else {
            for(int i = 0; i < strLen; ++i) {
                if (!Character.isWhitespace(cs.charAt(i))) {
                    return false;
                }
            }
            return true;
        }
    }
}
