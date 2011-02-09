package com.jayway.jsonpath.filter;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: kallestenflo
 * Date: 2/2/11
 * Time: 2:01 PM
 */
public abstract class JsonPathFilterBase {
    public abstract FilterOutput apply(FilterOutput filterItems);
}
