package com.jayway.jsonpath.reader.filter;

/**
 * Created by IntelliJ IDEA.
 * User: kallestenflo
 * Date: 11/4/11
 * Time: 10:13 PM
 */
public class FilterFactory {

    private final static Filter DOCUMENT_FILTER = new PassThrewFilter("$");
    private final static Filter ALL_ARRAY_ITEMS_FILTER = new PassThrewFilter("[*]");
    private final static Filter WILDCARD_FILTER = new WildcardFilter("*");
    private final static Filter SCAN_FILTER = new ScanFilter("..");

    public static Filter createFilter(String pathFragment) {

        if ("$".equals(pathFragment)) {

            return DOCUMENT_FILTER;

        } else if("[*]".equals(pathFragment)){

            return ALL_ARRAY_ITEMS_FILTER;

        } else if ("*".equals(pathFragment)) {

            return WILDCARD_FILTER;

        } else if (pathFragment.contains("..")) {

            return SCAN_FILTER;

        } else if (!pathFragment.contains("[")) {

            return new FieldFilter(pathFragment);

        } else if (pathFragment.contains("[")) {

            if (pathFragment.startsWith("[?")) {
                if(!pathFragment.contains("=") && !pathFragment.contains("<") && !pathFragment.contains(">")){
                    //[?(@.isbn)]
                    return new HasFieldFilter(pathFragment);
                } else {
                    //[?(@.name='foo')]
                    return new ArrayEvalFilter(pathFragment);
                }
            } else {
                //[0]
                //[0,1, ...]
                //[-1:]
                //[:1]
                return new ArrayIndexFilter(pathFragment);
            }
        }

        throw new UnsupportedOperationException("..");

    }



}
