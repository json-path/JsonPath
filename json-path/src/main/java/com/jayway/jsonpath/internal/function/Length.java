package com.jayway.jsonpath.internal.function;

import com.jayway.jsonpath.Function;
import com.jayway.jsonpath.internal.EvaluationContext;
import com.jayway.jsonpath.internal.PathRef;
import net.minidev.json.JSONArray;

import java.util.*;

/**
 * Provides the length of a JSONArray Object
 *
 * Created by mattg on 6/26/15.
 */
public class Length implements Function {

    @Override
    public Object invoke(String currentPath, PathRef parent, Object model, EvaluationContext ctx) {
        if(ctx.configuration().jsonProvider().isArray(model)){
            return ctx.configuration().jsonProvider().length(model);
        } else if(ctx.configuration().jsonProvider().isMap(model)){
            return ctx.configuration().jsonProvider().length(model);
        }
        return null;
    }
}