package com.jayway.jsonpath.internal.filter;

import com.jayway.jsonpath.InvalidPathException;

public enum RelationalOperator {

    GTE(">=", true),
    LTE("<=", true),
    // EQ already support implicit numeric conversion in StringNode.equals and NumericNode.equals
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
    LT("<", true),
    GT(">", true),
    REGEX("=~"),
    NIN("NIN"),
    IN("IN"),
    CONTAINS("CONTAINS"),
    ALL("ALL"),
    SIZE("SIZE"),
    EXISTS("EXISTS"),
    TYPE("TYPE"),
    MATCHES("MATCHES"),
    EMPTY("EMPTY");

    private final String operatorString;
    private final boolean allowImplicitNumericConversion;

    RelationalOperator(String operatorString) {
        this(operatorString, false);
    }
    RelationalOperator(String operatorString, boolean allowImplicitNumericConversion) {
        this.operatorString = operatorString;
        this.allowImplicitNumericConversion = allowImplicitNumericConversion;
    }

    public static RelationalOperator fromString(String operatorString){
        for (RelationalOperator operator : RelationalOperator.values()) {
            if(operator.operatorString.equals(operatorString.toUpperCase()) ){
                return operator;
            }
        }
        throw new InvalidPathException("Filter operator " + operatorString + " is not supported!");
    }

    public boolean allowImplicitNumericConversion() {
        return allowImplicitNumericConversion;
    }

    @Override
    public String toString() {
        return operatorString;
    }

    public static final EvaluatorFactory DEFAULT_EVALUATOR_FACTORY = new DefaultEvaluatorFactory();
    public static final EvaluatorFactory IMPLICIT_NUMERIC_CONVERSION_EVALUATOR_FACTORY
        = new ImplicitNumericConversionEvaluatorFactory(DEFAULT_EVALUATOR_FACTORY);

    public static EvaluatorFactory getEvaluatorFactory(boolean allowImplicitNumericConversion) {
        return allowImplicitNumericConversion
            ? IMPLICIT_NUMERIC_CONVERSION_EVALUATOR_FACTORY : DEFAULT_EVALUATOR_FACTORY;
    }
}
