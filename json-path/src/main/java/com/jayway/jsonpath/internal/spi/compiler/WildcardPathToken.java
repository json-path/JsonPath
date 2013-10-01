package com.jayway.jsonpath.internal.spi.compiler;

/**
 *
 */
class WildcardPathToken extends PathToken {

    @Override
    void evaluate(String currentPath, Object model, EvaluationContextImpl ctx) {
        if (ctx.jsonProvider().isMap(model)) {
            for (String property : ctx.jsonProvider().getPropertyKeys(model)) {
                handleObjectProperty(currentPath, model, ctx, property);
            }
        } else if (ctx.jsonProvider().isArray(model)) {
            for (int idx = 0; idx < ctx.jsonProvider().length(model); idx++) {
                handleArrayIndex(idx, currentPath, model, ctx);
            }
        }
    }


    @Override
    boolean isTokenDefinite() {
        return false;
    }

    @Override
    public String getPathFragment() {
        return "[*]";
    }
}
