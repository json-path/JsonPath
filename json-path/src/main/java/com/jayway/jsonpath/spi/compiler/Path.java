package com.jayway.jsonpath.spi.compiler;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.internal.spi.compiler.RootPathToken;

/**
 *
 */
public interface Path {

    EvaluationContext evaluate(Object model, Configuration configuration);

    public RootPathToken getRoot();

    boolean isDefinite();

    public Path clone();

}
