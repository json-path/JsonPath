package com.jayway.jsonpath.reader.filter;

import com.jayway.jsonpath.spi.JsonProvider;

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
    public Object filter(Object obj, JsonProvider jsonProvider) {
        List<Object> result = jsonProvider.createList();

        if (jsonProvider.isList(obj)) {
            for (Object current : jsonProvider.toList(obj)) {
                for (Object value : jsonProvider.toMap(current).values()) {
                    result.add(value);
                }
            }
        } else {
            for (Object value : jsonProvider.toMap(obj).values()) {
                result.add(value);
            }
        }
        return result;
    }
}
