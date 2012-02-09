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

    public JsonModel(String json) {
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

    public <T> T get(String jsonPath) {
        JsonPath path = JsonPath.compile(jsonProvider, jsonPath);
        return (T) get(path);
    }

    public <T> T get(JsonPath jsonPath) {
        return (T) jsonPath.read(jsonObject);
    }

    public String getJson(JsonPath jsonPath) {
        return jsonProvider.toJson(jsonPath.read(jsonObject));
    }

    public JsonModel getModel(String jsonPath) {
        JsonPath path = JsonPath.compile(jsonProvider, jsonPath);

        return getModel(path);
    }

    public JsonModel getModel(JsonPath jsonPath) {
        Object subModel = jsonPath.read(jsonObject);

        return new JsonModel(subModel);
    }

    public JsonProvider getJsonProvider() {
        return jsonProvider;
    }
    
    public String getJson(){
        return jsonProvider.toJson(jsonObject);
    }
}
