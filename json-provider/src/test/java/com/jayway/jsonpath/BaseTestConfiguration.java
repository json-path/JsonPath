package com.jayway.jsonpath;

import com.jayway.jsonpath.internal.Path;
import com.jayway.jsonpath.internal.path.PredicateContextImpl;
import com.jayway.jsonpath.spi.builder.JsonSmartNodeBuilder;
import com.jayway.jsonpath.spi.json.GsonJsonProvider;
import com.jayway.jsonpath.spi.json.JacksonJsonNodeJsonProvider;
import com.jayway.jsonpath.spi.json.JacksonJsonProvider;
import com.jayway.jsonpath.spi.json.JettisonProvider;
import com.jayway.jsonpath.spi.json.JsonOrgJsonProvider;
import com.jayway.jsonpath.spi.json.JsonSmartJsonProvider;
import com.jayway.jsonpath.spi.json.TapestryJsonProvider;
import com.jayway.jsonpath.spi.mapper.GsonMappingProvider;
import com.jayway.jsonpath.spi.mapper.JacksonMappingProvider;
import com.jayway.jsonpath.spi.mapper.JsonOrgMappingProvider;
import com.jayway.jsonpath.spi.mapper.JsonSmartMappingProvider;
import com.jayway.jsonpath.spi.mapper.TapestryMappingProvider;

import java.util.Arrays;
import java.util.HashMap;

public class BaseTestConfiguration {

    public static final Configuration JSON_ORG_CONFIGURATION = Configuration
            .builder()
            .mappingProvider(new JsonOrgMappingProvider())
            .jsonProvider(new JsonOrgJsonProvider())
            .nodeBuilder(new JsonSmartNodeBuilder())
            .build();

    public static final Configuration GSON_CONFIGURATION = Configuration
            .builder()
            .mappingProvider(new GsonMappingProvider())
            .jsonProvider(new GsonJsonProvider())
            .nodeBuilder(new JsonSmartNodeBuilder())
            .build();

    public static final Configuration JACKSON_CONFIGURATION = Configuration
            .builder()
            .mappingProvider(new JacksonMappingProvider())
            .jsonProvider(new JacksonJsonProvider())
            .nodeBuilder(new JsonSmartNodeBuilder())
            .build();

    public static final Configuration JACKSON_JSON_NODE_CONFIGURATION = Configuration
            .builder()
            .mappingProvider(new JacksonMappingProvider())
            .jsonProvider(new JacksonJsonNodeJsonProvider())
            .nodeBuilder(new JsonSmartNodeBuilder())
            .build();

    public static final Configuration JETTISON_CONFIGURATION = Configuration
            .builder()
            .mappingProvider(new JsonSmartMappingProvider())
            .jsonProvider(new JettisonProvider())
            .nodeBuilder(new JsonSmartNodeBuilder())
            .build();

    public static final Configuration JSON_SMART_CONFIGURATION = Configuration
            .builder()
            .mappingProvider(new JsonSmartMappingProvider())
            .jsonProvider(new JsonSmartJsonProvider())
            .nodeBuilder(new JsonSmartNodeBuilder())
            .build();
    
    public static final Configuration TAPESTRY_JSON_CONFIGURATION = Configuration
        .builder()
        .mappingProvider(new TapestryMappingProvider())
        .jsonProvider(TapestryJsonProvider.INSTANCE)
        .nodeBuilder(new JsonSmartNodeBuilder())
        .build();

    
    public static Iterable<Configuration> configurations() {
        return Arrays.asList(
               JSON_SMART_CONFIGURATION
               ,GSON_CONFIGURATION
               ,JACKSON_CONFIGURATION
               ,JACKSON_JSON_NODE_CONFIGURATION
               ,JSON_ORG_CONFIGURATION
        );
    }

    public static Iterable<Configuration> objectMappingConfigurations() {
        return Arrays.asList(
                 GSON_CONFIGURATION
                ,JACKSON_CONFIGURATION
                ,JACKSON_JSON_NODE_CONFIGURATION
        );
    }

    public static Predicate.PredicateContext createPredicateContext(final Object check) {
        return new PredicateContextImpl(check, check, 
        		Configuration.defaultConfiguration(), 
        		new HashMap<Path, Object>());
    }
}
