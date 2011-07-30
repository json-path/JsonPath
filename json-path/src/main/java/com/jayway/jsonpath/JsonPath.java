package com.jayway.jsonpath;


import com.jayway.jsonpath.filter.FilterOutput;
import com.jayway.jsonpath.filter.JsonPathFilterChain;
import com.jayway.jsonpath.json.JsonElement;
import com.jayway.jsonpath.json.JsonException;
import com.jayway.jsonpath.json.JsonFactory;
import com.jayway.jsonpath.json.JsonParser;



import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

import net.minidev.json.parser.ParseException;


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

//    private static int mode = SLACK_MODE;
	private static JsonFactory factory;

    private final static Logger log = Logger.getLogger(JsonPath.class.getName());



    private JsonPathFilterChain filters;

    /*
    public static void setMode(int mode){
        if(mode != JsonPath.mode){
            JsonPath.mode = mode;
            JSON_PARSER = new JSONParser(JsonPath.mode);
        }
    }
    */
/*
    public static int getMode(){
        return mode;
    }
*/

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
     * @throws JsonException 
     */
    public JsonElement read(JsonElement json) throws JsonException {
        FilterOutput filterOutput = filters.filter(json);
        
        if (filterOutput == null || filterOutput.getResult() == null) {
             return null;
        }

        return filterOutput.getResult();
        
    }
    
    
    
    /**
     * Applies this json path to the provided object
     *
     * @param json a json Object
     * @param <T>
     * @return list of objects matched by the given path
     * @throws JsonException 
     */
    public List<JsonElement> readResultSet(JsonElement json) throws JsonException {
        FilterOutput filterOutput = filters.filter(json);
        
        if (filterOutput == null || filterOutput.getResult() == null) {
             throw new JsonException("Element not found");
        }

        return filterOutput.getList();
        
    }
        
    
    

    /**
     * Applies this json path to the provided object
     *
     * @param json a json string
     * @param <T>
     * @return list of objects matched by the given path
     * @throws JsonException 
     */
    public JsonElement read(String json) throws java.text.ParseException, JsonException {
        return  read(parse(json));
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
     * @throws JsonException 
     */
    public static JsonElement read(String json, String jsonPath) throws java.text.ParseException, JsonException {
        return compile(jsonPath).read(json);
    }

    /**
     * Creates a new JsonPath and applies it to the provided Json object
     *
     * @param json     a json object
     * @param jsonPath the json path
     * @param <T>
     * @return list of objects matched by the given path
     * @throws JsonException 
     */
    public static JsonElement read(JsonElement json, String jsonPath) throws JsonException {
        return  compile(jsonPath).read(json);
    }

    
    public static List<JsonElement> readResultSet(JsonElement json, String jsonPath) throws JsonException {
        return  compile(jsonPath).readResultSet(json);
    }




    public  static JsonElement parse(String json)  {
        try {
            return JsonFactory.getInstance().parse(json);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (com.jayway.jsonpath.json.ParseException e) {
        	throw new RuntimeException(e);
		}
    }

}
