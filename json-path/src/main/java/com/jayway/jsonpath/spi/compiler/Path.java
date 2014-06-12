package com.jayway.jsonpath.spi.compiler;

import com.jayway.jsonpath.Configuration;

/**
 *
 */
public interface Path {

    EvaluationContext evaluate(Object model, Configuration configuration);

    boolean isDefinite();

}
