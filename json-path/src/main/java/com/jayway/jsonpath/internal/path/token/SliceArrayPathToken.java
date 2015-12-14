package com.jayway.jsonpath.internal.path.token;

import com.jayway.jsonpath.internal.path.operation.ArraySliceOperation;

public class SliceArrayPathToken extends ArrayPathToken {
  protected final ArraySliceOperation arraySliceOperation;

  public SliceArrayPathToken(final ArraySliceOperation arraySliceOperation) {
    super();
    if (arraySliceOperation == null) throw new IllegalArgumentException();
    this.arraySliceOperation = arraySliceOperation;
  }

  @Override
  public String getPathFragment() {
    return arraySliceOperation.toString();
  }

  @Override
  public boolean isTokenDefinite() {
    return false;
  }

  public ArraySliceOperation operation() {
    return arraySliceOperation;
  }
}
