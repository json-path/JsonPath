package com.jayway.jsonpath;

import com.jayway.jsonpath.internal.function.PathFunction;

import java.util.Collections;
import java.util.Map;

public class PathFunctionFactory {

	private static boolean initialized = false;

	/**
	 * by default, we add th common path functions
	 */
	private static Map<String, Class> functions = com.jayway.jsonpath.internal.function.PathFunctionFactory.getFunctions();

	/**
	 * Add a single custom path function
	 * Should only be done when your application is being initialized
	 *
	 * @param name  The name of the function
	 * @param clazz Class of a function
	 * @throws InvalidPathException
	 */
	public static void add(String name, Class clazz) throws InvalidPathException {
		assertNotInitialized();
		if (functions.containsKey(name)) {
			throw new InvalidPathException("Path function with name: " + name + " already exists");
		}

		functions.put(name, clazz);
	}

	/**
	 * Add a map custom path function
	 * Should only be done when your application is being initialized
	 *
	 * @param functionMap The name of the function as key, Class of a function as Value
	 * @throws InvalidPathException
	 */
	public static void add(Map<String, Class> functionMap) throws InvalidPathException {
		for (Map.Entry<String, Class> entry : functionMap.entrySet()) {
			add(entry.getKey(), entry.getValue());
		}
	}

	/**
	 * Should only your application initialized to
	 */
	public static void init() {
		if (initialized) {
			return;
		}
		functions.putAll(com.jayway.jsonpath.internal.function.PathFunctionFactory.getFunctions());
		functions = Collections.unmodifiableMap(functions);
		initialized = true;
	}

	public static PathFunction newFunction(String name) throws InvalidPathException {
		// Lazy init
		init();

		Class functionClazz = functions.get(name);
		if (functionClazz == null) {
			throw new InvalidPathException("Function with name: " + name + " does not exist.");
		} else {
			try {
				return (PathFunction) functionClazz.newInstance();
			} catch (Exception e) {
				throw new InvalidPathException("Function of name: " + name + " cannot be created", e);
			}
		}
	}

	private static void assertNotInitialized() {
		if (initialized) {
			throw new InvalidPathException("Can not change path function factory after it is initialized");
		}
	}
}
