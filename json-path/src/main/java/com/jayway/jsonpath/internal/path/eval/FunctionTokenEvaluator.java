package com.jayway.jsonpath.internal.path.eval;

import com.jayway.jsonpath.internal.PathRef;
import com.jayway.jsonpath.internal.function.PathFunction;
import com.jayway.jsonpath.internal.function.PathFunctionFactory;
import com.jayway.jsonpath.internal.path.EvaluationContextImpl;
import com.jayway.jsonpath.internal.path.EvaluatorDispatcher;
import com.jayway.jsonpath.internal.path.token.FunctionPathToken;

public class FunctionTokenEvaluator extends TokenEvaluator<FunctionPathToken> {
  public FunctionTokenEvaluator(final EvaluatorDispatcher dispatcher) {
    super(dispatcher);
  }

  @Override
  public void evaluate(final FunctionPathToken token, final String currentPath, final PathRef parent, final Object model, final EvaluationContextImpl ctx) {
    PathFunction pathFunction = PathFunctionFactory.newFunction(token.getFunctionName());
    Object result = pathFunction.invoke(currentPath, parent, model, ctx);
    ctx.addResult(currentPath, parent, result);
  }
}
