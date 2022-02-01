package com.jayway.jsonpath.internal.function.text;

import com.jayway.jsonpath.InvalidJsonException;
import com.jayway.jsonpath.internal.EvaluationContext;
import com.jayway.jsonpath.internal.PathRef;
import com.jayway.jsonpath.internal.function.Parameter;
import com.jayway.jsonpath.internal.function.PathFunction;

import java.util.List;

/**
 * Parses a string field containing a serialized JSON object into the object itself.
 */
public class Parse implements PathFunction {
    @Override
    public Object invoke(String currentPath, PathRef parent, Object model, EvaluationContext ctx, List<Parameter> parameters) {
        final Object unwrapped = ctx.configuration().jsonProvider().unwrap(model);
        if (unwrapped instanceof String) {
            try {
                return ctx.configuration().jsonProvider().parse((String) unwrapped);
            } catch (InvalidJsonException e) {
                throw new InvalidJsonException(String.format("String property at path %s did not parse as valid JSON", currentPath), e);
            }
        }
        return model;
    }
}
