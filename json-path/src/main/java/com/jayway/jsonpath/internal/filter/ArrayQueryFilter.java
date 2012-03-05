package com.jayway.jsonpath.internal.filter;

import com.jayway.jsonpath.Filter;
import com.jayway.jsonpath.spi.JsonProvider;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: kallestenflo
 * Date: 3/5/12
 * Time: 4:41 PM
 */
public class ArrayQueryFilter extends PathTokenFilter {

    ArrayQueryFilter(String condition) {
        super(condition);
    }

    @Override
    public Object filter(Object obj, JsonProvider jsonProvider, LinkedList<Filter> filters, boolean inArrayContext) {

        Filter filter = filters.poll();

        return filter.doFilter((List<Map<String, Object>>) obj);
    }

    @Override
    public Object filter(Object obj, JsonProvider jsonProvider) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object getRef(Object obj, JsonProvider jsonProvider) {
        throw new UnsupportedOperationException("");
    }

    @Override
    public boolean isArrayFilter() {
        return true;
    }
}
