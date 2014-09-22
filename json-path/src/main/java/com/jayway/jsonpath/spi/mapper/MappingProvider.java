package com.jayway.jsonpath.spi.mapper;

import com.jayway.jsonpath.Configuration;

/**
 * Maps object between different Types
 */
public interface MappingProvider {


    /**
     *
     * @param source object to map
     * @param targetType the type the source object should be mapped to
     * @param configuration current configuration
     * @param <T> the mapped result type
     * @return return the mapped object
     */
    <T> T map(Object source, Class<T> targetType, Configuration configuration);
}
