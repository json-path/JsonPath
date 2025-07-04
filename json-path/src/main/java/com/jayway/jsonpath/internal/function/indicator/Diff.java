package com.jayway.jsonpath.internal.function.indicator;

import com.jayway.jsonpath.internal.EvaluationContext;
import com.jayway.jsonpath.internal.PathRef;
import com.jayway.jsonpath.internal.function.Parameter;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class Diff extends AbstractIndicatorFunction {

    @Override
    public Object invoke(String currentPath, PathRef parent, Object model, EvaluationContext ctx, List<Parameter> parameters) {
        if (null != parameters && !parameters.isEmpty()) {
            int k = Optional.ofNullable(parameters.get(1)).map(Parameter::getValue).map(o -> o instanceof Integer ? (Integer)o : 2).orElse(2);
            if (ctx.configuration().jsonProvider().isArray(model)) {
                List<Double> list1 = makeListFromModel(ctx, model);
                Object innerModel = parameters.get(0).getPath().evaluate(ctx.rootDocument(), ctx.rootDocument(), ctx.configuration()).getValue();
                if (ctx.configuration().jsonProvider().isArray(innerModel)) {
                    List<Double> list2 = makeListFromModel(ctx, innerModel);
                    List<Double> diff = diff(list1, list2);
                    return roundList(diff, k);
                }
            }
        }
        return new ArrayList<>();
    }

    private List<Double> makeListFromModel(EvaluationContext ctx, Object model) {
        Iterable<?> objects = ctx.configuration().jsonProvider().toIterable(model);
        return StreamSupport.stream(objects.spliterator(), false)
                .filter(Number.class::isInstance)
                .map(obj -> ((Number) obj).doubleValue())
                .collect(Collectors.toList());
    }

    private List<Double> diff(List<Double> list1, List<Double> list2) {
        if (list1 == null || list2 == null || list1.isEmpty() || list2.isEmpty()) {
            return new ArrayList<>();
        }

        int resultLength = Math.min(list1.size(), list2.size());
        List<Double> differences = new ArrayList<>(resultLength);

        for (int i = 0; i < resultLength; i++) {
            int index1 = list1.size() - resultLength + i;
            int index2 = list2.size() - resultLength + i;
            double diff = list1.get(index1) - list2.get(index2);
            differences.add(diff);
        }
        return differences;
    }
}
