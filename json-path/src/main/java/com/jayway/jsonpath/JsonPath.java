package com.jayway.jsonpath;


import com.jayway.jsonpath.filter.JsonPathFilterChain;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.lang.String.format;

/**
 * Created by IntelliJ IDEA.
 * User: kallestenflo
 * Date: 2/2/11
 * Time: 1:03 PM
 */
public class JsonPath {

    private final static Logger log = Logger.getLogger(JsonPath.class.getName());

    private static JSONParser JSON_PARSER = new JSONParser();

    private JsonPathFilterChain filters;

    public static JsonPath compile(String jsonPath) {
        return new JsonPath(jsonPath);
    }

    /**
     * Creates a new JsonPath.
     *
     * @param jsonPath the path statement
     */
    private JsonPath(String jsonPath) {
        if(jsonPath == null ||
           jsonPath.trim().isEmpty() ||
           jsonPath.matches("new ") ||
           jsonPath.matches("[^\\?\\+\\=\\-\\*\\/\\!]\\(")){

           throw new InvalidPathException("Invalid path");
        }
        this.filters = new JsonPathFilterChain(PathUtil.splitPath(jsonPath));
    }

    public <T> List<T> read(Object json) {
        return (List<T>) filters.filter(json);
    }

    public <T> List<T> read(String json) throws java.text.ParseException {
        return read(parse(json));
    }

    public static <T> List<T> read(String json, String jsonPath) throws java.text.ParseException {
        return compile(jsonPath).read(json);
    }

    public static <T> List<T> read(Object json, String jsonPath) {
        return compile(jsonPath).read(json);
    }

    public static <T> T readOne(Object json, String jsonPath) {
        List<Object> result = compile(jsonPath).read(json, jsonPath);

        if(log.isLoggable(Level.WARNING)){
            if(!PathUtil.isPathDefinite(jsonPath)){
                log.warning("Using readOne(...) on a not definite json path may give incorrect results.");
            }
        }

        if (result.size() != 1) {
            throw new RuntimeException(format("Expected one result when reading path: %s  but was: ", jsonPath, result.size()));
        }

        return (T) result.get(0);
    }

    public static <T> T readOne(String json, String jsonPath) throws java.text.ParseException {
        return (T)readOne(parse(json), jsonPath);
    }

    private static Object parse(String json) throws java.text.ParseException {
        try {
            return JSON_PARSER.parse(json);
        } catch (ParseException e) {
            throw new java.text.ParseException(json, e.getPosition());
        }
    }
}
