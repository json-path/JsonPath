package com.jayway.jsonpath.internal.path.operation;

public class SliceFromOperation extends ArraySliceOperation {
  protected SliceFromOperation(Integer from) {
    super(from, null);
  }

  @Override
  public Operation operation() {
    return Operation.SLICE_FROM;
  }
}
