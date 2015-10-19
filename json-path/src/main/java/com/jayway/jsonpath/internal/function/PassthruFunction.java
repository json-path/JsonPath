package com.jayway.jsonpath.internal.function;

import com.jayway.jsonpath.Function;
import com.jayway.jsonpath.internal.EvaluationContext;
import com.jayway.jsonpath.internal.PathRef;

/**
 * Defines the default behavior which is to return the model that is provided as input as output
 *
 * Created by mattg on 6/26/15.
 */
public class PassthruFunction implements Function {

    @Override
    public Object invoke(String currentPath, PathRef parent, Object model, EvaluationContext ctx) {
        return model;
    }
}
