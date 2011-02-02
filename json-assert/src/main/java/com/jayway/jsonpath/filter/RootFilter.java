package com.jayway.jsonpath.filter;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: kallestenflo
 * Date: 2/2/11
 * Time: 2:31 PM
 */
public class RootFilter extends JsonPathFilterBase{

    @Override
    public List<Object> apply(List<Object>  filter) {
        return filter;
    }
}
