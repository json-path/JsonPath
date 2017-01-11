// (c) Copyright 2016 Likelihood, Inc.
package com.jayway.jsonpath.internal.filter;

/**
 * Interface for creating evaluators.
 */
public interface EvaluatorFactory {
  Evaluator createEvaluator(RelationalOperator operator);
}
