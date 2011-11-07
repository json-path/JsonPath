package com.jayway.jsonpath.reader.filter;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: kallestenflo
 * Date: 11/4/11
 * Time: 10:17 PM
 */
public class FieldFilter extends Filter {

    public FieldFilter(String condition) {
        super(condition);
    }

    public Object filter(Object obj) {
        if(isList(obj)){
            List<Object> result = new LinkedList<Object>();
            for (Object item : toList(obj)) {
                result.add(filter(item));
            }
            return result;
        } else {
            return getMapValue(obj, condition);
        }
    }
}
