package com.jayway.jsonpath.internal.function.numeric;

import com.jayway.jsonpath.internal.EvaluationContext;
import com.jayway.jsonpath.internal.PathRef;
import com.jayway.jsonpath.internal.function.Function;

/**
 * Defines the pattern for processing numerical values via an abstract implementation that iterates over the collection
 * of JSONArray entities and verifies that each is a numerical value and then passes that along the abstract methods
 *
 *
 * Created by mattg on 6/26/15.
 */
public abstract class AbstractAggregation implements Function {

    /**
     * Defines the next value in the array to the mathmatical function
     *
     * @param value
     *      The numerical value to process next
     */
    protected abstract void next(Number value);

    /**
     * Obtains the value generated via the series of next value calls
     *
     * @return
     *      A numerical answer based on the input value provided
     */
    protected abstract Number getValue();

    @Override
    public Object invoke(String currentPath, PathRef parent, Object model, EvaluationContext ctx) {
        if(ctx.configuration().jsonProvider().isArray(model)){

            Iterable<?> objects = ctx.configuration().jsonProvider().toIterable(model);
            for (Object obj : objects) {
//                Object unwraped = ctx.configuration().jsonProvider().unwrap(obj);
//                if (unwraped instanceof Number) {
//                    Number value = (Number) unwraped;
//                    next(value);
//                }
                if (obj instanceof Number) {
                    Number value = (Number) obj;
                    next(value);
                }
            }
            return getValue();
        }
        return null;
    }
}
