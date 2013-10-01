package com.jayway.jsonpath.internal.spi.compiler;

import com.jayway.jsonpath.Option;
import com.jayway.jsonpath.PathNotFoundException;

/**
 *
 */
abstract class PathToken {

    private PathToken next;
    private Boolean definite = null;

    PathToken appendTailToken(PathToken next) {
        this.next = next;
        return next;
    }

    void handleObjectProperty(String currentPath, Object model, EvaluationContextImpl ctx, String property) {
        String evalPath = currentPath + "['" + property + "']";
        Object propertyVal = readObjectProperty(property, model, ctx);
        if (isLeaf()) {
            ctx.addResult(evalPath, propertyVal);
        } else {
            next().evaluate(evalPath, propertyVal, ctx);
        }
    }


    private Object readObjectProperty(String property, Object model, EvaluationContextImpl ctx) {
        if (ctx.options().contains(Option.THROW_ON_MISSING_PROPERTY) && !ctx.jsonProvider().getPropertyKeys(model).contains(property)) {
            throw new PathNotFoundException("Path [" + property + "] not found in the current context:\n" + ctx.jsonProvider().toJson(model));
        }
        return ctx.jsonProvider().getProperty(model, property);
    }

    void handleArrayIndex(int index, String currentPath, Object json, EvaluationContextImpl ctx) {
        String evalPath = currentPath + "[" + index + "]";
        Object evalHit = ctx.jsonProvider().getProperty(json, index);
        if (isLeaf()) {
            ctx.addResult(evalPath, evalHit);
        } else {
            next().evaluate(evalPath, evalHit, ctx);
        }
    }


    PathToken next() {
        if (isLeaf()) {
            throw new IllegalStateException("Current path token is a leaf");
        }
        return next;
    }

    boolean isLeaf() {
        return next == null;
    }


    public int getTokenCount() {
        int cnt = 1;
        PathToken token = this;

        while (!token.isLeaf()){
            token = token.next();
            cnt++;
        }
        return cnt;
    }

    public boolean isPathDefinite() {
        if(definite != null){
            return definite.booleanValue();
        }
        boolean isDefinite = isTokenDefinite();
        if (isDefinite && !isLeaf()) {
            isDefinite = next.isPathDefinite();
        }
        definite = isDefinite;
        return isDefinite;
    }

    @Override
    public String toString() {
        if (isLeaf()) {
            return getPathFragment();
        } else {
            return getPathFragment() + next().toString();
        }
    }

    @Override
    public int hashCode() {
        return toString().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    abstract void evaluate(String currentPath, Object model, EvaluationContextImpl ctx);

    abstract boolean isTokenDefinite();

    abstract String getPathFragment();

}
