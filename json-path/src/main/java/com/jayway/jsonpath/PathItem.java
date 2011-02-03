package com.jayway.jsonpath;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 * Created by IntelliJ IDEA.
 * User: kallestenflo
 * Date: 2/2/11
 * Time: 2:33 PM
 */
public class PathItem {

    private final String path;

    private final Object target;

    public PathItem(String path, Object target) {
        this.path = path;
        this.target = target;
    }

    public String getPath() {
        return path;
    }

    public Object getTarget() {
        return target;
    }


}
