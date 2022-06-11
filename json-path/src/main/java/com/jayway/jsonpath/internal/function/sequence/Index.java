package com.jayway.jsonpath.internal.function.sequence;

import com.jayway.jsonpath.internal.EvaluationContext;
import com.jayway.jsonpath.internal.function.Parameter;

import java.util.List;

/**
 * Take the index from first Parameter, then the item from collection of JSONArray by index
 *
 * Created by git9527 on 6/11/22.
 */
public class Index extends AbstractSequenceAggregation {
    @Override
    protected int targetIndex(EvaluationContext ctx, List<Parameter> parameters) {
        return getIndexFromParameters(ctx, parameters);
    }
}
