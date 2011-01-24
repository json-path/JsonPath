package com.jayway.jsonassert.impl;

import java.util.LinkedList;
import java.util.Queue;

/**
 * User: kalle stenflo
 * Date: 1/23/11
 * Time: 1:52 PM
 */
class JSONPath {

    private final Queue<JSONPathFragment> path;

    JSONPath(String path) {
        this.path = compilePathFragments(path);
    }

    boolean hasMoreFragments(){
        return !path.isEmpty();
    }

    JSONPathFragment nextFragment(){
        return path.poll();
    }

    private Queue<JSONPathFragment> compilePathFragments(String path) {
        //TODO: this needs some attention but will work for now
        Queue<JSONPathFragment> processed = new LinkedList<JSONPathFragment>();
        for (String fragment : path.split("[\\.|\\[]")) {
            if (fragment.trim().length() > 0) {

                String compiledFragment = fragment.endsWith("]") ? "[" + fragment : fragment;

                processed.add(new JSONPathFragment(compiledFragment));
            }
        }
        return processed;
    }
}
