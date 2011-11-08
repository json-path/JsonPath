package com.jayway.jsonpath.reader;

import com.jayway.jsonpath.reader.filter.FilterFactory;
import com.jayway.jsonpath.spi.JsonProvider;

/**
 * Created by IntelliJ IDEA.
 * User: kallestenflo
 * Date: 11/4/11
 * Time: 10:00 PM
 */
public class PathToken {

    private String fragment;

    public PathToken(String fragment) {
        this.fragment = fragment;
    }

    public Object filter(Object model, JsonProvider jsonProvider){
        return FilterFactory.createFilter(fragment).filter(model, jsonProvider);
    }

    public String getFragment() {
        return fragment;
    }
}
