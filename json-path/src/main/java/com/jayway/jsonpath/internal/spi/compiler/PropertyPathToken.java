package com.jayway.jsonpath.internal.spi.compiler;

import com.jayway.jsonpath.PathNotFoundException;
import com.jayway.jsonpath.internal.Utils;

import java.util.List;

/**
 *
 */
class PropertyPathToken extends PathToken {

    private final List<String> properties;

    public PropertyPathToken(List<String> properties) {
        this.properties = properties;
    }

    public List<String> getProperties() {
        return properties;
    }

    @Override
    void evaluate(String currentPath, Object model, EvaluationContextImpl ctx) {
        if (!ctx.jsonProvider().isMap(model)) {
            throw new PathNotFoundException("Property " + getPathFragment() + " not found in path " + currentPath);
        }

        handleObjectProperty(currentPath, model, ctx, properties);
    }

    @Override
    boolean isTokenDefinite() {
        return true;
    }

    @Override
    public String getPathFragment() {
        return new StringBuilder()
                .append("[")
                .append(Utils.join(", ", "'", properties))
                .append("]").toString();
    }
}
