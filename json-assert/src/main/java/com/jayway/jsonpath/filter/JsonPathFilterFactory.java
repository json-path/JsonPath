package com.jayway.jsonpath.filter;

import java.util.regex.Pattern;

/**
 * User: kallestenflo
 * Date: 2/2/11
 * Time: 2:03 PM
 */
public class JsonPathFilterFactory {

    private static final Pattern ROOT_FILTER     = Pattern.compile("\\$");
    private static final Pattern PROPERTY_FILTER = Pattern.compile("\\w+");
    private static final Pattern WILDCARD_PROPERTY_FILTER = Pattern.compile("\\*");
    private static final Pattern LIST_FILTER = Pattern.compile("\\[.*?\\]");
    private static final Pattern TRAVERSE_FILTER = Pattern.compile("\\.\\.");

    public static JsonPathFilterBase createFilter(String pathFragment){


        if(ROOT_FILTER.matcher(pathFragment).matches()){
            return new RootFilter();
        }
        else if(PROPERTY_FILTER.matcher(pathFragment).matches() || WILDCARD_PROPERTY_FILTER.matcher(pathFragment).matches() ){
            return new PropertyFilter(pathFragment);
        }
        else if(LIST_FILTER.matcher(pathFragment).matches()){
            return new ListFilter(pathFragment);
        }
        else if(TRAVERSE_FILTER.matcher(pathFragment).matches()){
            return new TraverseFilter(pathFragment);
        }

        return null;
    }

}
