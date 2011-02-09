package com.jayway.jsonpath;


import com.jayway.jsonpath.filter.FilterOutput;
import com.jayway.jsonpath.filter.JsonPathFilterChain;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.lang.String.format;

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

    private final static Logger log = Logger.getLogger(JsonPath.class.getName());

    private static JSONParser JSON_PARSER = new JSONParser();

    private JsonPathFilterChain filters;

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
        this.filters = new JsonPathFilterChain(PathUtil.splitPath(jsonPath));
    }

    /**
     * Applies this json path to the provided object
     *
     * @param json a json Object
     * @param <T>
     * @return list of objects matched by the given path
     */
    public <T> T read(Object json) {
        FilterOutput filterOutput = filters.filter(json);

        if(filterOutput == null || filterOutput.getResult() == null){
            return null;
        }

        return (T)filterOutput.getResult();
    }

    /**
     * Applies this json path to the provided object
     *
     * @param json a json string
     * @param <T>
     * @return list of objects matched by the given path
     */
    public <T> T read(String json) throws java.text.ParseException {
        return (T)read(parse(json));
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
        return (T)compile(jsonPath).read(json);
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
        return (T)compile(jsonPath).read(json);
    }


    /**
     * Creates a new JsonPath and applies it to the provided Json object. Note this method
     * will throw an exception if the provided path returns more than one object. This method
     * can be used with paths that are not definite but a warning will be generated.
     *
     * @param json     a json object
     * @param jsonPath the json path
     * @param <T>
     * @return the object matched by the given path
     */

//    public static <T> T readOne(Object json, String jsonPath) {
//        Object result = compile(jsonPath).read(json, jsonPath);
//
//        if (log.isLoggable(Level.WARNING)) {
//            if (!PathUtil.isPathDefinite(jsonPath)) {
//                log.warning("Using readOne() on a not definite json path may give incorrect results. Path : " + jsonPath);
//            }
//        }
//
//        return (T)result;
//
//        /*
//        if(result instanceof List){
//            if (result.size() > 1) {
//                throw new RuntimeException(format("Expected one result when reading path: %s  but was: ", jsonPath, result.size()));
//            }
//            else if (result.isEmpty()){
//                return null;
//            }
//            return (T) result.get(0);
//        }
//         */
//    }


    /**
     * Creates a new JsonPath and applies it to the provided Json object. Note this method
     * will throw an exception if the provided path returns more than one object. This method
     * can be used with paths that are not definite but a warning will be generated.
     *
     * @param json     a json string
     * @param jsonPath the json path
     * @param <T>
     * @return the object matched by the given path
     */
//    public static <T> T readOne(String json, String jsonPath) throws java.text.ParseException {
//        return (T) readOne(parse(json), jsonPath);
//    }

    private static Object parse(String json) throws java.text.ParseException {
        try {
            return JSON_PARSER.parse(json);
        } catch (ParseException e) {
            throw new java.text.ParseException(json, e.getPosition());
        }
    }
}
