package com.jayway.jsonpath.internal.filter;

import com.jayway.jsonpath.InvalidPathException;

import java.util.Locale;

public enum RelationalOperator {

    GTE(">="),
    LTE("<="),
    EQ("=="),

    /**
     * Type safe equals
     */
    TSEQ("==="),
    NE("!="),

    /**
     * Type safe not equals
     */
    TSNE("!=="),
    LT("<"),
    GT(">"),
    REGEX("=~"),
    NIN("NIN"),
    IN("IN"),
    CONTAINS("CONTAINS"),
    ALL("ALL"),
    SIZE("SIZE"),
    EXISTS("EXISTS"),
    TYPE("TYPE"),
    MATCHES("MATCHES"),
    EMPTY("EMPTY"),
    SUBSETOF("SUBSETOF"),
    ANYOF("ANYOF"),
    NONEOF("NONEOF");

    private final String operatorString;

    RelationalOperator(String operatorString) {
        this.operatorString = operatorString;
    }

    public static RelationalOperator fromString(String operatorString) {
        String upperCaseOperatorString = operatorString.toUpperCase(Locale.ROOT);
        for (RelationalOperator operator : RelationalOperator.values()) {
            if(operator.operatorString.equals(upperCaseOperatorString) ){
                return operator;
            }
        }
        throw new InvalidPathException("Filter operator " + operatorString + " is not supported!");
    }

    @Override
    public String toString() {
        return operatorString;
    }
}
