package com.jayway.jsonpath.internal.path;

import com.jayway.jsonpath.internal.PathRef;
import com.jayway.jsonpath.internal.path.token.PathToken;

public interface EvaluatorDispatcher {
  void evaluate(PathToken token, String currentPath, PathRef parent, Object model, EvaluationContextImpl ctx);
}
