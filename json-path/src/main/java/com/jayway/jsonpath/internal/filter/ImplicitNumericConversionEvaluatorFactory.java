// (c) Copyright 2016 Likelihood, Inc.
package com.jayway.jsonpath.internal.filter;

import java.util.EnumMap;

import com.jayway.jsonpath.Predicate.PredicateContext;
import com.jayway.jsonpath.internal.filter.ValueNode.NumberNode;

/**
 * Factory that supports implicit numeric conversions for evaluators.
 */
public class ImplicitNumericConversionEvaluatorFactory implements EvaluatorFactory {

    private final EnumMap<RelationalOperator, Evaluator>
            evaluators = new EnumMap<RelationalOperator, Evaluator>(RelationalOperator.class);

    public ImplicitNumericConversionEvaluatorFactory(EvaluatorFactory delegateFactory) {
        // for each operator, wrap just the ones that allow implicit numeric conversion.
        for (RelationalOperator op : RelationalOperator.values()) {
            Evaluator delegateEvaluator = delegateFactory.createEvaluator(op);
            if (delegateEvaluator != null) {
                evaluators.put(
                        op,
                        op.allowImplicitNumericConversion()
                                ? new Wrapper(delegateEvaluator)
                                : delegateEvaluator);
            }
        }
    }

    @Override
    public Evaluator createEvaluator(RelationalOperator operator) {
        return evaluators.get(operator);
    }

    private static class Wrapper implements Evaluator {
        Evaluator wrapped;

        Wrapper(Evaluator wrapped) {
            this.wrapped = wrapped;
        }

        @Override
        public boolean evaluate(
                ValueNode left, ValueNode right, PredicateContext ctx) {
            final boolean eval = wrapped.evaluate(left, right, ctx);
            if (!eval) {
                if (left.isNumberNode() && right.isStringNode()) {
                    final ValueNode converted = right.asNumberNode();
                    return (converted != NumberNode.NAN) && wrapped.evaluate(left, converted, ctx);
                }
                if (left.isStringNode() && right.isNumberNode()) {
                    final ValueNode converted = left.asNumberNode();
                    return (converted != NumberNode.NAN) && wrapped.evaluate(converted, right, ctx);
                }
            }
            return eval;
        }
    }
}
