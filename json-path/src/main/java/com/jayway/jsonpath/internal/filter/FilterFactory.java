/*
 * Copyright 2011 the original author or authors.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.jayway.jsonpath.internal.filter;

/**
 * @author Kalle Stenflo
 */
public class FilterFactory {

    private final static Filter DOCUMENT_FILTER = new PassthroughFilter("$", false);
    private final static Filter ALL_ARRAY_ITEMS_FILTER = new PassthroughFilter("[*]", true);
    private final static Filter WILDCARD_FILTER = new WildcardFilter("*");
    private final static Filter SCAN_FILTER = new ScanFilter("..");

    public static Filter createFilter(String pathFragment) {

        if ("$".equals(pathFragment)) {

            return DOCUMENT_FILTER;

        } else if("[*]".equals(pathFragment)){

            return ALL_ARRAY_ITEMS_FILTER;

        } else if ("*".equals(pathFragment) || "['*']".equals(pathFragment)) {

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
