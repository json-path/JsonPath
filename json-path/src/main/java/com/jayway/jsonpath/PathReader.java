package com.jayway.jsonpath;

import com.jayway.jsonpath.spi.JsonProvider;
import com.jayway.jsonpath.spi.impl.DefaultJsonProvider;

/**
 * Created by IntelliJ IDEA.
 * User: kallestenflo
 * Date: 11/8/11
 * Time: 7:40 PM
 */
public class PathReader {

    private Object jsonModel;

    private JsonProvider jsonProvider;

    private PathReader(String json) {
        this.jsonProvider = new DefaultJsonProvider();
        this.jsonModel = jsonProvider.parse(json);
    }

    private PathReader(Object jsonModel) {
        this.jsonProvider = new DefaultJsonProvider();
        this.jsonModel = jsonModel;
    }

    public static PathReader create(String json) {
        return new PathReader(json);
    }

    public <T> T read(String jsonPath) {
        return (T)JsonPath.compile(jsonProvider, jsonPath).read(jsonModel);
    }

    public <T> T read(JsonPath jsonPath) {
        return (T)jsonPath.read(jsonModel);
    }

    public PathReader get(String jsonPath){
        Object subModel = read(jsonPath);

        return new PathReader(subModel);
    }
}
