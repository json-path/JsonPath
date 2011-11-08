package com.jayway.jsonpath.reader.filter;


import com.jayway.jsonpath.spi.JsonProvider;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: kallestenflo
 * Date: 11/7/11
 * Time: 12:31 PM
 */
public class ScanFilter extends Filter {

    public ScanFilter(String condition) {
        super(condition);
    }

    @Override
    public Object filter(Object obj, JsonProvider jsonProvider) {
        List<Object> result = jsonProvider.createList();
        scan(obj, result, jsonProvider);

        return result;
    }


    private void scan(Object container, List<Object> result, JsonProvider jsonProvider) {

        if (jsonProvider.isMap(container)) {
            result.add(container);

            for (Object value : jsonProvider.toMap(container).values()) {
                if (jsonProvider.isContainer(value)) {
                    scan(value, result, jsonProvider);
                }
            }
        } else if (jsonProvider.isList(container)) {

            for (Object value : jsonProvider.toList(container)) {
                if (jsonProvider.isContainer(value)) {
                    scan(value, result, jsonProvider);
                }
            }
        }
    }
}
