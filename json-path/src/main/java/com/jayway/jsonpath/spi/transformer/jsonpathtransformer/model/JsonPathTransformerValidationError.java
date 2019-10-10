package com.jayway.jsonpath.spi.transformer.jsonpathtransformer.model;

import com.jayway.jsonpath.spi.transformer.ValidationError;

import java.text.MessageFormat;
import java.util.ResourceBundle;

public class JsonPathTransformerValidationError extends ValidationError {

    public static final String BUNDLE = "jsonpath_transformer_resource_bundle";
    public static final String INVALID_LOOKUP_TABLE_REF = "INVALID_LOOKUP_TABLE_REF";
    public static final String UNSUPPORTED_WILDCARD_PATH = "UNSUPPORTED_WILDCARD_PATH";
    public static final String NULL_QUERY_RESULT = "NULL_QUERY_RESULT";
    public static final String INTERNAL_ERROR = "INTERNAL_ERROR";
    public static final String NULL_PARAMETER = "NULL_PARAMETER";
    public static final String SOURCE_NOT_SCALAR = "SOURCE_NOT_SCALAR";
    public static final String PATH_NEITHER_DEFINITE_NOR_WILDCARD_ARRAY =
            "PATH_NEITHER_DEFINITE_NOR_WILDCARD_ARRAY";
    public static final String INVALID_JSON_PATH = "INVALID_JSON_PATH";
    public static final String INVALID_JSON_OBJECT = "INVALID_JSON_OBJECT";
    public static final String INVALID_WILDCARD_ARRAY_MAPPING = "INVALID_WILDCARD_ARRAY_MAPPING";
    public static final String MISSING_TABLE_NAME = "MISSING_TABLE_NAME";
    public static final String MISSING_TABLE_DATA = "MISSING_TABLE_DATA";
    public static final String UNSUPPORTED_OPERATION = "UNSUPPORTED_OPERATION";
    public static final String NO_SOURCE_VALUE_FOR_MAPPING = "NO_SOURCE_VALUE_FOR_MAPPING";
    public static final String MISSING_TARGET_PATH_FOR_MAPPING = "MISSING_TARGET_PATH_FOR_MAPPING";
    public static final String AMBIGUOUS_ADDITIONAL_TRANSFORM = "AMBIGUOUS_ADDITIONAL_TRANSFORM";
    public static final String INVALID_OPERATOR_NULL_SRC_PATH = "INVALID_OPERATOR_NULL_SRC_PATH";
    public static final String MISSING_OPERATOR = "MISSING_OPERATOR";
    public static final String INVALID_UNARY_OPERATOR = "INVALID_UNARY_OPERATOR";
    public static final String INVALID_BINARY_OPERATOR = "INVALID_BINARY_OPERATOR";
    public static final String INVALID_OPERATOR_FOR_TYPE = "INVALID_OPERATOR_FOR_TYPE";
    public static final String NULL_SOURCE = "NULL_SOURCE";
    public static final String NULL_TARGET = "NULL_TARGET";
    public static final String INVALID_OPERATOR = "INVALID_OPERATOR";
    public static final String INVALID_CONSTANT_NOT_SCALAR = "INVALID_CONSTANT_NOT_SCALAR";
    public static final String NULL_ADDITIONAL_TRANSFORM = "NULL_ADDITIONAL_TRANSFORM";
    public static final String INVALID_ADDITIONAL_TRANSFORM = "INVALID_ADDITIONAL_TRANSFORM";
    public static final String MISSING_OPERAND_FOR_BINARY_OPERATOR = "MISSING_OPERAND_FOR_BINARY_OPERATOR";
    public static final String INVALID_OPERATOR_WITH_SRC_NULL = "INVALID_OPERATOR_WITH_SRC_NULL";

    private static ResourceBundle mybundle;

    static {
        mybundle = ResourceBundle.getBundle(BUNDLE);
    }

    public JsonPathTransformerValidationError(String errorCode) {
        super(errorCode, mybundle);
    }

    public JsonPathTransformerValidationError(String errorCode, Object... params) {
        super(errorCode, mybundle,  params);
    }

    public static String getStringFromBundle(String errorCode, Object... params) {
        return MessageFormat.format(mybundle.getString(errorCode),  params);
    }

    public static String getStringFromBundle(String errorCode) {
        return mybundle.getString(errorCode);
    }

}
