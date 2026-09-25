package com.jayway.jsonpath.spi.function;

import com.jayway.jsonpath.InvalidPathException;

/**
 * Service provider interface for resolving {@link PathFunction} implementations.
 */
public interface FunctionProvider {

    /**
     * Look up or instantiate a {@link PathFunction} by name.
     *
     * @param name the name of the function
     * @return the function instance
     * @throws InvalidPathException if function is not found or cannot be instantiated
     */
    PathFunction getFunction(String name) throws InvalidPathException;

    /**
     * Checks if a function with the specified name exists in this provider.
     *
     * @param name the name of the function
     * @return true if function is known to this provider
     */
    boolean hasFunction(String name);
}
