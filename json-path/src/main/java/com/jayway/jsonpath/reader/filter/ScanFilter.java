package com.jayway.jsonpath.reader.filter;

import com.jayway.jsonpath.JsonUtil;

import java.util.LinkedList;
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
    public Object filter(Object obj) {
        List<Object> result = new LinkedList<Object>();
        scan(obj, result);

        return result;
    }


    private void scan(Object container, List<Object> result) {

        if (isMap(container)) {
            result.add(container);

            for (Object value : toMap(container).values()) {
                if (isContainer(value)) {
                    scan(value, result);
                }
            }
        } else if (isList(container)) {

            for (Object value : JsonUtil.toList(container)) {
                if (isContainer(value)) {
                    scan(value, result);
                }
            }
        }
    }
}
