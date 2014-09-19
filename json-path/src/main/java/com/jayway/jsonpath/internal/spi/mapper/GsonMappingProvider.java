package com.jayway.jsonpath.internal.spi.mapper;

import com.jayway.jsonpath.spi.mapper.MappingProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GsonMappingProvider extends DefaultMappingProvider implements MappingProvider {

    private static final Logger logger = LoggerFactory.getLogger(GsonMappingProvider.class);

    public GsonMappingProvider() {
        super();
        try {
            Class.forName("com.google.gson.Gson");
            addMapper(new GsonMapper());
        } catch (ClassNotFoundException e) {
            logger.error("Gson not found on class path. No converters configured.");
            throw new RuntimeException("Gson not found on path", e);
        }
    }
}
