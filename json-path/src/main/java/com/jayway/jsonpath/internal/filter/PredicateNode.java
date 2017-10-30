package com.jayway.jsonpath.internal.filter;

import com.jayway.jsonpath.Predicate;

public class PredicateNode extends ValueNode {

    private final Predicate predicate;

    public PredicateNode(Predicate predicate) {
        this.predicate = predicate;
    }

    public Predicate getPredicate() {
        return predicate;
    }

    public PredicateNode asPredicateNode() {
        return this;
    }

    @Override
    public Class<?> type(Predicate.PredicateContext ctx) {
        return Void.class;
    }

    public boolean isPredicateNode() {
        return true;
    }

    @Override
    public boolean equals(Object o) {
        return false;
    }

    @Override
    public String toString() {
        return predicate.toString();
    }
}