package com.jayway.jsonpath.internal.spi.compiler;

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

    public PathToken getTail() {
        return tail;
    }

    public void setTail(PathToken tail) {
        this.tail = tail;
    }

    @Override
    public int getTokenCount() {
        return tokenCount;
    }

    public void setTokenCount(int tokenCount) {
        this.tokenCount = tokenCount;
    }

    public RootPathToken append(PathToken next) {
        this.tail = tail.appendTailToken(next);
        this.tokenCount++;
        return this;
    }
     /*
    @Override
    public EvaluationContextImpl evaluate(Object model, Configuration configuration) {
        if(logger.isDebugEnabled()) {
            logger.debug("Evaluating path: {}", toString());
        }

        EvaluationContextImpl ctx = new EvaluationContextImpl(configuration, isDefinite());
        evaluate("", model, ctx);

        if(logger.isDebugEnabled()) {
            logger.debug("Found:\n{}", JsonFormatter.prettyPrint(ctx.configuration().getProvider().toJson(ctx.getPathList())));
        }

        return ctx;
    }
     */
    @Override
    void evaluate(String currentPath, Object model, EvaluationContextImpl ctx) {
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

    public PathToken walkToTail() {
        PathToken next = this;
        while (next != null && ! next.isLeaf()) {
            next = next.next();
        }
        return next;
    }

    @Override
    public RootPathToken clone() {
        RootPathToken pathToken = new RootPathToken();
        // deep clone 'next' path
        cloneTo(pathToken);
        // get new tail clone
        PathToken tail = pathToken.walkToTail();
        pathToken.setTail(tail);
        pathToken.setTokenCount(tokenCount);
        return pathToken;
    }

}
