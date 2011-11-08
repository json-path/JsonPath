package com.jayway.jsonpath;

/**
 * Created by IntelliJ IDEA.
 * User: kallestenflo
 * Date: 11/8/11
 * Time: 4:01 PM
 */
public class InvalidJsonException extends RuntimeException{

    public InvalidJsonException() {
    }

    public InvalidJsonException(String message) {
        super(message);
    }

    public InvalidJsonException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidJsonException(Throwable cause) {
        super(cause);
    }
}
