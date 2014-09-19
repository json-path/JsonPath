package com.jayway.jsonpath.spi.mapper;

public class MappingException extends RuntimeException {

    public MappingException(Throwable cause) {
        super(cause);
    }

    public MappingException(String message) {
        super(message);
    }
}
