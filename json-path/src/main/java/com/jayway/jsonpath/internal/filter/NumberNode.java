package com.jayway.jsonpath.internal.filter;

import java.math.BigDecimal;

import com.jayway.jsonpath.Predicate;

public class NumberNode extends ValueNode {

    public static NumberNode NAN = new NumberNode((BigDecimal)null);

    private final BigDecimal number;

    public NumberNode(BigDecimal number) {
        this.number = number;
    }
    
    public NumberNode(CharSequence num) {
        number = new BigDecimal(num.toString());
    }

    @Override
    public StringNode asStringNode() {
        return new StringNode(number.toString(), false);
    }

    public BigDecimal getNumber() {
        return number;
    }

    @Override
    public Class<?> type(Predicate.PredicateContext ctx) {
        return Number.class;
    }

    public boolean isNumberNode() {
        return true;
    }

    public NumberNode asNumberNode() {
        return this;
    }

    @Override
    public String toString() {
        return number.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof NumberNode) && !(o instanceof StringNode)) return false;

        NumberNode that = ((ValueNode)o).asNumberNode();

        if(that == NumberNode.NAN){
            return false;
        } else {
            return number.compareTo(that.number) == 0;
        }
    }
}