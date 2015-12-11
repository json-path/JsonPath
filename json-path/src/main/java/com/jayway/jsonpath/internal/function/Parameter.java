package com.jayway.jsonpath.internal.function;

import com.jayway.jsonpath.internal.Path;

/**
 * Created by matt@mjgreenwood.net on 12/10/15.
 */
public class Parameter {
    private ParamType type;
    private Path path;
    private Object cachedValue;
    private Boolean evaluated = false;

    public Parameter() {}

    public Parameter(Path path) {
        this.path = path;
    }

    public Object getCachedValue() {
        return cachedValue;
    }

    public void setCachedValue(Object cachedValue) {
        this.cachedValue = cachedValue;
    }

    public Path getPath() {
        return path;
    }

    public void setEvaluated(Boolean evaluated) {
        this.evaluated = evaluated;
    }

    public boolean hasEvaluated() {
        return evaluated;
    }

    public ParamType getType() {
        return type;
    }

    public void setType(ParamType type) {
        this.type = type;
    }

    public void setPath(Path path) {
        this.path = path;
    }
}
