package com.jayway.jsonpath.filter;

import com.jayway.jsonpath.JsonUtil;
import org.json.simple.JSONArray;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: kallestenflo
 * Date: 2/2/11
 * Time: 2:32 PM
 */
public class PropertyFilter extends JsonPathFilterBase {

    private final static String WILDCARD = "*";

    private final String pathFragment;

    public PropertyFilter(String pathFragment) {
        this.pathFragment = pathFragment;
    }

    @Override
    public FilterOutput apply(FilterOutput filter) {

        List<Object> result = new JSONArray();

        if (WILDCARD.equals(pathFragment)) {
            if (filter.isList()) {
                for (Object current : filter.getResultAsList()) {
                    for (Object value : JsonUtil.toMap(current).values()) {
                        result.add(value);
                    }
                }
            } else {
                for (Object value : JsonUtil.toMap(filter.getResult()).values()) {
                    result.add(value);
                }
            }
            return new FilterOutput(result);
        } else {
            if (filter.isList()) {
                for (Object current : filter.getResultAsList()) {
                    if (JsonUtil.toMap(current).containsKey(pathFragment)) {

                        Object o = JsonUtil.toMap(current).get(pathFragment);
                        if (JsonUtil.isList(o)) {
                            result.addAll(JsonUtil.toList(o));

                        } else {
                            result.add(JsonUtil.toMap(current).get(pathFragment));
                        }
                    }
                }
                return new FilterOutput(result);
            } else {
                Object mapValue = null;
                if (JsonUtil.toMap(filter.getResult()).containsKey(pathFragment)) {
                    mapValue = JsonUtil.toMap(filter.getResult()).get(pathFragment);
                }
                return new FilterOutput(mapValue);
            }
        }
    }
}
