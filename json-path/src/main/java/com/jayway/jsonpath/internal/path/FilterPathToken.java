package com.jayway.jsonpath.internal.path;

import com.jayway.jsonpath.Filter;
import com.jayway.jsonpath.internal.PathRef;
import com.jayway.jsonpath.internal.function.FilterFunction;
import com.jayway.jsonpath.internal.function.FilterFunctionFactory;
import com.jayway.jsonpath.internal.function.PathFunctionFactory;

/**
 * Token representing a Function call to one of the functions produced via the FunctionFactory
 *
 * @see PathFunctionFactory
 */
public class FilterPathToken extends PathToken {

    private final String functionName;
    private final String pathFragment;
    private Filter functionFilter;

    public FilterPathToken(String pathFragment, Filter filter) {
        this.pathFragment = pathFragment + ((filter != null) ? "(...)" : "()");
        if(null != pathFragment){
            functionName = pathFragment;
            functionFilter = filter;
        } else {
            functionName = null;
            functionFilter = null;
        }
    }

    @Override
    public void evaluate(String currentPath, PathRef parent, Object model, EvaluationContextImpl ctx) {
        FilterFunction filterFunction = FilterFunctionFactory.newFunction(functionName);
        Object result = filterFunction.invoke(currentPath, parent, model, ctx, functionFilter);
        ctx.addResult(currentPath + "." + functionName, parent, result);
        if (!isLeaf()) {
            next().evaluate(currentPath, parent, result, ctx);
        }
    }

    @Override
    public boolean isTokenDefinite() {
        return true;
    }

    @Override
    public String getPathFragment() {
        return "." + pathFragment;
    }
}
