package com.jayway.jsonpath;


import com.jayway.jsonpath.filter.JsonPathFilterChain;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.List;

import static java.lang.String.format;

/**
 * Created by IntelliJ IDEA.
 * User: kallestenflo
 * Date: 2/2/11
 * Time: 1:03 PM
 */
public class JsonPath {

    private static final JSONParser JSON_PARSER = new JSONParser();

    private JsonPathFilterChain filters;

    public static JsonPath compile(String jsonPath) {
        return new JsonPath(jsonPath);
    }


    private JsonPath(String jsonPath) {
        this.filters = new JsonPathFilterChain(PathUtil.splitPath(jsonPath));
    }

    public <T> List<T> read(Object json) {
        return (List<T>) filters.filter(json);
    }

    public <T> List<T> read(String json) throws java.text.ParseException {
        Object root = null;
        try {
            root = JSON_PARSER.parse(json);
        } catch (ParseException e) {
            throw new java.text.ParseException(json, e.getPosition());
        }
        return (List<T>) filters.filter(root);
    }

    public static <T> List<T> read(String json, String jsonPath) throws java.text.ParseException {
        JsonPath path = compile(jsonPath);

        return path.read(json);
    }

    public static <T> List<T> read(Object json, String jsonPath) throws java.text.ParseException {
        JsonPath path = compile(jsonPath);

        return path.read(json);
    }

    public static <T> T readOne(String json, String jsonPath) throws java.text.ParseException {
        JsonPath path = compile(jsonPath);

        List<Object> result = read(json, jsonPath);

        if (result.size() != 1) {
            throw new RuntimeException(format("Expected one result when reading path: %s  but was: ", jsonPath, result.size()));
        }

        return (T) result.get(0);
    }

    public static <T> T readOne(Object json, String jsonPath) throws java.text.ParseException {
        JsonPath path = compile(jsonPath);

        List<Object> result = read(json, jsonPath);

        if (result.size() != 1) {
            throw new RuntimeException(format("Expected one result when reading path: %s  but was: ", jsonPath, result.size()));
        }

        return (T) result.get(0);
    }
}
