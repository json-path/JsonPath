package com.jayway.jsonpath.filter;

import com.jayway.jsonpath.InvalidPathException;

import java.util.LinkedList;
import java.util.List;

/**
 * User: kallestenflo
 * Date: 2/2/11
 * Time: 2:00 PM
 */
public class JsonPathFilterChain {

    private List<JsonPathFilterBase> filters;

    public JsonPathFilterChain(List<String> pathFragments) {
        filters = configureFilters(pathFragments);
    }

    private List<JsonPathFilterBase> configureFilters(List<String> pathFragments) {

        List<JsonPathFilterBase> configured = new LinkedList<JsonPathFilterBase>();

        for (String pathFragment : pathFragments) {
            configured.add(JsonPathFilterFactory.createFilter(pathFragment));
        }
        return configured;
    }

    public FilterOutput filter(Object root) {

        FilterOutput out = new FilterOutput(root);

        for (JsonPathFilterBase filter : filters) {
            if (filter == null) {
                throw new InvalidPathException();
            }
            if(out.getResult() == null){
                return null;
            }
            out = filter.apply(out);
        }

        return out;
    }
}
