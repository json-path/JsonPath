package com.jayway.jsonpath.spi.function;

import com.jayway.jsonpath.InvalidPathException;
import com.jayway.jsonpath.internal.function.PathFunctionFactory;

import java.lang.reflect.Constructor;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Default implementation of {@link FunctionProvider} that allows instance-level function registrations
 * and falls back to {@link PathFunctionFactory} for globally registered and built-in functions.
 */
public class DefaultFunctionProvider implements FunctionProvider {

    public static final DefaultFunctionProvider INSTANCE = new DefaultFunctionProvider(Collections.<String, Object>emptyMap());

    private final Map<String, Object> functions;

    public DefaultFunctionProvider() {
        this(Collections.<String, Object>emptyMap());
    }

    public DefaultFunctionProvider(Map<String, ?> customFunctions) {
        this.functions = new HashMap<String, Object>();
        if (customFunctions != null) {
            for (Map.Entry<String, ?> entry : customFunctions.entrySet()) {
                register(entry.getKey(), entry.getValue());
            }
        }
    }

    /**
     * Registers a custom function by class.
     *
     * @param name the name to bind the function to
     * @param functionClass the class implementing {@link PathFunction}
     * @return this provider for chaining
     */
    public DefaultFunctionProvider register(String name, Class<? extends PathFunction> functionClass) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Function name cannot be null or empty");
        }
        if (functionClass == null) {
            throw new IllegalArgumentException("Function class cannot be null");
        }
        this.functions.put(name, functionClass);
        return this;
    }

    /**
     * Registers a custom function by instance.
     *
     * @param name the name to bind the function to
     * @param function the function instance
     * @return this provider for chaining
     */
    public DefaultFunctionProvider register(String name, PathFunction function) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Function name cannot be null or empty");
        }
        if (function == null) {
            throw new IllegalArgumentException("Function instance cannot be null");
        }
        this.functions.put(name, function);
        return this;
    }

    private void register(String name, Object functionOrClass) {
        if (functionOrClass instanceof Class) {
            @SuppressWarnings("unchecked")
            Class<? extends PathFunction> clazz = (Class<? extends PathFunction>) functionOrClass;
            register(name, clazz);
        } else if (functionOrClass instanceof PathFunction) {
            register(name, (PathFunction) functionOrClass);
        } else {
            throw new IllegalArgumentException("Function must be a Class<? extends PathFunction> or PathFunction instance");
        }
    }

    /**
     * Unregisters a function from this provider instance.
     *
     * @param name the name of the function
     * @return true if function was removed, false otherwise
     */
    public boolean unregister(String name) {
        if (name == null) {
            return false;
        }
        return this.functions.remove(name) != null;
    }

    @Override
    public PathFunction getFunction(String name) throws InvalidPathException {
        if (name == null) {
            throw new InvalidPathException("Function name cannot be null");
        }
        Object registered = functions.get(name);
        if (registered != null) {
            return createFunctionInstance(name, registered);
        }
        return PathFunctionFactory.newFunction(name);
    }

    @Override
    public boolean hasFunction(String name) {
        if (name == null) {
            return false;
        }
        return functions.containsKey(name) || PathFunctionFactory.hasFunction(name);
    }

    /**
     * Returns an unmodifiable view of custom functions defined directly in this provider.
     */
    public Map<String, Object> getCustomFunctions() {
        return Collections.unmodifiableMap(new HashMap<String, Object>(functions));
    }

    private static PathFunction createFunctionInstance(String name, Object registered) throws InvalidPathException {
        if (registered instanceof PathFunction) {
            return (PathFunction) registered;
        } else if (registered instanceof Class) {
            try {
                Class<?> clazz = (Class<?>) registered;
                Constructor<?> constructor = clazz.getDeclaredConstructor();
                constructor.setAccessible(true);
                return (PathFunction) constructor.newInstance();
            } catch (Exception e) {
                throw new InvalidPathException("Function of name: " + name + " cannot be created", e);
            }
        }
        throw new InvalidPathException("Function with name: " + name + " does not exist.");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DefaultFunctionProvider that = (DefaultFunctionProvider) o;
        return Objects.equals(functions, that.functions);
    }

    @Override
    public int hashCode() {
        return Objects.hash(functions);
    }
}
