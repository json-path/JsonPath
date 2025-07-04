package com.jayway.jsonpath.internal.function.indicator;

import java.util.ArrayList;
import java.util.List;

/**
 * Calculate the EMA series of a JSONArray
 *
 * Created by Andrei on 02/07/25.
 */
public class Ema extends AbstractWindowFunction {

    @Override
    protected List<Double> calculate(List<Double> values, int window) {
        List<Double> ema = new ArrayList<>();
        if (values == null || values.isEmpty() || window <= 0 || window > values.size()) return ema;

        double alpha = 2.0 / (window + 1);
        double sum = 0;
        for (int i = 0; i < window; i++) sum += values.get(i);

        ema.add(sum/window);

        for (int i = window; i < values.size(); i++) {
            ema.add(alpha * values.get(i) + (1 - alpha) * ema.get(ema.size() - 1));
        }
        return ema;
    }
}
