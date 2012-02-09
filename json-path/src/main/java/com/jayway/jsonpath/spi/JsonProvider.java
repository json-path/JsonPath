package com.jayway.jsonpath.spi;

import com.jayway.jsonpath.InvalidJsonException;
import com.jayway.jsonpath.spi.impl.JacksonProvider;
import com.jayway.jsonpath.spi.impl.JsonSmartProvider;

import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: kallestenflo
 * Date: 11/8/11
 * Time: 3:51 PM
 */
public abstract class JsonProvider {

    public abstract Mode getMode();

    public abstract Object parse(String json) throws InvalidJsonException;

    public abstract String toJson(Object obj);

    public abstract Map<String, Object> createMap();

    public abstract List<Object> createList();

    public static JsonProvider getInstance(){
        return new JsonSmartProvider();
        //return new JacksonProvider();
    }

    /**
     * checks if object is <code>instanceof</code> <code>java.util.List</code> or <code>java.util.Map</code>
     *
     * @param obj object to check
     * @return true if List or Map
     */
    public boolean isContainer(Object obj) {
        return (isList(obj) || isMap(obj));
    }

    /**
     * checks if object is <code>instanceof</code> <code>java.util.List</code>
     *
     * @param obj object to check
     * @return true if List
     */
    public boolean isList(Object obj) {
        return (obj instanceof List);
    }

    /**
     * Converts give object to a List
     *
     * @param list
     * @return
     */
    public List<Object> toList(Object list) {
        return (List<Object>) list;
    }


    /**
     * Converts given object to a Map
     *
     * @param map
     * @return
     */
    public Map<String, Object> toMap(Object map) {
        return (Map<String, Object>) map;
    }

    /**
     * Extracts a value from a Map
     *
     * @param map
     * @param key
     * @return
     */
    public Object getMapValue(Object map, String key) {
        return toMap(map).get(key);
    }

    /**
     * checks if object is <code>instanceof</code> <code>java.util.Map</code>
     *
     * @param obj object to check
     * @return true if Map
     */
    public boolean isMap(Object obj) {
        return (obj instanceof Map);
    }


}
