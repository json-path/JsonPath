package com.jayway.jsonpath.spi.pathFunction;

import com.jayway.jsonpath.InvalidPathException;
import com.jayway.jsonpath.internal.function.json.Append;
import com.jayway.jsonpath.internal.function.json.KeySetFunction;
import com.jayway.jsonpath.internal.function.numeric.*;
import com.jayway.jsonpath.internal.function.sequence.First;
import com.jayway.jsonpath.internal.function.sequence.Index;
import com.jayway.jsonpath.internal.function.sequence.Last;
import com.jayway.jsonpath.internal.function.text.Concatenate;
import com.jayway.jsonpath.internal.function.text.Length;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class DefaultPathFunctionProvider implements PathFunctionProvider {
    public static final Map<String, Class<? extends PathFunction>> DEFAULT_FUNCTIONS;

    static {
        // New functions should be added here and ensure the name is not overridden
        Map<String, Class<? extends PathFunction>> map = new HashMap<>();

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


        DEFAULT_FUNCTIONS = Collections.unmodifiableMap(map);
    }

    @Override
    public PathFunction newFunction(String name) throws InvalidPathException {
        Class<? extends PathFunction> functionClazz = DEFAULT_FUNCTIONS.get(name);
        if(functionClazz == null){
            throw new InvalidPathException("Function with name: " + name + " does not exist.");
        } else {
            try {
                return functionClazz.getDeclaredConstructor().newInstance();
            } catch (Exception e) {
                throw new InvalidPathException("Function of name: " + name + " cannot be created", e);
            }
        }
    }
}
