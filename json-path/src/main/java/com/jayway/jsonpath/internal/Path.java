package com.jayway.jsonpath.internal;

import com.jayway.jsonpath.Configuration;

/**
 *
 */
public interface Path {

    /**
     * Evaluates this path
     *
     * @param document the json document to apply the path on
     * @param rootDocument the root json document that started this evaluation
     * @param configuration configuration to use
     * @return EvaluationContext containing results of evaluation
     */
    EvaluationContext evaluate(Object document, Object rootDocument, Configuration configuration);

    /**
     *
     * @return true id this path is definite
     */
    boolean isDefinite();

    /**
     *
     * @return true id this path is starts with '$' and false if the path starts with '@'
     */
    boolean isRootPath();

}
