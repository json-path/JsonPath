package com.jayway.jsonpath.internal.function.sequence;

import com.jayway.jsonpath.JsonPathException;
import com.jayway.jsonpath.internal.EvaluationContext;
import com.jayway.jsonpath.internal.PathRef;
import com.jayway.jsonpath.internal.function.Parameter;
import com.jayway.jsonpath.internal.function.PathFunction;

import java.util.ArrayList;
import java.util.List;

/**
 * Defines the pattern for taking item from collection of JSONArray by index
 *
 * Created by git9527 on 6/11/22.
 */
public abstract class AbstractSequenceAggregation implements PathFunction {
    
    protected abstract int targetIndex(EvaluationContext ctx, List<Parameter> parameters);
    
    @Override
    public Object invoke(String currentPath, PathRef parent, Object model, EvaluationContext ctx, List<Parameter> parameters) {
        if(ctx.configuration().jsonProvider().isArray(model)){

            Iterable<?> objects = ctx.configuration().jsonProvider().toIterable(model);
            List<Object> objectList = new ArrayList<>();
            objects.forEach(objectList::add);
            int targetIndex = this.targetIndex(ctx, parameters);
            if (targetIndex >= 0) {
                return objectList.get(targetIndex);
            } else {
                int realIndex = objectList.size() + targetIndex;
                if (realIndex > 0) {
                    return objectList.get(realIndex);
                } else {
                    throw new JsonPathException("Target index:" + targetIndex + " larger than object count:" + objectList.size());
                }
            }
        }
        throw new JsonPathException("Aggregation function attempted to calculate value using empty array");
    }
    
    protected int getIndexFromParameters(EvaluationContext ctx, List<Parameter> parameters) {
        List<Number> numbers = Parameter.toList(Number.class, ctx, parameters);
        return numbers.get(0).intValue();
    }
}
