package com.jayway.jsonpath.spi;

import com.jayway.jsonpath.spi.impl.JacksonProvider;

/**
 * Created by IntelliJ IDEA.
 * User: kallestenflo
 * Date: 3/5/12
 * Time: 11:03 AM
 */
public class MappingProviderFactory {

    private static MappingProvider mappingProvider;

    static {
        try {
            Class.forName("org.codehaus.jackson.map.ObjectMapper");

            mappingProvider = new JacksonProvider();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("org.codehaus.jackson.map.ObjectMapper not found on classpath. This is an optional dependency needed for POJO conversions.", e);
        }
    }




    public static MappingProvider getInstance() {
        return mappingProvider;
    }
}
