package com.jayway.jsonpath.internal.function;

import com.jayway.jsonpath.EvaluationContext;
import com.jayway.jsonpath.PathRef;
import com.jayway.jsonpath.spi.function.Parameter;
import com.jayway.jsonpath.spi.function.PathFunction;

import java.util.List;

/**
 * Defines the default behavior which is to return the model that is provided as input as output
 *
 * Created by mattg on 6/26/15.
 */
public class PassthruPathFunction implements PathFunction {

    @Override
    public Object invoke(String currentPath, PathRef parent, Object model, EvaluationContext ctx, List<Parameter> parameters) {
        return model;
    }
}
