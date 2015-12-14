package com.jayway.jsonpath.internal.path.token;

import com.jayway.jsonpath.internal.function.PathFunctionFactory;

/**
 * Token representing a Function call to one of the functions produced via the FunctionFactory
 *
 * @see PathFunctionFactory
 *
 * Created by mattg on 6/27/15.
 */
public class FunctionPathToken extends PathToken {

    private final String functionName;
    private final String pathFragment;

    public FunctionPathToken(String pathFragment) {
        this.pathFragment = pathFragment;
        if(pathFragment.endsWith("()")){
            functionName = pathFragment.substring(0, pathFragment.length()-2);
        } else {
            functionName = null;
        }
    }

    /**
     * Return the actual value by indicating true. If this return was false then we'd return the value in an array which
     * isn't what is desired - true indicates the raw value is returned.
     *
     * @return
     */
    @Override
    public boolean isTokenDefinite() {
        return true;
    }

    @Override
    public String getPathFragment() {
        return "." + pathFragment;
    }

    public String getFunctionName() {
        return functionName;
    }
}
