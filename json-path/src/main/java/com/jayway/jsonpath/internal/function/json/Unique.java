package com.jayway.jsonpath.internal.function.json;

import com.jayway.jsonpath.internal.EvaluationContext;
import com.jayway.jsonpath.internal.PathRef;
import com.jayway.jsonpath.internal.function.Parameter;
import com.jayway.jsonpath.internal.function.PathFunction;
import net.minidev.json.JSONArray;

import java.util.List;
import java.util.Iterator;
import java.util.HashMap;

public class Unique implements PathFunction {
    @Override
    public Object invoke(String currentPath, PathRef parent, Object model, EvaluationContext ctx, List<Parameter> parameters) {
        if (null != parameters && parameters.size() > 0) {
            Object value = parameters.get(0).getValue();
            // the value should be list
            if (!(value instanceof List)){
                return value;
            }
            return unique(((List) value).iterator());
        }
        if (ctx.configuration().jsonProvider().isArray(model)) {
            return unique(ctx.configuration().jsonProvider().toIterable(model).iterator());
        } else if(ctx.configuration().jsonProvider().isMap(model)){
            return model;
        }else{
            return model;
        }
    }

    /**
     * Do unique operation using HashMap.
     * @param iterator iterator
     * @return JSONArray
     */
    private JSONArray unique(Iterator<?> iterator){
        HashMap<Integer,Object> objectHashMap = new HashMap<>();
        int answerPosition = 0;
        // make sure it is in order by using index as key, object as value.
        while(iterator.hasNext()){
            Object obj = iterator.next();
            if(!objectHashMap.containsValue(obj)){
                objectHashMap.put(answerPosition++,obj);
            }
        }
        JSONArray result = new JSONArray();
        for(int n = 0;n<objectHashMap.size();n++){
            result.add(objectHashMap.get(n));
        }
        return result;
    }

}
