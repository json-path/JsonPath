package com.jayway.jsonpath.filter;

import java.util.regex.Pattern;

/**
 * Created by IntelliJ IDEA.
 * User: kallestenflo
 * Date: 2/15/11
 * Time: 8:09 PM
 */
public class ListWildcardFilter extends JsonPathFilterBase{

    public static final Pattern PATTERN = Pattern.compile("\\[\\*\\]");

    @Override
    public FilterOutput apply(FilterOutput filterItems) {
        return new FilterOutput(filterItems.getResultAsList());
    }
}
