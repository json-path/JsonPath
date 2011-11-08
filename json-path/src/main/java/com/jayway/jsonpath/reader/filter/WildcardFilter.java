package com.jayway.jsonpath.reader.filter;

import com.jayway.jsonpath.JsonUtil;
import net.minidev.json.JSONArray;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: kallestenflo
 * Date: 11/7/11
 * Time: 1:59 PM
 */
public class WildcardFilter extends Filter {

    public WildcardFilter(String condition) {
        super(condition);
    }

    @Override
    public Object filter(Object obj) {
        List<Object> result = new LinkedList<Object>();

        if (isList(obj)) {
            for (Object current : toList(obj)) {
                for (Object value : toMap(current).values()) {
                    result.add(value);
                }
            }
        } else {
            for (Object value : toMap(obj).values()) {
                result.add(value);
            }
        }
        return result;
    }
}
