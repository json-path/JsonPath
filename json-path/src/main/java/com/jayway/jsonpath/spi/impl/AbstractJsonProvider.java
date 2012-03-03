package com.jayway.jsonpath.spi.impl;

import com.jayway.jsonpath.spi.JsonProvider;

import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: kallestenflo
 * Date: 3/2/12
 * Time: 9:56 PM
 */
public abstract class AbstractJsonProvider implements JsonProvider {

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
    @SuppressWarnings({"unchecked"})
    public List<Object> toList(Object list) {
        return (List<Object>) list;
    }


    /**
     * Converts given object to a Map
     *
     * @param map
     * @return
     */
    @SuppressWarnings({"unchecked"})
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
