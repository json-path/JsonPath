package com.jayway.jsonpath.reader.filter;

import com.jayway.jsonpath.filter.ListEvalFilter;

/**
 * Created by IntelliJ IDEA.
 * User: kallestenflo
 * Date: 11/4/11
 * Time: 10:13 PM
 */
public class FilterFactory {

    public static Filter createFilter(String pathFragment) {

        if ("$".equals(pathFragment) || "[*]".equals(pathFragment)) {

            return new PassThrewFilter(pathFragment);

        } else if ("*".equals(pathFragment)) {

            return new WildcardFilter(pathFragment);

        } else if (pathFragment.contains("..")) {

            return new ScanFilter(pathFragment);

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
