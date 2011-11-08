package com.jayway.jsonpath;


import com.jayway.jsonpath.reader.PathToken;
import com.jayway.jsonpath.reader.PathTokenizer;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
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

    public final static int STRICT_MODE = 0;
    public final static int SLACK_MODE = -1;

    private static int mode = SLACK_MODE;

    private final static Logger log = Logger.getLogger(JsonPath.class.getName());

    private static JSONParser JSON_PARSER = new JSONParser(JsonPath.mode);

    private static Pattern DEFINITE_PATH_PATTERN = Pattern.compile(".*(\\.\\.|\\*|\\[[\\\\/]|\\?|,|:\\s?\\]|\\[\\s?:|>|\\(|<|=|\\+).*");

    private PathTokenizer tokenizer;

    public static void setMode(int mode) {
        if (mode != JsonPath.mode) {
            JsonPath.mode = mode;
            JSON_PARSER = new JSONParser(JsonPath.mode);
        }
    }

    public static int getMode() {
        return mode;
    }


    /**
     * Creates a new JsonPath.
     *
     * @param jsonPath the path statement
     */
    private JsonPath(String jsonPath) {
        if (jsonPath == null ||
                jsonPath.trim().isEmpty() ||
                jsonPath.matches("new ") ||
                jsonPath.matches("[^\\?\\+\\=\\-\\*\\/\\!]\\(")) {

            throw new InvalidPathException("Invalid path");
        }
        this.tokenizer = new PathTokenizer(jsonPath);
        //this.filters = new JsonPathFilterChain(PathUtil.splitPath(jsonPath));
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

        if(!(container instanceof Map) && !(container instanceof List) ){
            throw new IllegalArgumentException("Invalid container object");
        }

        Object result = container;

        for (PathToken pathToken : tokenizer) {
            result = pathToken.filter(result);
        }
        return (T)result;
        /*
        FilterOutput filterOutput = filters.filter(container);

        if (filterOutput == null || filterOutput.getResult() == null) {
            return null;
        }

        return (T) filterOutput.getResult();
        */
    }

    /**
     * Applies this json path to the provided object
     *
     * @param json a json string
     * @param <T>
     * @return list of objects matched by the given path
     */
    public <T> T read(String json) throws java.text.ParseException {
        return (T) read(parse(json));
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

    /**
     * Creates a new JsonPath and applies it to the provided Json string
     *
     * @param json     a json string
     * @param jsonPath the json path
     * @param <T>
     * @return list of objects matched by the given path
     */
    public static <T> T read(String json, String jsonPath) throws java.text.ParseException {
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


    public static Object parse(String json) throws java.text.ParseException {
        try {
            return JSON_PARSER.parse(json);
        } catch (ParseException e) {
            throw new java.text.ParseException(json, e.getPosition());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
