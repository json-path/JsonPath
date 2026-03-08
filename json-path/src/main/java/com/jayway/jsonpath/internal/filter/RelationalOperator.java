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
    NONEOF("NONEOF"),
    GTALL("GTALL"),
    GTEALL("GTEALL"),
    GTANY("GTANY"),
    GTEANY("GTEANY"),
    LTALL("LTALL"),
    LTEALL("LTEALL"),
    LTANY("LTANY"),
    LTEANY("LTEANY"),
    DATEEQ("DATEEQ"),
    MONTHEQ("MONTHEQ"),
    DAYEQ("DAYEQ"),
    YEAREQ("YEAREQ"),
    MONTHIN("MONTHIN"),
    DAYIN("DAYIN"),
    YEARIN("YEARIN"),
    BEFORE("BEFORE"),
    AFTER("AFTER"),
    HOUREQ("HOUREQ"),
    HOURIN("HOURIN"),
    TIMEBEFORE("TIMEBEFORE"),
    TIMEAFTER("TIMEAFTER"),
    NOTCONTAINS("NOTCONTAINS"),
    ALLMATCH("ALLMATCH"),
    ANYMATCH("ANYMATCH"),
    NONEMATCH("NONEMATCH"),
    EXACTMATCH("EXACTMATCH"),
    WINDOWIN("WINDOWIN"),
    WINDOWOUT("WINDOWOUT"),
    WINDOWTIMEIN("WINDOWTIMEIN"),
    WINDOWTIMEOUT("WINDOWTIMEOUT");

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
