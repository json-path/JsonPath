package com.jayway.jsonpath.internal.path;

import com.jayway.jsonpath.internal.PathRef;
import com.jayway.jsonpath.internal.function.PathFunction;
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

    @Override
    public void evaluate(String currentPath, PathRef parent, Object model, EvaluationContextImpl ctx) {
        PathFunction pathFunction = PathFunctionFactory.newFunction(functionName);
        Object result = pathFunction.invoke(currentPath, parent, model, ctx);
        ctx.addResult(currentPath, parent, result);
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


}
