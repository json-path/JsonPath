package com.jayway.jsonpath.filter;

/**
 * User: kallestenflo
 * Date: 2/2/11
 * Time: 2:03 PM
 */
public class JsonPathFilterFactory {

    public static JsonPathFilterBase createFilter(String pathFragment) {

        if (RootFilter.PATTERN.matcher(pathFragment).matches()) {
            return new RootFilter();
        } else if (ListIndexFilter.PATTERN.matcher(pathFragment).matches()) {
            return new ListIndexFilter(pathFragment);
        } else if (ListFrontFilter.PATTERN.matcher(pathFragment).matches()) {
            return new ListFrontFilter(pathFragment);
        } else if (ListWildcardFilter.PATTERN.matcher(pathFragment).matches()) {
            return new ListWildcardFilter();
        } else if (ListTailFilter.PATTERN.matcher(pathFragment).matches()) {
            return new ListTailFilter(pathFragment);
        } else if (ListPropertyFilter.PATTERN.matcher(pathFragment).matches()) {
            return new ListPropertyFilter(pathFragment);
        } else if (ListEvalFilter.PATTERN.matcher(pathFragment).matches()) {
            return new ListEvalFilter(pathFragment);
        } else if (TraverseFilter.PATTERN.matcher(pathFragment).matches()) {
            return new TraverseFilter();
        } else if (WildcardPropertyFilter.PATTERN.matcher(pathFragment).matches()) {
            return new WildcardPropertyFilter();
        }
        else if (TypeFilter.PATTERN.matcher(pathFragment).matches()) {
            return new TypeFilter(pathFragment);
        }else if (PropertyFilter.PATTERN.matcher(pathFragment).matches()) {
        	return new PropertyFilter(pathFragment);
        }
        return null;


    }

}
