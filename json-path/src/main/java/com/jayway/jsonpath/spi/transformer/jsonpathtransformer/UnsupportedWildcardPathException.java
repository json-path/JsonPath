package com.jayway.jsonpath.spi.transformer.jsonpathtransformer;

import com.jayway.jsonpath.spi.transformer.TransformationSpecValidationException;

public class UnsupportedWildcardPathException  extends TransformationSpecValidationException {
    public UnsupportedWildcardPathException(Throwable cause) {
        super(cause);
    }
    public UnsupportedWildcardPathException(String message) {
        super(message);
    }

}
