package com.jayway.jsonpath.internal.function.indicator;

import com.jayway.jsonpath.internal.EvaluationContext;
import com.jayway.jsonpath.internal.function.Parameter;
import com.jayway.jsonpath.internal.function.PathFunction;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

abstract public class AbstractIndicatorFunction implements PathFunction {

    private static double round(double value, int places) {
        double scale = Math.pow(10, places);
        return Math.round(value * scale) / scale;
    }

    protected int geParameter(EvaluationContext ctx, List<Parameter> parameters, int index, int defaultValue) {
        List<Number> params = Parameter.toList(Number.class, ctx, parameters);
        return params.size() > index ? params.get(index).intValue() : defaultValue;
    }

    protected List<Double> roundList(List<Double> values, int scale) {
        return Optional.ofNullable(values)
                .map(l -> l.stream()
                        .map(v -> round(v, scale))
                        .collect(Collectors.toList()))
                .orElse(new ArrayList<>());
    }
}
