package com.jayway.jsonpath.filter;

import com.jayway.jsonpath.PathUtil;
import org.json.simple.JSONArray;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: kallestenflo
 * Date: 2/2/11
 * Time: 2:32 PM
 */
public class PropertyFilter extends JsonPathFilterBase {
    private final String pathFragment;

    public PropertyFilter(String pathFragment) {
        this.pathFragment = pathFragment;
    }

    @Override
    public List<Object> apply(List<Object> filter) {
        List<Object> result = new JSONArray();

        for (Object current : filter) {

            if ("*".equals(pathFragment)) {

                for (Object value : PathUtil.toDocument(current).values()) {
                     result.add(value);
                }

            } else {
                if (PathUtil.toDocument(current).containsKey(pathFragment)) {
                    result.add(PathUtil.toDocument(current).get(pathFragment));
                }
            }
        }
        return result;
    }
}
