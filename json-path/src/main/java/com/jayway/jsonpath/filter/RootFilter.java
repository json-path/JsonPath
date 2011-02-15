package com.jayway.jsonpath.filter;

import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by IntelliJ IDEA.
 * User: kallestenflo
 * Date: 2/2/11
 * Time: 2:31 PM
 */
public class RootFilter extends JsonPathFilterBase{

    public final static Pattern PATTERN = Pattern.compile("\\$");

    @Override
    public FilterOutput apply(FilterOutput  root) {
        return root;
    }
}
