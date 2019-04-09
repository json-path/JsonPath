package com.jayway.jsonpath.internal.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RelationalExpressionNode extends ExpressionNode {

    private static final Logger logger = LoggerFactory.getLogger(RelationalExpressionNode.class);

    private final ValueNode left;
    private final RelationalOperator relationalOperator;
    private final ValueNode right;

    public RelationalExpressionNode(ValueNode left, RelationalOperator relationalOperator, ValueNode right) {
        this.left = left;
        this.relationalOperator = relationalOperator;
        this.right = right;

        logger.trace("ExpressionNode {}", toString());
    }

    @Override
    public String toString() {
        if(relationalOperator == RelationalOperator.EXISTS){
            return left.toString();
        } else {
            return left.toString() + " " + relationalOperator.toString() + " " + right.toString();
        }
    }

    @Override
    public boolean apply(PredicateContext ctx) {
        ValueNode l = left;
        ValueNode r = right;

        if(left.isPathNode()){
            l = left.asPathNode().evaluate(ctx);
        }
        if(right.isPathNode()){
            r = right.asPathNode().evaluate(ctx);
        }
        Evaluator evaluator = EvaluatorFactory.createEvaluator(relationalOperator);
        if(evaluator != null){
            return evaluator.evaluate(l, r, ctx);
        }
        return false;
    }
}