package com.jayway.jsonpath.internal.spi.mapper;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.spi.mapper.MappingProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;

public class DefaultMappingProvider implements MappingProvider {

    private static final Logger logger = LoggerFactory.getLogger(DefaultMappingProvider.class);

    private HashMap<Class<?>, HashMap<Class<?>, Mapper>> converters = new HashMap<Class<?>, HashMap<Class<?>, Mapper>>();

    public DefaultMappingProvider(){
        addMapper(new NumberMapper());
        addMapper(new StringMapper());
        addMapper(new DateMapper());
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
            mapper = targetConverters.get(Object.class);
            if(mapper != null){
                return (T) mapper.convert(source, source.getClass(), targetType, configuration);
            }
        }
        return (T)source;
    }
}
