package com.jayway.jsonpath.spi.mapper;

import com.jayway.jsonpath.Configuration;

public interface MappingProvider {
    <T> T map(Object source, Class<T> targetType, Configuration configuration);
}
