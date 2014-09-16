package com.jayway.jsonpath.spi.converter;

public class ConversionException extends RuntimeException {


    public ConversionException(String message, Throwable cause) {
        super(message, cause);
    }

    public ConversionException(Throwable cause) {
        super(cause);
    }

    public ConversionException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public ConversionException(String message) {
        super(message);
    }
}
