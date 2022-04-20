package com.jayway.jsonpath.internal.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RelationalExpressionNode extends ExpressionNode {

    private static final Logger logger = LoggerFactory.getLogger(RelationalExpressionNode.class);

    private final ValueNode left;
    private final RelationalOperator relationalOperator;
    private final ValueNode right;

    public RelationalExpressionNode(ValueNode left, RelationalOperator relationalOperator, ValueNode right) {
        try{
            // if need change left and right, change them
            if(needSwap(right)){
                ValueNode tmp=left;
                left=right;
                right=tmp;
                relationalOperator=reverseRelationOperator(relationalOperator);
            }
        }catch (Exception e){
            // if can't change, restore them
            ValueNode tmp=left;
            left=right;
            right=tmp;
        }
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
    boolean needSwap(ValueNode right){
        return right.toString().charAt(0)=='@';
    }
    RelationalOperator reverseRelationOperator(RelationalOperator operator) throws Exception{
        switch (operator){
            case EQ:
                return RelationalOperator.EQ;
            case GTE:
                return RelationalOperator.LTE;
            case LTE:
                return RelationalOperator.GTE;
            case GT:
                return RelationalOperator.LT;
            case NE:
                return RelationalOperator.NE;
            default:
                throw new Exception("Type can't change!");
        }
    }
}