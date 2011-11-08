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
        if (isList(obj)) {
            List<Object> result = new LinkedList<Object>();
            for (Object current : toList(obj)) {
                if (isMap(current)) {
                    Map<String, Object> map = toMap(current);
                    if (map.containsKey(condition)) {
                        Object o = map.get(condition);
                        if (isList(o)) {
                            result.addAll(toList(o));
                        } else {
                            result.add(map.get(condition));
                        }
                    }
                }
            }
            return result;
        } else {
            return getMapValue(obj, condition);
        }
    }
}
