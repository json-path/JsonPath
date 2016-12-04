package com.jayway.jsonpath.internal.function;

import com.jayway.jsonpath.InvalidPathException;

import java.util.Map;

public interface JsonPathFunctionFactory {
    /**
     * Returns the function by name or throws InvalidPathException if function not found.
     *
     * @see #FUNCTIONS
     * @see PathFunction
     *
     * @param name
     *      The name of the function
     *
     * @return
     *      The implementation of a function
     *
     * @throws InvalidPathException
     */
    PathFunction newFunction(final String name) throws InvalidPathException;
}

