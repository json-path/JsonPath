package com.jayway.jsonpath.internal.function.indicator;

import java.util.ArrayList;
import java.util.List;

/**
 * Calculate the RSI series of a JSONArray
 *
 * Created by Andrei on 02/07/25.
 */
public class Rsi extends AbstractWindowFunction {
    @Override
    protected List<Double> calculate(List<Double> values, int window) {
        List<Double> rsi = new ArrayList<>();
        if (values == null || values.isEmpty() || window <= 0 || window > values.size() - 1) return rsi;

        List<Double> gains = new ArrayList<>();
        List<Double> losses = new ArrayList<>();
        for (int i = 1; i < values.size(); i++) {
            double change = values.get(i) - values.get(i - 1);
            gains.add(change > 0 ? change : 0.0);
            losses.add(change < 0 ? Math.abs(change) : 0.0);
        }

        double avgGain = 0.0;
        double avgLoss = 0.0;
        for (int i = 0; i < window; i++) {
            avgGain += gains.get(i);
            avgLoss += losses.get(i);
        }
        avgGain /= window;
        avgLoss /= window;

        double rs = avgLoss == 0 ? Double.POSITIVE_INFINITY : avgGain/avgLoss;
        double value = rs == Double.POSITIVE_INFINITY ? 100.0 : 100.0 - (100.0 / (1.0 + rs));
        rsi.add(value);

        for (int i = window; i < gains.size(); i++) {
            avgGain = ((avgGain * (window - 1)) + gains.get(i)) / window;
            avgLoss = ((avgLoss * (window - 1)) + losses.get(i)) / window;
            rs = avgLoss == 0 ? Double.POSITIVE_INFINITY : avgGain / avgLoss;
            value = rs == Double.POSITIVE_INFINITY ? 100.0 : 100.0 - (100.0 / (1.0 + rs));
            rsi.add(value);
        }

        return rsi;
    }
}
