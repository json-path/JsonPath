package com.jayway.jsonpath;

import com.jayway.jsonpath.internal.Path;
import com.jayway.jsonpath.internal.path.PredicateContextImpl;
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

import java.util.HashMap;

public class BaseTest {

    public static final Configuration JSON_ORG_CONFIGURATION = Configuration
            .builder()
            .mappingProvider(new JsonOrgMappingProvider())
            .jsonProvider(new JsonOrgJsonProvider())
            .build();

    public static final Configuration GSON_CONFIGURATION = Configuration
            .builder()
            .mappingProvider(new GsonMappingProvider())
            .jsonProvider(new GsonJsonProvider())
            .build();

    public static final Configuration JACKSON_CONFIGURATION = Configuration
            .builder()
            .mappingProvider(new JacksonMappingProvider())
            .jsonProvider(new JacksonJsonProvider())
            .build();

    public static final Configuration JACKSON_JSON_NODE_CONFIGURATION = Configuration
            .builder()
            .mappingProvider(new JacksonMappingProvider())
            .jsonProvider(new JacksonJsonNodeJsonProvider())
            .build();

    public static final Configuration JETTISON_CONFIGURATION = Configuration
            .builder()
            .jsonProvider(new JettisonProvider())
            .build();

    public static final Configuration JSON_SMART_CONFIGURATION = Configuration
            .builder()
            .mappingProvider(new JsonSmartMappingProvider())
            .jsonProvider(new JsonSmartJsonProvider())
            .build();
    
    public static final Configuration TAPESTRY_JSON_CONFIGURATION = Configuration
        .builder()
        .mappingProvider(new TapestryMappingProvider())
        .jsonProvider(TapestryJsonProvider.INSTANCE)
        .build();

    public static final String JSON_BOOK_DOCUMENT =
            "{ " +
            "   \"category\" : \"reference\",\n" +
            "   \"author\" : \"Nigel Rees\",\n" +
            "   \"title\" : \"Sayings of the Century\",\n" +
            "   \"display-price\" : 8.95\n" +
            "}";
    public static final String JSON_DOCUMENT = "{\n" +
            "   \"string-property\" : \"string-value\", \n" +
            "   \"int-max-property\" : " + Integer.MAX_VALUE + ", \n" +
            "   \"long-max-property\" : " + Long.MAX_VALUE + ", \n" +
            "   \"boolean-property\" : true, \n" +
            "   \"null-property\" : null, \n" +
            "   \"int-small-property\" : 1, \n" +
            "   \"max-price\" : 10, \n" +
            "   \"store\" : {\n" +
            "      \"book\" : [\n" +
            "         {\n" +
            "            \"category\" : \"reference\",\n" +
            "            \"author\" : \"Nigel Rees\",\n" +
            "            \"title\" : \"Sayings of the Century\",\n" +
            "            \"display-price\" : 8.95\n" +
            "         },\n" +
            "         {\n" +
            "            \"category\" : \"fiction\",\n" +
            "            \"author\" : \"Evelyn Waugh\",\n" +
            "            \"title\" : \"Sword of Honour\",\n" +
            "            \"display-price\" : 12.99\n" +
            "         },\n" +
            "         {\n" +
            "            \"category\" : \"fiction\",\n" +
            "            \"author\" : \"Herman Melville\",\n" +
            "            \"title\" : \"Moby Dick\",\n" +
            "            \"isbn\" : \"0-553-21311-3\",\n" +
            "            \"display-price\" : 8.99\n" +
            "         },\n" +
            "         {\n" +
            "            \"category\" : \"fiction\",\n" +
            "            \"author\" : \"J. R. R. Tolkien\",\n" +
            "            \"title\" : \"The Lord of the Rings\",\n" +
            "            \"isbn\" : \"0-395-19395-8\",\n" +
            "            \"display-price\" : 22.99\n" +
            "         }\n" +
            "      ],\n" +
            "      \"bicycle\" : {\n" +
            "         \"foo\" : \"baz\",\n" +
            "         \"escape\" : \"Esc\\b\\f\\n\\r\\t\\n\\t\\u002A\",\n" +
            "         \"color\" : \"red\",\n" +
            "         \"display-price\" : 19.95,\n" +
            "         \"foo:bar\" : \"fooBar\",\n" +
            "         \"dot.notation\" : \"new\",\n" +
            "         \"dash-notation\" : \"dashes\"\n" +
            "      }\n" +
            "   },\n" +
            "   \"foo\" : \"bar\",\n" +
            "   \"@id\" : \"ID\"\n" +
            "}";

    public static String JSON_BOOK_STORE_DOCUMENT = "{\n" +
            "    \"store\": {\n" +
            "        \"book\": [\n" +
            "            {\n" +
            "                \"category\": \"reference\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"category\": \"fiction\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"category\": \"fiction\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"category\": \"fiction\"\n" +
            "            }\n" +
            "        ]\n" +
            "    },\n" +
            "    \"expensive\": 10\n" +
            "}";

    public Predicate.PredicateContext createPredicateContext(final Object check) {

        return new PredicateContextImpl(check, check, Configuration.defaultConfiguration(), new HashMap<Path, Object>());
    }
}
