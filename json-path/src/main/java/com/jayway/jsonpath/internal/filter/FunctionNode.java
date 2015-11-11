package com.jayway.jsonpath.internal.filter;

import com.jayway.jsonpath.Predicate;

public class FunctionNode extends ValueNode {

    @Override
    public Class<?> type(Predicate.PredicateContext ctx) {
        return Void.class;
    }
}
