package com.jayway.jsonpath.reader.filter;

import com.jayway.jsonpath.spi.JsonProvider;

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

    public Object filter(Object obj, JsonProvider jsonProvider) {
        if (jsonProvider.isList(obj)) {
            List<Object> result = jsonProvider.createList();
            for (Object current : jsonProvider.toList(obj)) {
                if (jsonProvider.isMap(current)) {
                    Map<String, Object> map = jsonProvider.toMap(current);
                    if (map.containsKey(condition)) {
                        Object o = map.get(condition);
                        if (jsonProvider.isList(o)) {
                            result.addAll(jsonProvider.toList(o));
                        } else {
                            result.add(map.get(condition));
                        }
                    }
                }
            }
            return result;
        } else {
            return jsonProvider.getMapValue(obj, condition);
        }
    }
}
