package com.jayway.jsonpath.internal.function.numeric;

import com.jayway.jsonpath.JsonPathException;
import com.jayway.jsonpath.internal.EvaluationContext;
import com.jayway.jsonpath.internal.PathRef;
import com.jayway.jsonpath.internal.function.Parameter;
import com.jayway.jsonpath.internal.function.PathFunction;

import java.util.List;

public class Divide implements PathFunction {
    @Override
    public Object invoke(String currentPath, PathRef parent, Object model, EvaluationContext ctx, List<Parameter> parameters) {
        if (parameters != null && parameters.size() == 2) {
            List<Number> numbers = Parameter.toList(Number.class, ctx, parameters);
            if (numbers.get(1).doubleValue() == 0d) {
                throw new JsonPathException("Arithmetic error: Divide by zero");
            }
            return numbers.get(0).doubleValue()/numbers.get(1).doubleValue();
        }
        throw new JsonPathException("Exactly 2 parameters are expected for divide operator");
    }
}
