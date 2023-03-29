package com.jayway.jsonpath.internal.function.numeric;

public class Multiply extends AbstractAggregation {
    private Double multiplication = 1d;

    @Override
    protected void next(Number value) {
        multiplication *= value.doubleValue();
    }

    @Override
    protected Number getValue() {
        return multiplication;
    }
}
