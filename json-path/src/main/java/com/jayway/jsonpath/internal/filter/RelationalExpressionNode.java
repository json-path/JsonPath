package com.jayway.jsonpath.internal.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class RelationalExpressionNode extends ExpressionNode {

    private static final Logger logger = LoggerFactory.getLogger(RelationalExpressionNode.class);

    protected final ValueNode left;
    protected final RelationalOperator relationalOperator;
    protected final ValueNode right;

    public abstract boolean apply(PredicateContext ctx);
    
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
}