package com.jayway.jsonpath.internal.path.predicate;

import com.jayway.jsonpath.internal.path.EvaluationContextImpl;
import com.jayway.jsonpath.internal.path.token.PathToken;
import com.jayway.jsonpath.internal.path.token.PredicatePathToken;

public final class FilterPathTokenPredicate implements PathTokenPredicate {
    private final EvaluationContextImpl ctx;
    private PredicatePathToken predicatePathToken;

    public FilterPathTokenPredicate(PathToken target, EvaluationContextImpl ctx) {
        this.ctx = ctx;
        predicatePathToken = (PredicatePathToken) target;
    }

    @Override
    public boolean matches(Object model) {
        return predicatePathToken.accept(model, ctx.rootDocument(), ctx.configuration(), ctx);
    }
}
