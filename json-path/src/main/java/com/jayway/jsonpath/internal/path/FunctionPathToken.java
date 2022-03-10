package com.jayway.jsonpath.internal.path;

import com.jayway.jsonpath.internal.Path;
import com.jayway.jsonpath.internal.PathRef;
import com.jayway.jsonpath.internal.function.Parameter;
import com.jayway.jsonpath.internal.function.PathFunction;
import com.jayway.jsonpath.internal.function.PathFunctionFactory;
import com.jayway.jsonpath.internal.function.latebinding.JsonLateBindingValue;
import com.jayway.jsonpath.internal.function.latebinding.PathLateBindingValue;

import java.util.List;

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
    private List<Parameter> functionParams;

    public FunctionPathToken(String pathFragment, List<Parameter> parameters) {
        this.pathFragment = pathFragment + ((parameters != null && parameters.size() > 0) ? "(...)" : "()");
        if(null != pathFragment){
            functionName = pathFragment;
            functionParams = parameters;
        } else {
            functionName = null;
            functionParams = null;
        }
    }

    @Override
    public void evaluate(String currentPath, PathRef parent, Object model, EvaluationContextImpl ctx) {
        PathFunction pathFunction = PathFunctionFactory.newFunction(functionName);
        evaluateParameters(currentPath, parent, model, ctx);
        Object result = pathFunction.invoke(currentPath, parent, model, ctx, functionParams);
        ctx.addResult(currentPath + "." + functionName, parent, result);
        cleanWildcardPathToken();
        if (!isLeaf()) {
            next().evaluate(currentPath, parent, result, ctx);
        }
    }

    private void cleanWildcardPathToken() {
        if (null != functionParams && functionParams.size() > 0) {
            Path path = functionParams.get(0).getPath();
            if (null != path && !path.isFunctionPath() && path instanceof CompiledPath) {
                RootPathToken root = ((CompiledPath) path).getRoot();
                PathToken tail = root.getNext();
                while (null != tail && null != tail.getNext() ) {
                    if(tail.getNext() instanceof WildcardPathToken){
                        tail.setNext(tail.getNext().getNext());
                        break;
                    }
                    tail = tail.getNext();
                }
            }
        }
    }

    private void evaluateParameters(String currentPath, PathRef parent, Object model, EvaluationContextImpl ctx) {

        if (null != functionParams) {
            for (Parameter param : functionParams) {
                switch (param.getType()) {
                    case PATH:
                        PathLateBindingValue pathLateBindingValue = new PathLateBindingValue(param.getPath(), ctx.rootDocument(), ctx.configuration());
                        if (!param.hasEvaluated()||!pathLateBindingValue.equals(param.getILateBingValue())) {
                            param.setLateBinding(pathLateBindingValue);
                            param.setEvaluated(true);
                        }
                        break;
                    case JSON:
                        if (!param.hasEvaluated()) {
                            param.setLateBinding(new JsonLateBindingValue(ctx.configuration().jsonProvider(), param));
                            param.setEvaluated(true);
                        }
                        break;
                }
            }
        }
    }

    /**
     * Return the actual value by indicating true. If this return was false then we'd return the value in an array which
     * isn't what is desired - true indicates the raw value is returned.
     *
     * @return true if token is definite
     */
    @Override
    public boolean isTokenDefinite() {
        return true;
    }

    @Override
    public String getPathFragment() {
        return "." + pathFragment;
    }


    public void setParameters(List<Parameter> parameters) {
        this.functionParams = parameters;
    }

    public List<Parameter> getParameters() {
        return this.functionParams;
    }
    public String getFunctionName() {
        return this.functionName;
    }
}
