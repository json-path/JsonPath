package com.jayway.jsonpath.spi.converter;

public class ConversionException extends RuntimeException {

    public ConversionException(Throwable cause) {
        super(cause);
    }

    public ConversionException(String message) {
        super(message);
    }
}
