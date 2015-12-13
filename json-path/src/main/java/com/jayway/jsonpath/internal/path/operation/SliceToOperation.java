package com.jayway.jsonpath.internal.path.operation;

public class SliceToOperation extends ArraySliceOperation {
  protected SliceToOperation(final Integer to) {
    super(null, to);
  }

  @Override
  public Operation operation() {
    return Operation.SLICE_TO;
  }
}
