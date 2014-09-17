package com.jayway.jsonpath.internal.spi.converter;

import com.jayway.jsonpath.spi.converter.ConversionException;
import com.jayway.jsonpath.spi.converter.Converter;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public abstract class ConverterBase implements Converter{

    private final Set<Converter.ConvertiblePair> convertiblePairs = new HashSet<Converter.ConvertiblePair>();

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
            throw new ConversionException("Source: " + src.getClass() + " is not assignable from: " + srcType.getName());
        }
        if(!canConvert(srcType, targetType)){
            throw new ConversionException("Can not convert: " + srcType.getName() + " to: " + targetType.getName());
        }
    }

    boolean canConvert(Class<?> srcType, Class<?> targetType){
        return convertiblePairs.contains(new Converter.ConvertiblePair(srcType, targetType));
    }
}
