package com.jayway.jsonpath.internal.path.eval;

import com.jayway.jsonpath.InvalidPathException;
import com.jayway.jsonpath.internal.PathRef;
import com.jayway.jsonpath.internal.path.EvaluationContextImpl;
import com.jayway.jsonpath.internal.path.EvaluatorDispatcher;
import com.jayway.jsonpath.internal.path.token.PredicatePathToken;

import static java.lang.String.format;

public class PredicateTokenEvaluator extends TokenEvaluator<PredicatePathToken> {
  public PredicateTokenEvaluator(final EvaluatorDispatcher dispatcher) {
    super(dispatcher);
  }

  @Override
  public void evaluate(final PredicatePathToken token, final String currentPath, final PathRef parent, final Object model, final EvaluationContextImpl ctx) {
    if (ctx.jsonProvider().isMap(model)) {
      if (token.accept(model, ctx.rootDocument(), ctx.configuration(), ctx)) {
        PathRef op = ctx.forUpdate() ? parent : PathRef.NO_OP;
        if (token.isLeaf()) {
          ctx.addResult(currentPath, op, model);
        } else {
          dispatcher.evaluate(token.next(), currentPath, op, model, ctx);
        }
      }
    } else if (ctx.jsonProvider().isArray(model)){
      int idx = 0;
      Iterable<?> objects = ctx.jsonProvider().toIterable(model);

      for (Object idxModel : objects) {
        if (token.accept(idxModel, ctx.rootDocument(),  ctx.configuration(), ctx)) {
          handleArrayIndex(token, idx, currentPath, model, ctx);
        }
        idx++;
      }
    } else {
      if (token.isUpstreamDefinite()) {
        throw new InvalidPathException(format("Filter: %s can not be applied to primitives. Current context is: %s", toString(), model));
      }
    }
  }
}
