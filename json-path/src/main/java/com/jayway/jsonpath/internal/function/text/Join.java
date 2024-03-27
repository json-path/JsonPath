package com.jayway.jsonpath.internal.function.text;

import com.jayway.jsonpath.internal.EvaluationContext;
import com.jayway.jsonpath.internal.PathRef;
import com.jayway.jsonpath.internal.function.Parameter;
import com.jayway.jsonpath.internal.function.PathFunction;

import java.util.List;

public class Join implements PathFunction {
    @Override
    public Object invoke(String currentPath, PathRef parent, Object model, EvaluationContext ctx, List<Parameter> parameters) {
        StringBuilder result = new StringBuilder();

        List<String> strings = null;
        String separator = " ";
        if (null != parameters){
            strings = Parameter.toList(String.class, ctx, parameters);
            if (strings.size() == 0)
                separator = " ";
            else
                separator = strings.remove(strings.size() - 1);
        }

        if(ctx.configuration().jsonProvider().isArray(model)){
            Iterable<?> objects = ctx.configuration().jsonProvider().toIterable(model);
            for (Object obj : objects) {
                if (obj instanceof String) {
                    result.append(obj.toString());
                    result.append(separator);
                }
            }
        }

        if (null != strings){
            for (String string : strings){
                result.append(string);
                result.append(separator);
            }
        }

        if (result.length() > 0)
            return result.delete(result.length() - separator.length(), result.length()).toString();
        else
            return result;
    }
}
