package com.jayway.jsonpath.spi.mapper;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.TypeRef;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JsonOrgMappingProvider implements MappingProvider {
    @Override
    public <T> T map(Object source, Class<T> targetType, Configuration configuration) {
        if(source == null){
            return null;
        }
        if(targetType.equals(Object.class) || targetType.equals(List.class) || targetType.equals(Map.class)){
            return (T) mapToObject(source);
        }
        return (T)source;
    }

    @Override
    public <T> T map(Object source, TypeRef<T> targetType, Configuration configuration) {
        throw new UnsupportedOperationException("JsonOrg provider does not support TypeRef! Use a Jackson or Gson based provider");
    }


    private Object mapToObject(Object source){
        if(source instanceof JSONArray){
            List<Object> mapped = new ArrayList<Object>();
            JSONArray array = (JSONArray) source;

            for (int i = 0; i < array.length(); i++){
                mapped.add(mapToObject(array.get(i)));
            }

            return mapped;
        }
        else if (source instanceof JSONObject){
            Map<String, Object> mapped = new HashMap<String, Object>();
            JSONObject obj = (JSONObject) source;

            for (Object o : obj.keySet()) {
                String key = o.toString();
                mapped.put(key, mapToObject(obj.get(key)));
            }
            return mapped;
        }
        else if (source == JSONObject.NULL){
            return null;
        } else {
            return source;
        }
    }
}
