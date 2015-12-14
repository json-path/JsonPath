package com.jayway.jsonpath.internal.path.eval;

import com.jayway.jsonpath.Option;
import com.jayway.jsonpath.PathNotFoundException;
import com.jayway.jsonpath.internal.PathRef;
import com.jayway.jsonpath.internal.path.EvaluationContextImpl;
import com.jayway.jsonpath.internal.path.EvaluatorDispatcher;
import com.jayway.jsonpath.internal.path.token.WildcardPathToken;

import java.util.Collections;

public class WildcardTokenEvaluator extends TokenEvaluator<WildcardPathToken> {
  public WildcardTokenEvaluator(final EvaluatorDispatcher dispatcher) {
    super(dispatcher);
  }

  @Override
  public void evaluate(final WildcardPathToken token, final String currentPath, final PathRef parent, final Object model, final EvaluationContextImpl ctx) {
    if (ctx.jsonProvider().isMap(model)) {
      for (String property : ctx.jsonProvider().getPropertyKeys(model)) {
        handleObjectProperty(token, currentPath, model, ctx, Collections.singletonList(property));
      }
    } else if (ctx.jsonProvider().isArray(model)) {
      for (int idx = 0; idx < ctx.jsonProvider().length(model); idx++) {
        try {
          handleArrayIndex(token, idx, currentPath, model, ctx);
        } catch (PathNotFoundException p){
          if(ctx.options().contains(Option.REQUIRE_PROPERTIES)){
            throw p;
          }
        }
      }
    }
  }
}
