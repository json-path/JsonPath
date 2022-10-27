package com.jayway.jsonpath.internal.function.sequence;

import com.jayway.jsonpath.internal.EvaluationContext;
import com.jayway.jsonpath.internal.function.Parameter;

import java.util.List;

/**
 * Take the first item from collection of JSONArray
 *
 * Created by git9527 on 6/11/22.
 */
public class Last extends AbstractSequenceAggregation {
    @Override
    protected int targetIndex(EvaluationContext ctx, List<Parameter> parameters) {
        return -1;
    }
}
