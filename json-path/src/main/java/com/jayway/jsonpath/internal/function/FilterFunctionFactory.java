package com.jayway.jsonpath.internal.function;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.jayway.jsonpath.Filter;
import com.jayway.jsonpath.InvalidPathException;

/**
 * Implements a factory that given a name of the function will return the Function implementation, or null
 * if the value is not obtained.
 *
 * Leverages the function's name in order to determine which function to execute which is maintained internally
 * here via a static map
 *
 */
public class FilterFunctionFactory {

    public static final Map<String, Class<? extends FilterFunction>> FUNCTIONS;

    static {
        // New functions should be added here and ensure the name is not overridden
        Map<String, Class<? extends FilterFunction>> map = new HashMap<String, Class<? extends FilterFunction>>();

        // Math Functions
        map.put("child", Child.class);
        
        FUNCTIONS = Collections.unmodifiableMap(map);
    }

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
    public static FilterFunction newFunction(String name) throws InvalidPathException {
        Class<? extends FilterFunction> functionClazz = FUNCTIONS.get(name);
        if(functionClazz == null){
            throw new InvalidPathException("Function with name: " + name + " does not exist.");
        } else {
            try {
                return functionClazz.newInstance();
            } catch (Exception e) {
                throw new InvalidPathException("Function of name: " + name + " cannot be created", e);
            }
        }
    }
}
