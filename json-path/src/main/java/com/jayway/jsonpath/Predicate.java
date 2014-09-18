package com.jayway.jsonpath;

/**
 *
 */
public interface Predicate {

    boolean apply(PredicateContext ctx);

    public interface PredicateContext {

        /**
         * Returns the current element being evaluated by this predicate
         * @return current document
         */
        Object contextDocument();

        /**
         * Returns the root document (the complete JSON)
         * @return root document
         */
        Object rootDocument();

        /**
         * Configuration to use when evaluating
         * @return configuration
         */
        Configuration configuration();
    }
}
