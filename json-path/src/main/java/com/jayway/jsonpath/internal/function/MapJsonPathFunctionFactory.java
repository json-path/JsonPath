package com.jayway.jsonpath.internal.function;

import com.jayway.jsonpath.InvalidPathException;

import java.util.Map;

public class MapJsonPathFunctionFactory implements JsonPathFunctionFactory {

    private final Map<String, Class<? extends PathFunction>> pathFunctionMap;

    public MapJsonPathFunctionFactory(
            final Map<String, Class<? extends PathFunction>> pathFunctionMap) {

        this.pathFunctionMap = pathFunctionMap;
    }

    @Override
    public PathFunction newFunction(final String name) throws InvalidPathException {
        Class functionClazz = pathFunctionMap.get(name);
        if(functionClazz == null){
            throw new InvalidPathException("Function with name: " + name + " does not exists.");
        } else {
            try {
                return (PathFunction)functionClazz.newInstance();
            } catch (Exception e) {
                throw new InvalidPathException("Function of name: " + name + " cannot be created", e);
            }
        }
    }
}
