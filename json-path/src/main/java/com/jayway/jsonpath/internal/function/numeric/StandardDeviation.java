package com.jayway.jsonpath.internal.function.numeric;

/**
 * Provides the standard deviation of a series of numbers
 *
 * Created by mattg on 6/27/15.
 */
public class StandardDeviation extends AbstractAggregation {
    private double mean = 0d;
    private double sumOfSquaredDifferences = 0d;
    private long count = 0;

    @Override
    protected void next(Number value) {
        double number = value.doubleValue();
        count++;
        double delta = number - mean;
        mean += delta / count;
        sumOfSquaredDifferences += delta * (number - mean);
    }

    @Override
    protected Number getValue() {
        return Math.sqrt(sumOfSquaredDifferences / count);
    }
}
