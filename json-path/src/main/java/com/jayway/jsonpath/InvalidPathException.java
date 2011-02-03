package com.jayway.jsonpath;

/**
 * User: kalle stenflo
 * Date: 1/24/11
 * Time: 10:09 AM
 */
public class InvalidPathException extends RuntimeException {

    public InvalidPathException() {
    }

    public InvalidPathException(String message) {
        super(message);
    }

    public InvalidPathException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidPathException(Throwable cause) {
        super(cause);
    }
}
