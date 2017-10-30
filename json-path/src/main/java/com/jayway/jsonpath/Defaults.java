package com.jayway.jsonpath;

import java.util.Set;

import com.jayway.jsonpath.spi.builder.NodeBuilder;
import com.jayway.jsonpath.spi.json.JsonProvider;
import com.jayway.jsonpath.spi.mapper.MappingProvider;

public interface Defaults {
    /**
     * Returns the default {@link com.jayway.jsonpath.spi.json.JsonProvider}
     * @return default json provider
     */
    JsonProvider jsonProvider();

    /**
     * Returns default setOptions
     * @return setOptions
     */
    Set<Option> options();

    /**
     * Returns the default {@link com.jayway.jsonpath.spi.mapper.MappingProvider}
     *
     * @return default mapping provider
     */
    MappingProvider mappingProvider();

    
    /**
     * Returns the default {@link com.jayway.jsonpath.spi.builder.NodeBuilder}
     * 
     * @return the default ValueNode builder
     */
    NodeBuilder nodeBuilder();

}