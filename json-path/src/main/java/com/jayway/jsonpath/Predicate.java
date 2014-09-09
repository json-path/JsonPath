package com.jayway.jsonpath;

/**
 *
 */
public interface Predicate {

    boolean apply(PredicateContext ctx);


    public interface PredicateContext {

        Object target();

        Configuration configuration();
    }
}
