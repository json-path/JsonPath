package com.jayway.jsonpath.internal.spi.mapper;

import com.jayway.jsonpath.Configuration;

public class StringMapper extends MapperBase {

    public StringMapper() {
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
