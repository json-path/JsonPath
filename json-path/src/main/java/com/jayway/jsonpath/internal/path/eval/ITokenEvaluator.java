package com.jayway.jsonpath.internal.path.eval;

import com.jayway.jsonpath.internal.PathRef;
import com.jayway.jsonpath.internal.path.EvaluationContextImpl;
import com.jayway.jsonpath.internal.path.token.PathToken;

public interface ITokenEvaluator<P extends PathToken> {
  void evaluate(P token, String currentPath, PathRef parent, Object model, EvaluationContextImpl ctx);
}
