package com.jayway.jsonpath.internal.filter;

import com.jayway.jsonpath.Predicate;

public class ClassNode extends ValueNode {
    private final Class clazz;

    public ClassNode(Class clazz) {
        this.clazz = clazz;
    }

    @Override
    public Class<?> type(Predicate.PredicateContext ctx) {
        return Class.class;
    }

    public boolean isClassNode() {
        return true;
    }

    public ClassNode asClassNode() {
        return this;
    }

    public Class getClazz() {
        return clazz;
    }

    @Override
    public String toString() {
        return clazz.getName();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ClassNode)) return false;

        ClassNode that = (ClassNode) o;

        return !(clazz != null ? !clazz.equals(that.clazz) : that.clazz != null);
    }
}