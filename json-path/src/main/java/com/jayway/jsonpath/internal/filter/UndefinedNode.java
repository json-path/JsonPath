package com.jayway.jsonpath.internal.filter;

import com.jayway.jsonpath.Predicate;

public class UndefinedNode extends ValueNode {

    @Override
    public Class<?> type(Predicate.PredicateContext ctx) {
        return Void.class;
    }

    public UndefinedNode asUndefinedNode() {
        return this;
    }

    public boolean isUndefinedNode() {
        return true;
    }

    @Override
    public boolean equals(Object o) {
        return false;
    }
}