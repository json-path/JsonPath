package com.jayway.jsonpath.internal.function.indicator;

import java.util.ArrayList;
import java.util.List;

/**
 * Calculate the SMA series of a JSONArray
 *
 * Created by Andrei on 02/07/25.
 */
public class Sma extends AbstractWindowFunction {

    @Override
    protected List<Double> calculate(List<Double> values, int window) {
        List<Double> sma = new ArrayList<>();
        if (values == null || values.isEmpty() || window <= 0 || window > values.size()) return sma;

        for (int i = window - 1; i < values.size(); i++) {
            double sum = 0;
            for (int j = i - window + 1; j <= i; j++) {
                sum += values.get(j);
            }
            sma.add(sum / window);
        }
        return sma;
    }
}
