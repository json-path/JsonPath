package com.jayway.jsonpath.internal.function.numeric;

import com.jayway.jsonpath.JsonPathException;
import com.jayway.jsonpath.internal.EvaluationContext;
import com.jayway.jsonpath.internal.PathRef;
import com.jayway.jsonpath.internal.function.Parameter;
import com.jayway.jsonpath.internal.function.PathFunction;

import java.util.List;

/**
 * Created by soberich on 12/12/18.
 */
public class Div implements PathFunction {

    @Override
    public Object invoke(String currentPath, PathRef parent, Object model, EvaluationContext ctx, List<Parameter> parameters) {
        Double result;
        if (parameters != null && parameters.size() == 2) {

            List<Number> numbers = Parameter.toList(Number.class, ctx, parameters);
            double divisor = numbers.get(1).doubleValue();
            if (divisor == 0d) {
                throw new JsonPathException("Division by zero. Infinity is not a valid double value as per JSON specification");
            }
            double dividend = numbers.get(0).doubleValue();
            result = dividend / divisor;
            return result;
        }
        throw new JsonPathException("Division function attempted to calculate value using other than 2 arguments");
    }
}
