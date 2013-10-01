package com.jayway.jsonpath;

/**
 *
 */
public interface Predicate {

    boolean apply(Object target, Configuration configuration);
}
