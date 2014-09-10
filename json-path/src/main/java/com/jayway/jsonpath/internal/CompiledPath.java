package com.jayway.jsonpath.internal;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.internal.compiler.EvaluationContextImpl;
import com.jayway.jsonpath.internal.compiler.PathToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public  class CompiledPath implements Path {

    private static final Logger logger = LoggerFactory.getLogger(CompiledPath.class);

    private final PathToken root;


    public CompiledPath(PathToken root) {
        this.root = root;
    }

    @Override
    public EvaluationContext evaluate(Object model, Configuration configuration) {
        if(logger.isDebugEnabled()) {
            logger.debug("Evaluating path: {}", toString());
        }

        EvaluationContextImpl ctx = new EvaluationContextImpl(this, configuration);
        root.evaluate("", model, ctx);

        return ctx;
    }

    @Override
    public boolean isDefinite() {
        return root.isPathDefinite();
    }

    @Override
    public String toString() {
        return root.toString();
    }
}
