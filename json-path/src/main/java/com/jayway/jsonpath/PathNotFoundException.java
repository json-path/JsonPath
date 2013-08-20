package com.jayway.jsonpath;

/**
 * User: kalle
 * Date: 8/20/13
 * Time: 2:33 PM
 */
public class PathNotFoundException extends InvalidPathException {

    public PathNotFoundException() {
    }

    public PathNotFoundException(String message) {
        super(message);
    }

    public PathNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public PathNotFoundException(Throwable cause) {
        super(cause);
    }
}
