package com.jayway.jsonpath.spi.converter;

import com.jayway.jsonpath.Configuration;

public interface ConversionProvider {

    boolean canConvert(Class<?> sourceType, Class<?> targetType);

    <T> T convert(Object source, Class<T> targetType, Configuration configuration);
}
