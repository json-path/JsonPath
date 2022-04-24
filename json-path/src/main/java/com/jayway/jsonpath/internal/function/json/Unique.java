package com.jayway.jsonpath.internal.function.json;

import com.jayway.jsonpath.internal.EvaluationContext;
import com.jayway.jsonpath.internal.PathRef;
import com.jayway.jsonpath.internal.function.Parameter;
import com.jayway.jsonpath.internal.function.PathFunction;
import net.minidev.json.JSONArray;

import java.util.*;

public class Unique implements PathFunction {
    @Override
    public Object invoke(String currentPath, PathRef parent, Object model, EvaluationContext ctx, List<Parameter> parameters) {
        if (null != parameters && parameters.size() > 0) {
            Object result = parameters.get(0).getValue();
            if (!(result instanceof JSONArray)){
                return result;
            }
            JSONArray resultArray = (JSONArray) result;
            int length = resultArray.size();
            HashMap<Integer,Object> objectHashMap = new HashMap<>();
            int i = 0;
            for(int n = 0;n<length;n++){
                if(!objectHashMap.containsValue(resultArray.get(n))){
                    objectHashMap.put(i,resultArray.get(n));
                    i++;
                }
            }
            length = objectHashMap.size();
            ArrayList<Object> arrayList = new ArrayList<>(length);
            for(int n = 0;n<length;n++){
                arrayList.add(objectHashMap.get(n));
            }
            JSONArray jsonArray = new JSONArray();
            jsonArray.addAll(arrayList);
            return jsonArray;
        }
        if (ctx.configuration().jsonProvider().isArray(model)) {
            Iterable<?> objects = ctx.configuration().jsonProvider().toIterable(model);
            HashMap<Integer,Object> objectHashMap = new HashMap<>();
            HashMap<Integer,String> objectStringHashMap = new HashMap<>();
            int n = 0;
            for(Object o : objects){
                if(!objectStringHashMap.containsValue(o.toString())){
                    objectHashMap.put(n,o);
                    objectStringHashMap.put(n,o.toString());
                    n++;
                }
            }
            int length = n;
            ArrayList<Object> arrayList = new ArrayList<>(length);
            for(n = 0;n<length;n++){
                arrayList.add(objectHashMap.get(n));
            }
            JSONArray jsonArray = new JSONArray();
            jsonArray.addAll(arrayList);
            return jsonArray;
        } else if(ctx.configuration().jsonProvider().isMap(model)){
            return model;
        }else{
            return model;
        }
    }
}
