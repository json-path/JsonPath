package com.jayway.jsonpath.internal.path.token;

import com.jayway.jsonpath.internal.path.operation.ArrayIndexOperation;

public class IndexArrayPathToken extends ArrayPathToken {
  protected final ArrayIndexOperation arrayIndexOperation;

  public IndexArrayPathToken(final ArrayIndexOperation arrayIndexOperation) {
    super();
    if (arrayIndexOperation == null) throw new IllegalArgumentException();
    this.arrayIndexOperation = arrayIndexOperation;
  }

  @Override
  public String getPathFragment() {
    return arrayIndexOperation.toString();
  }

  @Override
  public boolean isTokenDefinite() {
    return arrayIndexOperation.isSingleIndexOperation();
  }

  public ArrayIndexOperation operation() {
    return arrayIndexOperation;
  }
}
