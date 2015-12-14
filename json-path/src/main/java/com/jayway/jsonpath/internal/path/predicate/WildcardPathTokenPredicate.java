package com.jayway.jsonpath.internal.path.predicate;

public final class WildcardPathTokenPredicate implements PathTokenPredicate {
    @Override
    public boolean matches(Object model) {
        return true;
    }
}
