package com.jayway.jsonpath.internal.path.eval;

import com.jayway.jsonpath.internal.PathRef;
import com.jayway.jsonpath.internal.path.EvaluationContextImpl;
import com.jayway.jsonpath.internal.path.EvaluatorDispatcher;
import com.jayway.jsonpath.internal.path.token.RootPathToken;

public class RootTokenEvaluator extends TokenEvaluator<RootPathToken> {
  public RootTokenEvaluator(final EvaluatorDispatcher dispatcher) {
    super(dispatcher);
  }

  @Override
  public void evaluate(final RootPathToken token, final String currentPath, final PathRef parent, final Object model, final EvaluationContextImpl ctx) {
    if (token.isLeaf()) {
      PathRef op = ctx.forUpdate() ? parent : PathRef.NO_OP;
      ctx.addResult(token.getPathFragment(), op, model);
    } else {
      dispatcher.evaluate(token.next(), token.getPathFragment(), parent, model, ctx);
    }
  }
}
