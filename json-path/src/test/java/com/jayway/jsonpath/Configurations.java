package com.jayway.jsonpath;

import com.jayway.jsonpath.spi.json.GsonJsonProvider;
import com.jayway.jsonpath.spi.json.JacksonJsonNodeJsonProvider;
import com.jayway.jsonpath.spi.json.JacksonJsonProvider;
import com.jayway.jsonpath.spi.json.JakartaJsonProvider;
import com.jayway.jsonpath.spi.json.JsonOrgJsonProvider;
import com.jayway.jsonpath.spi.json.JsonSmartJsonProvider;
import com.jayway.jsonpath.spi.mapper.GsonMappingProvider;
import com.jayway.jsonpath.spi.mapper.JacksonMappingProvider;
import com.jayway.jsonpath.spi.mapper.JakartaMappingProvider;
import com.jayway.jsonpath.spi.mapper.JsonOrgMappingProvider;
import com.jayway.jsonpath.spi.mapper.JsonSmartMappingProvider;

import java.util.Arrays;

public class Configurations {

    private static final Configuration JSON_ORG_CONFIGURATION = Configuration
            .builder()
            .mappingProvider(new JsonOrgMappingProvider())
            .jsonProvider(new JsonOrgJsonProvider())
            .build();

    private static final Configuration GSON_CONFIGURATION = Configuration
            .builder()
            .mappingProvider(new GsonMappingProvider())
            .jsonProvider(new GsonJsonProvider())
            .build();

    private static final Configuration JACKSON_CONFIGURATION = Configuration
            .builder()
            .mappingProvider(new JacksonMappingProvider())
            .jsonProvider(new JacksonJsonProvider())
            .build();

    private static final Configuration JACKSON_JSON_NODE_CONFIGURATION = Configuration
            .builder()
            .mappingProvider(new JacksonMappingProvider())
            .jsonProvider(new JacksonJsonNodeJsonProvider())
            .build();

    private static final Configuration JSON_SMART_CONFIGURATION = Configuration
            .builder()
            .mappingProvider(new JsonSmartMappingProvider())
            .jsonProvider(new JsonSmartJsonProvider())
            .build();

    private static final Configuration JAKARTA_CONFIGURATION = Configuration
            .builder()
            .mappingProvider(new JakartaMappingProvider())
            .jsonProvider(new JakartaJsonProvider())
            .build();

    public static Iterable<Configuration> configurations() {
        return Arrays.asList(
                JSON_SMART_CONFIGURATION,
                GSON_CONFIGURATION,
                JACKSON_CONFIGURATION,
                JACKSON_JSON_NODE_CONFIGURATION,
                JSON_ORG_CONFIGURATION,
                JAKARTA_CONFIGURATION
        );
    }

    public static Iterable<Configuration> objectMappingConfigurations() {
        return Arrays.asList(
                GSON_CONFIGURATION,
                JACKSON_CONFIGURATION,
                JACKSON_JSON_NODE_CONFIGURATION,
                JAKARTA_CONFIGURATION
        );
    }

    // Public getter methods for accessing configurations
    public static Configuration getJsonOrgConfiguration() {
        return JSON_ORG_CONFIGURATION;
    }

    public static Configuration getGsonConfiguration() {
        return GSON_CONFIGURATION;
    }

    public static Configuration getJacksonConfiguration() {
        return JACKSON_CONFIGURATION;
    }

    public static Configuration getJacksonJsonNodeConfiguration() {
        return JACKSON_JSON_NODE_CONFIGURATION;
    }

    public static Configuration getJsonSmartConfiguration() {
        return JSON_SMART_CONFIGURATION;
    }

    public static Configuration getJakartaConfiguration() {
        return JAKARTA_CONFIGURATION;
    }
}
