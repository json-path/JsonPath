package com.jayway.jsonpath;

/**
 * Created by IntelliJ IDEA.
 * User: kallestenflo
 * Date: 3/4/12
 * Time: 2:38 PM
 */
@SuppressWarnings("serial")
public class InvalidModelPathException extends RuntimeException {

    public InvalidModelPathException() {
        super();
    }

    public InvalidModelPathException(String message) {
        super(message);
    }

    public InvalidModelPathException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidModelPathException(Throwable cause) {
        super(cause);
    }
}
