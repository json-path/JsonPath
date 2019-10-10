package com.jayway.jsonpath.spi.transformer;

import com.jayway.jsonpath.JsonPathException;

public class TransformationSpecValidationException extends JsonPathException {

    public TransformationSpecValidationException(Throwable cause) {
        super(cause);
    }

    public TransformationSpecValidationException(String message) {
        super(message);
    }
}
