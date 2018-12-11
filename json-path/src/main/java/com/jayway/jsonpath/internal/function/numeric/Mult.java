package com.jayway.jsonpath.internal.function.numeric;

/**
 * Created by soberich on 12/12/18.
 */
public class Mult extends AbstractAggregation {
    private Double multiplicaton = null;

    @Override
    protected void next(Number value) {
        if (multiplicaton == null) {
            multiplicaton = value.doubleValue();
        } else {
            multiplicaton *= value.doubleValue();
        }

    }

    @Override
    protected Number getValue() {
        return multiplicaton;
    }
}
