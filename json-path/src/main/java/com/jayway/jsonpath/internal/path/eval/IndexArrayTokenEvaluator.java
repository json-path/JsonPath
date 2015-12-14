package com.jayway.jsonpath.internal.path.eval;

import com.jayway.jsonpath.internal.PathRef;
import com.jayway.jsonpath.internal.path.EvaluationContextImpl;
import com.jayway.jsonpath.internal.path.EvaluatorDispatcher;
import com.jayway.jsonpath.internal.path.operation.ArrayIndexOperation;
import com.jayway.jsonpath.internal.path.token.IndexArrayPathToken;

public class IndexArrayTokenEvaluator extends TokenEvaluator<IndexArrayPathToken> {
  public IndexArrayTokenEvaluator(final EvaluatorDispatcher dispatcher) {
    super(dispatcher);
  }

  @Override
  public void evaluate(final IndexArrayPathToken token, final String currentPath, final PathRef parent, final Object model, final EvaluationContextImpl ctx) {
    if (! checkArrayModel(token, currentPath, model, ctx))
      return;
    evaluateIndexOperation(token, currentPath, parent, model, ctx);
  }

  public void evaluateIndexOperation(final IndexArrayPathToken token, String currentPath, PathRef parent, Object model, EvaluationContextImpl ctx) {

    if (! checkArrayModel(token, currentPath, model, ctx))
      return;

    final ArrayIndexOperation operation = token.operation();
    if(operation.isSingleIndexOperation()){
      handleArrayIndex(token, operation.indexes().get(0), currentPath, model, ctx);
    } else {
      for (Integer index : operation.indexes()) {
        handleArrayIndex(token, index, currentPath,  model, ctx);
      }
    }
  }

}
