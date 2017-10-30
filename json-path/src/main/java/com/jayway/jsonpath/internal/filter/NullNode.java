package com.jayway.jsonpath.internal.filter;

import com.jayway.jsonpath.Predicate;

public class NullNode extends ValueNode {

    NullNode() {}

    @Override
    public Class<?> type(Predicate.PredicateContext ctx) {
        return Void.class;
    }

    @Override
    public boolean isNullNode() {
        return true;
    }

    @Override
    public NullNode asNullNode() {
        return this;
    }

    @Override
    public String toString() {
        return "null";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof NullNode)) return false;

        return true;
    }
}