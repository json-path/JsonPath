package com.jayway.jsonpath.spi.function;

import com.jayway.jsonpath.internal.EvaluationContext;
import com.jayway.jsonpath.internal.PathRef;
import com.jayway.jsonpath.internal.function.ParamType;
import com.jayway.jsonpath.internal.function.Parameter;

import java.util.ArrayList;
import java.util.List;

/**
 * A custom {@link PathFunction} plugin example that joins multiple values or json-path results
 * using a specified delimiter.
 * <p>
 * Supported usage patterns:
 * <ul>
 *   <li>Delimiter first: {@code $.join(" - ", $.user.firstName, $.user.lastName)} or {@code join(", ", $.items[*].name)}</li>
 *   <li>Delimiter last: {@code $.join($.user.firstName, $.user.lastName, " - ")}</li>
 *   <li>Tail-call on array: {@code $.items[*].name.join(", ")} or {@code $.numbers.join("-")}</li>
 * </ul>
 */
public class JoinFunction implements PathFunction {

    @Override
    public Object invoke(String currentPath, PathRef parent, Object model, EvaluationContext ctx, List<Parameter> parameters) {
        String delimiter = "";
        List<Parameter> targetParams = new ArrayList<>();

        if (parameters == null || parameters.isEmpty()) {
            delimiter = "";
        } else if (parameters.size() == 1) {
            // Single parameter is the delimiter; values to join come from model
            Object d = parameters.get(0).getValue();
            delimiter = d != null ? d.toString() : "";
        } else {
            Parameter first = parameters.get(0);
            Parameter last = parameters.get(parameters.size() - 1);

            if (first.getType() != ParamType.PATH && last.getType() == ParamType.PATH) {
                // Delimiter is first argument: join(delimiter, path1, path2, ...)
                Object d = first.getValue();
                delimiter = d != null ? d.toString() : "";
                targetParams = parameters.subList(1, parameters.size());
            } else if (first.getType() == ParamType.PATH && last.getType() != ParamType.PATH) {
                // Delimiter is last argument: join(path1, path2, ..., delimiter)
                Object d = last.getValue();
                delimiter = d != null ? d.toString() : "";
                targetParams = parameters.subList(0, parameters.size() - 1);
            } else {
                // Default: treat first parameter as delimiter
                Object d = first.getValue();
                delimiter = d != null ? d.toString() : "";
                targetParams = parameters.subList(1, parameters.size());
            }
        }

        List<String> itemsToJoin = new ArrayList<>();

        if (!targetParams.isEmpty()) {
            for (Parameter param : targetParams) {
                Object val = param.getValue();
                appendValue(val, ctx, itemsToJoin);
            }
        } else if (model != null) {
            appendValue(model, ctx, itemsToJoin);
        }

        return String.join(delimiter, itemsToJoin);
    }

    private void appendValue(Object val, EvaluationContext ctx, List<String> itemsToJoin) {
        if (val == null) {
            return;
        }
        if (ctx.configuration().jsonProvider().isArray(val)) {
            for (Object item : ctx.configuration().jsonProvider().toIterable(val)) {
                if (item != null) {
                    itemsToJoin.add(item.toString());
                }
            }
        } else if (val instanceof Iterable && !(val instanceof CharSequence)) {
            for (Object item : (Iterable<?>) val) {
                if (item != null) {
                    itemsToJoin.add(item.toString());
                }
            }
        } else {
            itemsToJoin.add(val.toString());
        }
    }
}
