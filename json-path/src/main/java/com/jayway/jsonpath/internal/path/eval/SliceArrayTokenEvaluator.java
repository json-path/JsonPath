package com.jayway.jsonpath.internal.path.eval;

import com.jayway.jsonpath.internal.PathRef;
import com.jayway.jsonpath.internal.path.*;
import com.jayway.jsonpath.internal.path.operation.ArraySliceOperation;
import com.jayway.jsonpath.internal.path.token.SliceArrayPathToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SliceArrayTokenEvaluator extends TokenEvaluator<SliceArrayPathToken> {
  private static final Logger logger = LoggerFactory.getLogger(SliceArrayTokenEvaluator.class);

  public SliceArrayTokenEvaluator(final EvaluatorDispatcher dispatcher) {
    super(dispatcher);
  }

  @Override
  public void evaluate(final SliceArrayPathToken token, final String currentPath, final PathRef parent, final Object model, final EvaluationContextImpl ctx) {
    if (! checkArrayModel(token, currentPath, model, ctx))
      return;
    evaluateSliceOperation(token, currentPath, parent, model, ctx);
  }

  public void evaluateSliceOperation(final SliceArrayPathToken token, String currentPath, PathRef parent, Object model, EvaluationContextImpl ctx) {

    if (! checkArrayModel(token, currentPath, model, ctx))
      return;

    final ArraySliceOperation operation = token.operation();
    switch (operation.operation()) {
      case SLICE_FROM:
        sliceFrom(token, currentPath, parent, model, ctx);
        break;
      case SLICE_BETWEEN:
        sliceBetween(token, currentPath, parent, model, ctx);
        break;
      case SLICE_TO:
        sliceTo(token, currentPath, parent, model, ctx);
        break;
    }
  }

  public void sliceFrom(final SliceArrayPathToken token, String currentPath, PathRef parent, Object model, EvaluationContextImpl ctx) {
    int length = ctx.jsonProvider().length(model);
    int from = token.operation().from();
    if (from < 0) {
      //calculate slice start from array length
      from = length + from;
    }
    from = Math.max(0, from);

    logger.debug("Slice from index on array with length: {}. From index: {} to: {}. Input: {}", length, from, length - 1, toString());

    if (length == 0 || from >= length) {
      return;
    }
    for (int i = from; i < length; i++) {
      handleArrayIndex(token, i, currentPath, model, ctx);
    }
  }

  public void sliceBetween(final SliceArrayPathToken token, String currentPath, PathRef parent, Object model, EvaluationContextImpl ctx) {
    int length = ctx.jsonProvider().length(model);
    int from = token.operation().from();
    int to = token.operation().to();

    to = Math.min(length, to);

    if (from >= to || length == 0) {
      return;
    }

    logger.debug("Slice between indexes on array with length: {}. From index: {} to: {}. Input: {}", length, from, to, toString());

    for (int i = from; i < to; i++) {
      handleArrayIndex(token, i, currentPath, model, ctx);
    }
  }

  public void sliceTo(final SliceArrayPathToken token, String currentPath, PathRef parent, Object model, EvaluationContextImpl ctx) {
    int length = ctx.jsonProvider().length(model);
    if (length == 0) {
      return;
    }
    int to = token.operation().to();
    if (to < 0) {
      //calculate slice end from array length
      to = length + to;
    }
    to = Math.min(length, to);

    logger.debug("Slice to index on array with length: {}. From index: 0 to: {}. Input: {}", length, to, toString());

    for (int i = 0; i < to; i++) {
      handleArrayIndex(token, i, currentPath, model, ctx);
    }
  }

}
