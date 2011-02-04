package com.jayway.jsonpath.filter;

import java.util.regex.Pattern;

/**
 * User: kallestenflo
 * Date: 2/2/11
 * Time: 2:03 PM
 */
public class JsonPathFilterFactory {

    private final static Pattern ROOT_FILTER_PATTERN = Pattern.compile("\\$");
    private final static Pattern PROPERTY_FILTER_PATTERN = Pattern.compile("(\\w+)|\\['(\\w+)'\\]");
    //private final static Pattern PROPERTY_FILTER_PATTERN = Pattern.compile("\\w+");
    private final static Pattern WILDCARD_PROPERTY_FILTER_PATTERN = Pattern.compile("\\*");
    private final static Pattern LIST_FILTER_PATTERN = Pattern.compile("\\[.*?\\]");
    private final static Pattern TRAVERSE_FILTER_PATTERN = Pattern.compile("\\.\\.");


    private final static RootFilter ROOT_FILTER = new RootFilter();
    private final static TraverseFilter TRAVERSE_FILTER = new TraverseFilter();

    public static JsonPathFilterBase createFilter(String pathFragment){


        if(ROOT_FILTER_PATTERN.matcher(pathFragment).matches()){
            return ROOT_FILTER;
        }
        else if(PROPERTY_FILTER_PATTERN.matcher(pathFragment).matches() || WILDCARD_PROPERTY_FILTER_PATTERN.matcher(pathFragment).matches() ){
            return new PropertyFilter(pathFragment);
        }
        else if(LIST_FILTER_PATTERN.matcher(pathFragment).matches()){
            return new ListFilter(pathFragment);
        }
        else if(TRAVERSE_FILTER_PATTERN.matcher(pathFragment).matches()){
            return TRAVERSE_FILTER;
        }

        return null;
    }

}
