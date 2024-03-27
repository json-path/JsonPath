package com.jayway.jsonpath.internal.function.text;

import com.jayway.jsonpath.internal.EvaluationContext;
import com.jayway.jsonpath.internal.PathRef;
import com.jayway.jsonpath.internal.function.ParamType;
import com.jayway.jsonpath.internal.function.Parameter;
import com.jayway.jsonpath.internal.function.PathFunction;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * join values in an array, supply two grammars:
 * <p>
 * 1. [].join([delimiter],[..path])
 * 2. $.join([delimiter],[..path])
 */
public class Join implements PathFunction {
    @Override
    public Object invoke(String currentPath, PathRef parent, Object model, EvaluationContext ctx, List<Parameter> parameters) {
        String delimiter = this.getDelimiterFromParameters(parameters);
        Collection<String> results = Collections.emptyList();

        // [].join([..path])
        if (ctx.configuration().jsonProvider().isArray(model)) {
            results = this.joinByArrayModel(model, ctx, parameters);
        }

        // $.join([delimiter],[..path])
        if (null == results || results.size() == 0) {
            results = this.joinByParams(ctx, parameters);
        }

        if (null == results || results.size() == 0) {
            return "";
        }
        return String.join(delimiter, results);
    }


    /**
     * get results by model
     *
     * @param model
     * @param ctx
     * @param parameters
     * @return
     */
    protected Collection<String> joinByArrayModel(Object model, EvaluationContext ctx, List<Parameter> parameters) {
        Collection<String> resultList = new ArrayList<>();
        List<Parameter> pathParams = Optional.ofNullable(parameters).orElseGet(Collections::emptyList)
                .stream().filter(item -> ParamType.PATH.equals(item.getType())).collect(Collectors.toList());

        if (pathParams.size() == 0) {
            return this.arrayIterableToList(ctx, model, item -> null);
        }

        for (Parameter pathParam : pathParams) {
            List<String> list = this.arrayIterableToList(ctx, model, item -> {
                Object value = pathParam.getPath().evaluate(item, model, ctx.configuration()).getValue();
                return this.arrayIterableToList(ctx, value, obj -> null);
            });
            if (null == list || list.size() == 0) {
                continue;
            }
            resultList.addAll(list);
        }
        return resultList;
    }


    /**
     * get results by params
     *
     * @param ctx
     * @param parameters
     * @return
     */
    protected Collection<String> joinByParams(EvaluationContext ctx, List<Parameter> parameters) {
        if (null == parameters || parameters.size() == 0) {
            return null;
        }
        List<Parameter> notJsonParams = parameters.stream().filter(item -> ParamType.PATH.equals(item.getType())).collect(Collectors.toList());
        if (notJsonParams.size() == 0) {
            return parameters.stream().map(Parameter::getValue).filter(Objects::nonNull).map(Object::toString).collect(Collectors.toList());
        }
        List<String> list = Parameter.toList(String.class, ctx, notJsonParams);
        return list.size() > 0 ? list : null;
    }

    /**
     * get delimiter
     *
     * @param parameters
     * @return
     */
    protected String getDelimiterFromParameters(List<Parameter> parameters) {
        if (null != parameters && parameters.size() >= 1) {
            Parameter parameter = parameters.get(0);
            if (ParamType.JSON.equals(parameter.getType())) {
                return parameter.getValue().toString();
            }
        }
        return ",";
    }


    /**
     * literal quantity type
     *
     * @param obj
     * @return
     */
    protected boolean simpleType(Object obj) {
        if (null == obj) {
            return false;
        }
        return obj instanceof Number || obj instanceof CharSequence ||
                obj instanceof Boolean || obj instanceof Character;
    }


    /**
     * iterable
     *
     * @param ctx
     * @param model
     * @param itemIsNotSimpleFunc
     */
    protected List<String> arrayIterableToList(EvaluationContext ctx, Object model, Function<Object, List<String>> itemIsNotSimpleFunc) {
        if (null == model) {
            return Collections.emptyList();
        }

        if (this.simpleType(model)) {
            return Collections.singletonList(model.toString());
        }

        boolean isArray = ctx.configuration().jsonProvider().isArray(model);
        if (!isArray) {
            return Collections.emptyList();
        }

        List<String> resultList = new ArrayList<>();
        Iterable<?> iterable = ctx.configuration().jsonProvider().toIterable(model);
        for (Object obj : iterable) {
            if (null == obj) {
                continue;
            }

            boolean isSimpleType = this.simpleType(obj);
            if (isSimpleType) {
                resultList.add(obj.toString());
                continue;
            }

            List<String> items = itemIsNotSimpleFunc.apply(obj);
            if (null == items || items.size() == 0) {
                continue;
            }
            resultList.addAll(items);
        }
        return resultList;
    }
}
