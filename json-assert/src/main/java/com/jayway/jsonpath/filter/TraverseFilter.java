package com.jayway.jsonpath.filter;

import com.jayway.jsonpath.PathUtil;
import org.json.simple.JSONArray;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: kallestenflo
 * Date: 2/2/11
 * Time: 2:33 PM
 */
public class TraverseFilter extends JsonPathFilterBase {
    private final String pathFragment;

    public TraverseFilter(String pathFragment) {
        this.pathFragment = pathFragment;
    }

    @Override
    public List<Object> apply(List<Object> filter) {
        List<Object> result = new JSONArray();

        traverse(filter, result);

        return result;
    }

    private void traverse(Object container, List<Object> result) {

        if (PathUtil.isDocument(container)) {
            result.add(container);

            for (Object value : PathUtil.toDocument(container).values()) {
                if (PathUtil.isContainer(value)) {
                    traverse(value, result);
                }
            }
        } else if (PathUtil.isArray(container)) {

            for (Object value : PathUtil.toArray(container)) {
                if (PathUtil.isContainer(value)) {
                    traverse(value, result);
                }
            }
        }
    }

}