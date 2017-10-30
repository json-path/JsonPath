package com.jayway.jsonpath.internal.filter;

import com.jayway.jsonpath.Predicate;

public class BooleanNode extends ValueNode {
    private final Boolean value;

    BooleanNode(CharSequence boolValue) {
        value = Boolean.parseBoolean(boolValue.toString());
    }

    @Override
    public Class<?> type(Predicate.PredicateContext ctx) {
        return Boolean.class;
    }

    public boolean isBooleanNode() {
        return true;
    }

    public BooleanNode asBooleanNode() {
        return this;
    }

    public boolean getBoolean() {
        return value;
    }

    @Override
    public String toString() {
        return value.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BooleanNode)) return false;

        BooleanNode that = (BooleanNode) o;

        return !(value != null ? !value.equals(that.value) : that.value != null);
    }
}