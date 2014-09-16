package com.jayway.jsonpath.internal.spi.converter;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.spi.converter.Converter;

public class StringConverter extends ConverterBase {

    public StringConverter() {
        register(Object.class, String.class);
    }

    @Override
    public Object convert(Object src, Class<?> srcType, Class<?> targetType, Configuration conf) {
        assertValidConversion(src, srcType, targetType);

        if (src == null) {
            return null;
        }
        return src.toString();
    }

    @Override
    boolean canConvert(Class<?> srcType, Class<?> targetType){
        return true;
    }
}
