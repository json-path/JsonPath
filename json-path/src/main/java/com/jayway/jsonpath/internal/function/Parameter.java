package com.jayway.jsonpath.internal.function;

import com.jayway.jsonpath.internal.Path;

/**
 * Created by matt@mjgreenwood.net on 12/10/15.
 */
public class Parameter {
    private final Path path;
    private Object cachedValue;
    private Boolean evaluated = false;

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
}
