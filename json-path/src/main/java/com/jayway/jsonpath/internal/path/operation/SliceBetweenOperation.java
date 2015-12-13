package com.jayway.jsonpath.internal.path.operation;

public class SliceBetweenOperation extends ArraySliceOperation {
  protected SliceBetweenOperation(final Integer from, final Integer to) {
    super(from, to);
  }

  @Override
  public Operation operation() {
    return Operation.SLICE_BETWEEN;
  }
}
