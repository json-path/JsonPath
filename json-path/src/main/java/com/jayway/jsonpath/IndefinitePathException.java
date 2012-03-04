package com.jayway.jsonpath;

/**
 * Created by IntelliJ IDEA.
 * User: kallestenflo
 * Date: 3/4/12
 * Time: 7:15 PM
 */
public class IndefinitePathException extends RuntimeException {


    public IndefinitePathException(String path) {
        super("The path " + path + " is not definite");
    }
}
