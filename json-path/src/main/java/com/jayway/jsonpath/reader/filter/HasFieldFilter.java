package com.jayway.jsonpath.reader.filter;

import com.jayway.jsonpath.spi.JsonProvider;

import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: kallestenflo
 * Date: 11/5/11
 * Time: 12:17 AM
 */
public class HasFieldFilter extends Filter {

    public HasFieldFilter(String condition) {
        super(condition);
    }

    @Override
    public Object filter(Object obj, JsonProvider jsonProvider) {

        //[?(@.isbn)]
        List<Object> src = jsonProvider.toList(obj);
        List<Object> result = jsonProvider.createList();

        String trimmedCondition = trim(condition, 5, 2);

        for (Object item : src) {
            if(jsonProvider.isMap(item)){
                Map<String, Object> map = jsonProvider.toMap(item);
                if(map.containsKey(trimmedCondition)){
                    result.add(map);
                }
            }
        }
        return result;
    }
}
