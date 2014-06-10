package com.jayway.jsonpath.internal.spi.compiler;

import com.jayway.jsonpath.PathNotFoundException;

/**
 *
 */
class PropertyPathToken extends PathToken {

    private final String property;

    public PropertyPathToken(String property) {
        this.property = property;
    }

    public String getProperty() {
        return property;
    }

    @Override
    void evaluate(String currentPath, Object model, EvaluationContextImpl ctx) {
        if (!ctx.jsonProvider().isMap(model)) {
            throw new PathNotFoundException("Property ['" + property + "'] not found in path " + currentPath);
        }

        handleObjectProperty(currentPath, model, ctx, property);
    }

    @Override
    boolean isTokenDefinite() {
        return true;
    }

    @Override
    public String getPathFragment() {
        return new StringBuilder()
                .append("['")
                .append(property)
                .append("']").toString();
    }
}
