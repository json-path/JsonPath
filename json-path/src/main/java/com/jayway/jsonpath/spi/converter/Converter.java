package com.jayway.jsonpath.spi.converter;

import com.jayway.jsonpath.Configuration;

public interface Converter<T>{
    T convert(Object o, Configuration conf);
}