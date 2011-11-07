package com.jayway.jsonpath.reader;

import com.jayway.jsonpath.reader.filter.Filter;
import com.jayway.jsonpath.reader.filter.FilterFactory;

/**
 * Created by IntelliJ IDEA.
 * User: kallestenflo
 * Date: 11/4/11
 * Time: 10:00 PM
 */
public class PathToken {

    private Filter filter;

    public PathToken(String pathFragment) {
        filter = FilterFactory.createFilter(pathFragment);
    }

    public Object filter(Object model){
        return filter.filter(model);
    }
}
