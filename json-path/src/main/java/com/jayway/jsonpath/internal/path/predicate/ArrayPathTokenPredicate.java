package com.jayway.jsonpath.internal.path.predicate;

import com.jayway.jsonpath.internal.path.EvaluationContextImpl;

public final class ArrayPathTokenPredicate implements PathTokenPredicate {
    private final EvaluationContextImpl ctx;

    public ArrayPathTokenPredicate(EvaluationContextImpl ctx) {
        this.ctx = ctx;
    }

    @Override
    public boolean matches(Object model) {
        return ctx.jsonProvider().isArray(model);
    }
}
