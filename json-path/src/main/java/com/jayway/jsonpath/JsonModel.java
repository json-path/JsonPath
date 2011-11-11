package com.jayway.jsonpath;

import com.jayway.jsonpath.spi.JsonProvider;

/**
 * Created by IntelliJ IDEA.
 * User: kallestenflo
 * Date: 11/8/11
 * Time: 7:40 PM
 */
public class JsonModel {

    private Object jsonObject;

    private JsonProvider jsonProvider;

    private JsonModel(String json) {
        this.jsonProvider = JsonProvider.getInstance();
        this.jsonObject = jsonProvider.parse(json);
    }

    private JsonModel(Object jsonObject) {
        this.jsonProvider = JsonProvider.getInstance();
        this.jsonObject = jsonObject;
    }

    public static JsonModel create(String json) {
        return new JsonModel(json);
    }

    public <T> T read(String jsonPath) {
        JsonPath path = JsonPath.compile(jsonProvider, jsonPath);
        return (T)read(path);
    }

    public <T> T read(JsonPath jsonPath) {
        return (T) jsonPath.read(jsonObject);
    }

    public JsonModel get(String jsonPath) {
        JsonPath path = JsonPath.compile(jsonProvider, jsonPath);

        return get(path);
    }

    public JsonModel get(JsonPath jsonPath) {
        Object subModel = jsonPath.read(jsonObject);

        return new JsonModel(subModel);
    }
}
