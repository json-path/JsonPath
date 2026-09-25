package com.jayway.jsonpath.spi.function;

import com.jayway.jsonpath.internal.EvaluationContext;
import com.jayway.jsonpath.internal.PathRef;
import com.jayway.jsonpath.internal.function.Parameter;

import java.util.List;

/**
 * Defines the contract by which a function can be executed over the result set in the particular path
 * being evaluated. The function's input is the content of the data from the json path selector and its output
 * is defined via the function's behavior. Additionally, functions can accept multiple parameters.
 */
public interface PathFunction {

    /**
     * Invoke the function and output a JSON object (or scalar) value which will be the result of executing the path.
     *
     * @param currentPath
     *      The current path location inclusive of the function name
     * @param parent
     *      The path location above the current function
     * @param model
     *      The JSON model as input to this particular function
     * @param ctx
     *      Eval context, state bag used as the path is traversed, maintains the result of executing
     * @param parameters
     *      Function parameters passed in the path expression
     * @return result of function execution
     */
    Object invoke(String currentPath, PathRef parent, Object model, EvaluationContext ctx, List<Parameter> parameters);
}
