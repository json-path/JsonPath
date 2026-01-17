package com.jayway.jsonpath.spi.function;

import com.jayway.jsonpath.InvalidPathException;

public interface PathFunctionProvider {

    /**
     * Returns the function by name or throws InvalidPathException if function not found.
     *
     * @see DefaultPathFunctionProvider#DEFAULT_FUNCTIONS
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
    public PathFunction newFunction(String name) throws InvalidPathException;
}
