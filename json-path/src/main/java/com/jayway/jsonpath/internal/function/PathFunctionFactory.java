package com.jayway.jsonpath.internal.function;

import com.jayway.jsonpath.InvalidPathException;
import com.jayway.jsonpath.internal.function.json.Append;
import com.jayway.jsonpath.internal.function.json.KeySetFunction;
import com.jayway.jsonpath.internal.function.numeric.Average;
import com.jayway.jsonpath.internal.function.numeric.Max;
import com.jayway.jsonpath.internal.function.numeric.Min;
import com.jayway.jsonpath.internal.function.numeric.StandardDeviation;
import com.jayway.jsonpath.internal.function.numeric.Sum;
import com.jayway.jsonpath.internal.function.sequence.First;
import com.jayway.jsonpath.internal.function.sequence.Index;
import com.jayway.jsonpath.internal.function.sequence.Last;
import com.jayway.jsonpath.internal.function.text.Concatenate;
import com.jayway.jsonpath.internal.function.text.Length;
import com.jayway.jsonpath.spi.function.PathFunction;

import java.lang.reflect.Constructor;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Implements a factory that given a name of the function will return the Function implementation, or null
 * if the value is not obtained.
 *
 * Leverages the function's name in order to determine which function to execute which is maintained internally
 * here via a static map. Also supports global programmatic registration of custom functions.
 */
public class PathFunctionFactory {

    public static final Map<String, Class> FUNCTIONS;
    private static final Map<String, Object> REGISTERED_FUNCTIONS = new ConcurrentHashMap<String, Object>();

    static {
        // New functions should be added here and ensure the name is not overridden
        Map<String, Class> map = new HashMap<String, Class>();

        // Math Functions
        map.put("avg", Average.class);
        map.put("stddev", StandardDeviation.class);
        map.put("sum", Sum.class);
        map.put("min", Min.class);
        map.put("max", Max.class);

        // Text Functions
        map.put("concat", Concatenate.class);

        // JSON Entity Functions
        map.put(Length.TOKEN_NAME, Length.class);
        map.put("size", Length.class);
        map.put("append", Append.class);
        map.put("keys", KeySetFunction.class);
        
        // Sequential Functions
        map.put("first", First.class);
        map.put("last", Last.class);
        map.put("index", Index.class);

        FUNCTIONS = Collections.unmodifiableMap(map);
    }

    /**
     * Registers a custom function globally by class.
     *
     * @param name The name of the function
     * @param functionClazz The class implementing PathFunction
     */
    public static void registerFunction(String name, Class<? extends PathFunction> functionClazz) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Function name cannot be null or empty");
        }
        if (functionClazz == null) {
            throw new IllegalArgumentException("Function class cannot be null");
        }
        REGISTERED_FUNCTIONS.put(name, functionClazz);
    }

    /**
     * Registers a custom function globally by instance.
     *
     * @param name The name of the function
     * @param function The PathFunction instance
     */
    public static void registerFunction(String name, PathFunction function) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Function name cannot be null or empty");
        }
        if (function == null) {
            throw new IllegalArgumentException("Function instance cannot be null");
        }
        REGISTERED_FUNCTIONS.put(name, function);
    }

    /**
     * Unregisters a globally registered function.
     *
     * @param name The name of the function to unregister
     * @return true if function was unregistered, false otherwise
     */
    public static boolean unregisterFunction(String name) {
        if (name == null) {
            return false;
        }
        return REGISTERED_FUNCTIONS.remove(name) != null;
    }

    /**
     * Checks if a function with the specified name exists (either registered or built-in).
     *
     * @param name The name of the function
     * @return true if function exists
     */
    public static boolean hasFunction(String name) {
        if (name == null) {
            return false;
        }
        return REGISTERED_FUNCTIONS.containsKey(name) || FUNCTIONS.containsKey(name);
    }

    /**
     * Checks if a custom function with the specified name was globally registered.
     *
     * @param name The name of the function
     * @return true if function is registered in custom functions
     */
    public static boolean isRegistered(String name) {
        if (name == null) {
            return false;
        }
        return REGISTERED_FUNCTIONS.containsKey(name);
    }

    /**
     * Clears all globally registered custom functions.
     * Built-in functions are not affected.
     */
    public static void clearRegisteredFunctions() {
        REGISTERED_FUNCTIONS.clear();
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
    public static PathFunction newFunction(String name) throws InvalidPathException {
        if (name == null) {
            throw new InvalidPathException("Function name cannot be null");
        }
        Object registered = REGISTERED_FUNCTIONS.get(name);
        if (registered != null) {
            if (registered instanceof PathFunction) {
                return (PathFunction) registered;
            } else if (registered instanceof Class) {
                try {
                    Class<?> clazz = (Class<?>) registered;
                    Constructor<?> ctor = clazz.getDeclaredConstructor();
                    ctor.setAccessible(true);
                    return (PathFunction) ctor.newInstance();
                } catch (Exception e) {
                    throw new InvalidPathException("Function of name: " + name + " cannot be created", e);
                }
            }
        }

        Class functionClazz = FUNCTIONS.get(name);
        if(functionClazz == null){
            throw new InvalidPathException("Function with name: " + name + " does not exist.");
        } else {
            try {
                Constructor<?> ctor = functionClazz.getDeclaredConstructor();
                ctor.setAccessible(true);
                return (PathFunction) ctor.newInstance();
            } catch (Exception e) {
                throw new InvalidPathException("Function of name: " + name + " cannot be created", e);
            }
        }
    }
}
