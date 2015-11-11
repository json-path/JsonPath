package com.jayway.jsonpath.internal.filter;

import com.jayway.jsonpath.InvalidPathException;

public enum RelationalOperator {

    GTE(">="),
    LTE("<="),
    EQ("=="),
    NE("!="),
    LT("<"),
    GT(">"),
    REGEX("=~"),
    NIN("¦NIN¦"),
    IN("¦IN¦"),
    CONTAINS("¦CONTAINS¦"),
    ALL("¦ALL¦"),
    SIZE("¦SIZE¦"),
    EXISTS("¦EXISTS¦"),
    TYPE("¦TYPE¦"),
    MATCHES("¦MATCHES¦"),
    EMPTY("¦EMPTY¦");

    private final String operatorString;

    RelationalOperator(String operatorString) {
        this.operatorString = operatorString;
    }

    public String getOperatorString() {
        return operatorString;
    }

    public static RelationalOperator fromString(String operatorString){
        for (RelationalOperator operator : RelationalOperator.values()) {
            if(operator.operatorString.equals(operatorString) ){
                return operator;
            }
        }
        throw new InvalidPathException("Operator not supported " + operatorString);
    }

    @Override
    public String toString() {
        return operatorString;
    }
}
