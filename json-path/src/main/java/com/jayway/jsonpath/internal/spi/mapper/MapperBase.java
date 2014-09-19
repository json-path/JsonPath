package com.jayway.jsonpath.internal.spi.mapper;

import com.jayway.jsonpath.spi.mapper.MappingException;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public abstract class MapperBase implements Mapper {

    private final Set<Mapper.ConvertiblePair> convertiblePairs = new HashSet<Mapper.ConvertiblePair>();

    protected void register(Class<?> srcType, Class<?> targetType){
        convertiblePairs.add(new ConvertiblePair(srcType, targetType));
    }

    @Override
    public Set<ConvertiblePair> getConvertibleTypes() {
        return Collections.unmodifiableSet(convertiblePairs);
    }

    void assertValidConversion(Object src, Class<?> srcType, Class<?> targetType) {

        if (src == null) {
            return;
        }

        if (!srcType.isAssignableFrom(src.getClass())) {
            throw new MappingException("Source: " + src.getClass() + " is not assignable from: " + srcType.getName());
        }
        if(!canConvert(srcType, targetType)){
            throw new MappingException("Can not map: " + srcType.getName() + " to: " + targetType.getName());
        }
    }

    boolean canConvert(Class<?> srcType, Class<?> targetType){
        return convertiblePairs.contains(new Mapper.ConvertiblePair(srcType, targetType));
    }
}
