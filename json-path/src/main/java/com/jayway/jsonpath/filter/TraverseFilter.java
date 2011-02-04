package com.jayway.jsonpath.filter;

import com.jayway.jsonpath.JsonUtil;
import org.json.simple.JSONArray;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: kallestenflo
 * Date: 2/2/11
 * Time: 2:33 PM
 */
public class TraverseFilter extends JsonPathFilterBase {

    @Override
    public List<Object> apply(List<Object> filter) {
        List<Object> result = new JSONArray();

        traverse(filter, result);

        return result;
    }

    private void traverse(Object container, List<Object> result) {

        if (JsonUtil.isMap(container)) {
            result.add(container);

            for (Object value : JsonUtil.toMap(container).values()) {
                if (JsonUtil.isContainer(value)) {
                    traverse(value, result);
                }
            }
        } else if (JsonUtil.isList(container)) {

            for (Object value : JsonUtil.toList(container)) {
                if (JsonUtil.isContainer(value)) {
                    traverse(value, result);
                }
            }
        }
    }

}