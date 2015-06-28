package com.jayway.jsonpath.internal.function;

import com.jayway.jsonpath.Function;
import com.jayway.jsonpath.InvalidPathException;
import com.jayway.jsonpath.internal.function.numeric.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Implements a factory that given a name of the function will return the Function implementation, or null
 * if the value is not obtained.
 *
 * Leverages the function's name in order to determine which function to execute which is maintained internally
 * here via a static map
 *
 * Created by mattg on 6/27/15.
 */
public class FunctionFactory {

    public static final Map<String, Class> FUNCTIONS;

    static {
        // New functions should be added here and ensure the name is not overridden
        Map<String, Class> map = new HashMap<String, Class>();

        // Math Functions
        map.put("avg", Average.class);
        map.put("stddev", StandardDeviation.class);
        map.put("sum", Sum.class);
        map.put("min", Min.class);
        map.put("max", Max.class);

        // JSON Entity Functions
        map.put("length", Length.class);

        FUNCTIONS = Collections.unmodifiableMap(map);
    }

    /**
     * Either provides a pass thru function when the function cannot be properly mapped or otherwise returns the function
     * implementation based on the name using the internal FUNCTION map
     *
     * @see #FUNCTIONS
     * @see Function
     *
     * @param pathFragment
     *      The path fragment that is currently being processed which is believed to be the name of a function
     *
     * @return
     *      The implementation of a function
     *
     * @throws InvalidPathException
     */
    public static Function newFunction(String pathFragment) throws InvalidPathException {
        Function result = new PassthruFunction();
        if (null != pathFragment) {
            String name = pathFragment.replaceAll("['%\\]\\[\\(\\)]", "").trim().toLowerCase();

            if (null != name && FUNCTIONS.containsKey(name) && Function.class.isAssignableFrom(FUNCTIONS.get(name))) {
                try {
                    result = (Function)FUNCTIONS.get(name).newInstance();
                } catch (InstantiationException e) {
                    throw new InvalidPathException("Function of name: " + name + " cannot be created", e);
                } catch (IllegalAccessException e) {
                    throw new InvalidPathException("Function of name: " + name + " cannot be created", e);
                }
            }
        }
        return result;

    }
}
