package com.jayway.jsonpath.spi.transformer.jsonpathtransformer.model;

import java.util.*;

/**
 *  Captures a single additional Path OR Constant along with Operation to be performed
 * on the Source Value read from the Json Document.
 * <p>
 *
 *  If a Constant Value is specified without Operator and the srcPath at the parent level
 *  is null then effectively the constant is set as the value of the target path in the
 *  transformed document.
 *
 */
public class SourceTransform {


    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public String getSourcePath() {
        return sourcePath;
    }

    public void setSourcePath(String sourcePath) {
        this.sourcePath = sourcePath;
    }

    public Object getConstantSourceValue() {
        return constantSourceValue;
    }

    public void setConstantSourceValue(Object constantSourceValue) {
        this.constantSourceValue = constantSourceValue;
    }

    public static final String NUMERIC = "numeric";
    public static final String STRING = "string";
    public static final String BOOLEAN = "boolean";
    public static final String UNARY_BOOLEAN = "unary_boolean";
    public static final String UNARY_TIME ="unary_time";
    public static final String UNARY_PREFIX = "unary";


    public enum AllowedOperation {



        LHS_STRING_CONCAT(STRING), RHS_STRING_CONCAT(STRING), ADD(NUMERIC), LHS_SUB(NUMERIC),
        RHS_SUB(NUMERIC), MUL(NUMERIC), LHS_DIV(NUMERIC), RHS_DIV(NUMERIC),
        AND(BOOLEAN), OR(BOOLEAN), NOT(UNARY_BOOLEAN), XOR(BOOLEAN), TO_EPOCHMILLIS(UNARY_TIME),
        TO_ISO8601(UNARY_TIME);

        private static Set<String> allowedOperations;
        private String type;

        AllowedOperation(String type) {
            this.type = type;
        }

        static {
            allowedOperations = new HashSet<>();
            for (AllowedOperation op : AllowedOperation.values()) {
                allowedOperations.add(op.name());
            }
        }

        public static Set<String> getAllowedOperations() {
            return allowedOperations;
        }

        public String getType() {
            return type;
        }

        public static boolean isUnary(AllowedOperation op) {
            if (op == null) {
                return false;
            }
            return op.getType().startsWith(UNARY_PREFIX);
        }

    }

    @Override
    public String toString() {
        return " AdditionalSource [operator = " + operator + ", sourcePath = " + sourcePath +
                ", constantSourceValue = " + constantSourceValue  + "]";
    }

    private String operator;
    private String sourcePath;
    private Object constantSourceValue;
}
