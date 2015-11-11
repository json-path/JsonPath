package com.jayway.jsonpath.internal.filter;

import com.jayway.jsonpath.Predicate;

public abstract class ExpressionNode implements Predicate {

    public static ExpressionNode createExpressionNode(ExpressionNode right, LogicalOperator operator,  ExpressionNode left){
        if(operator == LogicalOperator.AND){
            if((left instanceof LogicalExpressionNode) && ((LogicalExpressionNode)left).getOperator() == LogicalOperator.AND ){
                LogicalExpressionNode len = (LogicalExpressionNode) left;
                return len.append(right);
            } else {
                return LogicalExpressionNode.createLogicalAnd(left, right);
            }
        } else {
            if((left instanceof LogicalExpressionNode) && ((LogicalExpressionNode)left).getOperator() == LogicalOperator.OR ){
                LogicalExpressionNode len = (LogicalExpressionNode) left;
                return len.append(right);
            } else {
                return LogicalExpressionNode.createLogicalOr(left, right);
            }
        }
    }
}
