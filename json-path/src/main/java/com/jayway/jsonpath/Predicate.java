package com.jayway.jsonpath;

import com.jayway.jsonpath.spi.mapper.MappingException;

/**
 *
 */
public interface Predicate {

    boolean apply(PredicateContext ctx);

    public interface PredicateContext {

        /**
         * Returns the current item being evaluated by this predicate
         * @return current document
         */
        Object item();

        /**
         * Returns the current item being evaluated by this predicate. It will be mapped
         * to the provided class
         * @return current document
         */
        <T> T item(Class<T> clazz) throws MappingException;

        /**
         * Returns the root document (the complete JSON)
         * @return root document
         */
        Object root();

        /**
         * Configuration to use when evaluating
         * @return configuration
         */
        Configuration configuration();
    }
}
