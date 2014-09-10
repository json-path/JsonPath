package com.jayway.jsonpath.internal.compiler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 */
public class RootPathToken extends PathToken /*implements Path*/ {

    private static final Logger logger = LoggerFactory.getLogger(RootPathToken.class);

    private PathToken tail;
    private int tokenCount;

    public RootPathToken() {
        this.tail = this;
        this.tokenCount = 1;
    }

    @Override
    public int getTokenCount() {
        return tokenCount;
    }

    public RootPathToken append(PathToken next) {
        this.tail = tail.appendTailToken(next);
        this.tokenCount++;
        return this;
    }

    @Override
    public void evaluate(String currentPath, Object model, EvaluationContextImpl ctx) {
        if (isLeaf()) {
            ctx.addResult("$", model);
        } else {
            next().evaluate("$", model, ctx);
        }
    }

    @Override
    public String getPathFragment() {
        return "$";
    }

    @Override
    boolean isTokenDefinite() {
        return true;
    }
}
