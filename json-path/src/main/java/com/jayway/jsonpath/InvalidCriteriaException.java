package com.jayway.jsonpath;

/**
 * Created by IntelliJ IDEA.
 * User: kallestenflo
 * Date: 3/5/12
 * Time: 12:20 PM
 */
public class InvalidCriteriaException extends RuntimeException{
    public InvalidCriteriaException() {
    }

    public InvalidCriteriaException(String message) {
        super(message);
    }

    public InvalidCriteriaException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidCriteriaException(Throwable cause) {
        super(cause);
    }
}
