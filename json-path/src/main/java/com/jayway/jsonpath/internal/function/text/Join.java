package com.jayway.jsonpath.internal.function.text;

import com.jayway.jsonpath.JsonPathException;
import com.jayway.jsonpath.internal.EvaluationContext;
import com.jayway.jsonpath.internal.PathRef;
import com.jayway.jsonpath.internal.function.Parameter;
import com.jayway.jsonpath.internal.function.PathFunction;

import java.util.Iterator;
import java.util.List;

/**
 * Provides the join of a JSONArray Object
 *
 * Created by fbrissi on 6/26/15.
 */
public class Join implements PathFunction {

    @Override
    public Object invoke(String currentPath, PathRef parent, Object model, EvaluationContext ctx, List<Parameter> parameters) {
        StringBuilder builder = new StringBuilder();
        Parameter path = null != parameters && parameters.size() > 1 ? parameters.get(1) : null;
        String delimiter = null != parameters && parameters.size() > 0 ? parameters.get(0).getValue().toString() : ",";
        if (ctx.configuration().jsonProvider().isArray(model)) {
            Iterator<?> iterator = ctx.configuration().jsonProvider().toIterable(model).iterator();
            while (iterator.hasNext()) {
                Object innerModel = null == path ? iterator.next()
                        : path.getPath().evaluate(iterator.next(), model, ctx.configuration()).getValue();
                if (ctx.configuration().jsonProvider().isArray(innerModel)) {
                    Iterable<?> iterable = ctx.configuration().jsonProvider().toIterable(innerModel);
                    builder.append(iterable.iterator().next().toString());
                } else {
                    builder.append(innerModel.toString());
                }
                if (iterator.hasNext()) {
                    builder.append(delimiter);
                }
            }
            return builder.toString();
        }
        throw new JsonPathException(String.format("join operation cannot be applied to %s", model.getClass()));
    }

}
