package com.jayway.jsonpath;


import com.jayway.jsonpath.reader.PathToken;
import com.jayway.jsonpath.reader.PathTokenizer;
import com.jayway.jsonpath.spi.JsonProvider;

import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * User: kalle stenflo
 * Date: 2/2/11
 * Time: 1:03 PM
 * <p/>
 * JsonPath is to JSON what XPATH is to XML, a simple way to extract parts of a given document. JsonPath is
 * available in many programming languages such as Javascript, Python and PHP.
 * <p/>
 * JsonPath allows you to compile a json path string to use it many times or to compile and apply in one
 * single on demand operation.
 * <p/>
 * Given the Json document:
 * <p/>
 * <code>
 * String json =
 * "{
 * "store":
 * {
 * "book":
 * [
 * {
 * "category": "reference",
 * "author": "Nigel Rees",
 * "title": "Sayings of the Century",
 * "price": 8.95
 * },
 * {
 * "category": "fiction",
 * "author": "Evelyn Waugh",
 * "title": "Sword of Honour",
 * "price": 12.99
 * }
 * ],
 * "bicycle":
 * {
 * "color": "red",
 * "price": 19.95
 * }
 * }
 * }";
 * </code>
 * <p/>
 * A JsonPath can be compiled and used as shown:
 * <p/>
 * <code>
 * JsonPath path = JsonPath.compile("$.store.book[1]");
 * <br/>
 * List&lt;Object&gt; books = path.read(json);
 * </code>
 * </p>
 * Or:
 * <p/>
 * <code>
 * List&lt;Object&gt; authors = JsonPath.read(json, "$.store.book[*].author")
 * </code>
 * <p/>
 * If the json path returns a single value (is definite):
 * </p>
 * <code>
 * String author = JsonPath.read(json, "$.store.book[1].author")
 * </code>
 */
public class JsonPath {

    private static Pattern DEFINITE_PATH_PATTERN = Pattern.compile(".*(\\.\\.|\\*|\\[[\\\\/]|\\?|,|:\\s?\\]|\\[\\s?:|>|\\(|<|=|\\+).*");

    private PathTokenizer tokenizer;

    private JsonProvider jsonProvider;


    /**
     * Creates a new JsonPath.
     *
     * @param jsonPath the path statement
     */
    private JsonPath(String jsonPath) {
        this(JsonProvider.getInstance(), jsonPath);
    }


    private JsonPath(JsonProvider jsonProvider, String jsonPath) {
        if (jsonPath == null ||
                jsonPath.trim().isEmpty() ||
                jsonPath.matches("new ") ||
                jsonPath.matches("[^\\?\\+\\=\\-\\*\\/\\!]\\(")) {

            throw new InvalidPathException("Invalid path");
        }
        this.jsonProvider = jsonProvider;
        this.tokenizer = new PathTokenizer(jsonPath, jsonProvider);
    }

    public String getPath() {
        return this.tokenizer.getPath();
    }

    /**
     * Checks if a path points to a single item or if it potentially returns multiple items
     * <p/>
     * a path is considered <strong>not</strong> definite if it contains a scan fragment ".."
     * or an array position fragment that is not based on a single index
     * <p/>
     * <p/>
     * definite path examples are:
     * <p/>
     * $store.book
     * $store.book[1].title
     * <p/>
     * not definite path examples are:
     * <p/>
     * $..book
     * $.store.book[1,2]
     * $.store.book[?(@.category = 'fiction')]
     *
     * @return true if path is definite (points to single item)
     */
    public boolean isPathDefinite() {
        //return !getPath().replaceAll("\"[^\"\\\\\\n\r]*\"", "").matches(".*(\\.\\.|\\*|\\[[\\\\/]|\\?|,|:\\s?\\]|\\[\\s?:|>|\\(|<|=|\\+).*");

        String preparedPath = getPath().replaceAll("\"[^\"\\\\\\n\r]*\"", "");

        return !DEFINITE_PATH_PATTERN.matcher(preparedPath).matches();

    }

    /**
     * Applies this container path to the provided object
     *
     * @param container a container Object
     * @param <T>
     * @return list of objects matched by the given path
     */
    public <T> T read(Object container) {
        if (!(container instanceof Map) && !(container instanceof List)) {
            throw new IllegalArgumentException("Invalid container object");
        }

        Object result = container;

        for (PathToken pathToken : tokenizer) {
            result = pathToken.filter(result, jsonProvider);
        }
        return (T) result;
    }

    /**
     * Applies this json path to the provided object
     *
     * @param json a json string
     * @param <T>
     * @return list of objects matched by the given path
     */
    public <T> T read(String json) {
        return (T) read(jsonProvider.parse(json));
    }

    /**
     * Compiles a JsonPath from the given string
     *
     * @param jsonPath to compile
     * @return compiled JsonPath
     */
    public static JsonPath compile(String jsonPath) {
        return new JsonPath(jsonPath);
    }

    public static JsonPath compile(JsonProvider provider, String jsonPath) {
        return new JsonPath(provider, jsonPath);
    }

    /**
     * Creates a new JsonPath and applies it to the provided Json string
     *
     * @param json     a json string
     * @param jsonPath the json path
     * @param <T>
     * @return list of objects matched by the given path
     */
    public static <T> T read(String json, String jsonPath) {
        return (T) compile(jsonPath).read(json);
    }

    /**
     * Creates a new JsonPath and applies it to the provided Json object
     *
     * @param json     a json object
     * @param jsonPath the json path
     * @param <T>
     * @return list of objects matched by the given path
     */
    public static <T> T read(Object json, String jsonPath) {
        return (T) compile(jsonPath).read(json);
    }

}
