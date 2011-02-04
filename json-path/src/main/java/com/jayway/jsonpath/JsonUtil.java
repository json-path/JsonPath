package com.jayway.jsonpath;

import java.util.List;
import java.util.Map;

/**
 * User: kalle stenflo
 * Date: 2/4/11
 * Time: 1:01 PM
 */
public class JsonUtil {
    /**
     * checks if object is <code>instanceof</code> <code>java.util.List</code> or <code>java.util.Map</code>
     *
     * @param obj object to check
     * @return true if List or Map
     */
    public static boolean isContainer(Object obj) {
        return (isList(obj) || isMap(obj));
    }

    /**
     * checks if object is <code>instanceof</code> <code>java.util.List</code>
     *
     * @param obj object to check
     * @return true if List
     */
    public static boolean isList(Object obj) {
        return (obj instanceof List);
    }

    /**
     * checks if object is <code>instanceof</code> <code>java.util.Map</code>
     *
     * @param obj object to check
     * @return true if Map
     */
    public static boolean isMap(Object obj) {
        return (obj instanceof Map);
    }

    /**
     * converts casts to <code>java.util.List</code>
     *
     * @param obj
     * @return the list
     */
    public static List<Object> toList(Object obj) {
        return (List) obj;
    }

    /**
     * converts casts to <code>java.util.Map</code>
     *
     * @param obj
     * @return the Map
     */
    public static Map<Object, Object> toMap(Object obj) {
        return (Map) obj;
    }
}
