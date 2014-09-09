package com.jayway.jsonpath.internal.compiler;

import com.jayway.jsonpath.InvalidPathException;
import com.jayway.jsonpath.Option;
import com.jayway.jsonpath.PathNotFoundException;
import com.jayway.jsonpath.internal.Utils;
import com.jayway.jsonpath.spi.json.JsonProvider;

import java.util.List;

public abstract class PathToken {

    private PathToken next;
    private Boolean definite = null;

    PathToken appendTailToken(PathToken next) {
        this.next = next;
        return next;
    }

    void handleObjectProperty(String currentPath, Object model, EvaluationContextImpl ctx, List<String> properties) {

        if(properties.size() == 1) {
            String property = properties.get(0);
            String evalPath = currentPath + "['" + property + "']";
            Object propertyVal = readObjectProperty(property, model, ctx);
            if(propertyVal == JsonProvider.UNDEFINED){
                if(isLeaf()) {
                    if(ctx.options().contains(Option.DEFAULT_PATH_LEAF_TO_NULL)){
                        propertyVal =  null;
                    } else {
                        if(ctx.options().contains(Option.SUPPRESS_EXCEPTIONS)){
                            return;
                        } else {
                            throw new PathNotFoundException("No results for path: " + evalPath);
                        }

                    }
                } else {
                    throw new PathNotFoundException();
                    //return;
                }
            }
            if (isLeaf()) {
                ctx.addResult(evalPath, propertyVal);
            } else {
                next().evaluate(evalPath, propertyVal, ctx);
            }
        } else {
            String evalPath = currentPath + "[" + Utils.join(", ", "'", properties) + "]";

            if (!isLeaf()) {
                throw new InvalidPathException("Multi properties can only be used as path leafs: " + evalPath);
            }

            if(ctx.configuration().containsOption(Option.MERGE_MULTI_PROPS)) {
                Object map = ctx.jsonProvider().createMap();
                for (String property : properties) {
                    Object propertyVal = readObjectProperty(property, model, ctx);
                    if(propertyVal == JsonProvider.UNDEFINED){
                        if(ctx.options().contains(Option.DEFAULT_PATH_LEAF_TO_NULL)){
                            propertyVal = null;
                        } else {
                            continue;
                        }
                    }
                    ctx.jsonProvider().setProperty(map, property, propertyVal);
                }
                ctx.addResult(evalPath, map);

            } else {
                for (String property : properties) {
                    evalPath = currentPath + "['" + property + "']";
                    if(hasProperty(property, model, ctx)) {
                        Object propertyVal = readObjectProperty(property, model, ctx);
                        if(propertyVal == JsonProvider.UNDEFINED){
                            if(ctx.options().contains(Option.DEFAULT_PATH_LEAF_TO_NULL)){
                                propertyVal = null;
                            } else {
                                continue;
                            }
                        }
                        ctx.addResult(evalPath, propertyVal);
                    }
                }
            }
        }
    }

    private boolean hasProperty(String property, Object model, EvaluationContextImpl ctx) {
        return ctx.jsonProvider().getPropertyKeys(model).contains(property);
    }

    private Object readObjectProperty(String property, Object model, EvaluationContextImpl ctx) {
        return ctx.jsonProvider().getMapValue(model, property, true);
    }

    void handleArrayIndex(int index, String currentPath, Object json, EvaluationContextImpl ctx) {
        String evalPath = currentPath + "[" + index + "]";
        try {
            Object evalHit = ctx.jsonProvider().getArrayIndex(json, index);
            if (isLeaf()) {
                ctx.addResult(evalPath, evalHit);
            } else {
                next().evaluate(evalPath, evalHit, ctx);
            }
        } catch (IndexOutOfBoundsException e) {
            throw new PathNotFoundException("Index out of bounds when evaluating path " + evalPath);
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

    public abstract void evaluate(String currentPath, Object model, EvaluationContextImpl ctx);

    abstract boolean isTokenDefinite();

    abstract String getPathFragment();

}
