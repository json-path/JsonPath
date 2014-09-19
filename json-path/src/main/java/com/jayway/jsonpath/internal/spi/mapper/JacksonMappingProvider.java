package com.jayway.jsonpath.internal.spi.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.spi.mapper.MappingProvider;

import java.util.HashMap;

public class JacksonMappingProvider implements MappingProvider {

    private final ObjectMapper objectMapper;

    private HashMap<Class<?>, HashMap<Class<?>, Mapper>> converters = new HashMap<Class<?>, HashMap<Class<?>, Mapper>>();

    public JacksonMappingProvider() {
        this(new ObjectMapper());
    }

    public JacksonMappingProvider(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }
    public void addMapper(Mapper converter) {
        for (Mapper.ConvertiblePair convertible : converter.getConvertibleTypes()) {
            if(!converters.containsKey(convertible.getTargetType())){
                converters.put(convertible.getTargetType(), new HashMap<Class<?>, Mapper>());
            }
            converters.get(convertible.getTargetType()).put(convertible.getSourceType(), converter);
        }
    }


    @Override
    public <T> T map(Object source, Class<T> targetType, Configuration configuration) {
        if(source == null){
            return null;
        }
        HashMap<Class<?>, Mapper> targetConverters = converters.get(targetType);
        if(targetConverters != null){
            Mapper mapper = targetConverters.get(source.getClass());
            if(mapper != null){
                return (T) mapper.convert(source, source.getClass(), targetType, configuration);
            }
        }
        return objectMapper.convertValue(source, targetType);
    }
}
