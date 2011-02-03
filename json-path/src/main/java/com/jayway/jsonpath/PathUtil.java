package com.jayway.jsonpath;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.LinkedList;
import java.util.List;

/**
 * User: kallestenflo
 * Date: 2/2/11
 * Time: 2:08 PM
 */
public class PathUtil {

    public static boolean isPathDefinite(String jsonPath) {
        return !jsonPath.replaceAll("\"[^\"\\\\\\n\r]*\"", "").matches(".*(\\.\\.|\\*|\\[[\\\\/]|\\?|,|:|>|\\(|<|=|\\+).*");
    }

    public static boolean isContainer(Object obj) {
        return (isArray(obj) || isDocument(obj));
    }

    public static boolean isArray(Object obj) {
        return (obj instanceof JSONArray);
    }

    public static boolean isDocument(Object obj) {
        return (obj instanceof JSONObject);
    }

    public static JSONArray toArray(Object array) {
        return (JSONArray) array;
    }

    public static JSONObject toDocument(Object document) {
        return (JSONObject) document;
    }


    public static List<String> splitPath(String jsonPath) {

        LinkedList<String> fragments = new LinkedList<String>();

        if (!jsonPath.startsWith("$.")) {
            jsonPath = "$." + jsonPath;
        }

        jsonPath = jsonPath.replace("..", ".~.")
                .replace("[", ".[")
                .replace("@.", "@");

        String[] split = jsonPath.split("\\.");

        for (int i = 0; i < split.length; i++) {
            if (split[i].trim().isEmpty()) {
                continue;
            }
            fragments.add(split[i].replace("@", "@.").replace("~", ".."));
        }
        return fragments;
    }
}
