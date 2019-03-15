package com.jayway.jsonpath.spi.transformer;

import com.jayway.jsonpath.JsonPathException;

public class TransformationException extends JsonPathException {

    public TransformationException(Throwable cause) {
        super(cause);
    }

    public TransformationException(String message) {
        super(message);
    }
}