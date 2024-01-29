package com.jayway.jsonpath;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.google.gson.JsonArray;
import com.jayway.jsonpath.spi.json.*;
import com.jayway.jsonpath.spi.mapper.*;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ProviderInTest {
    private final String JSON = "[{\"foo\": \"bar\"}, {\"foo\": \"baz\"}]";
    private final String EQUALS_FILTER = "$.[?(@.foo == %s)].foo";
    private final String IN_FILTER = "$.[?(@.foo in [%s])].foo";
    private final String DOUBLE_QUOTES = "\"bar\"";
    private final String DOUBLE_QUOTES_EQUALS_FILTER = String.format(EQUALS_FILTER, DOUBLE_QUOTES);
    private final String DOUBLE_QUOTES_IN_FILTER = String.format(IN_FILTER, DOUBLE_QUOTES);
    private final String SINGLE_QUOTES = "'bar'";
    private final String SINGLE_QUOTES_EQUALS_FILTER = String.format(EQUALS_FILTER, SINGLE_QUOTES);
    private final String SINGLE_QUOTES_IN_FILTER = String.format(IN_FILTER, SINGLE_QUOTES);


    @Test
    public void testJsonPathQuotesJackson() {
        final Configuration jackson = Configuration.builder().jsonProvider(new JacksonJsonProvider()).mappingProvider(new JacksonMappingProvider()).build();
        final DocumentContext ctx = JsonPath.using(jackson).parse(JSON);

        final List<String> doubleQuoteEqualsResult = ctx.read(DOUBLE_QUOTES_EQUALS_FILTER);
        assertEquals(Lists.newArrayList("bar"), doubleQuoteEqualsResult);

        final List<String> singleQuoteEqualsResult = ctx.read(SINGLE_QUOTES_EQUALS_FILTER);
        assertEquals(doubleQuoteEqualsResult, singleQuoteEqualsResult);

        final List<String> doubleQuoteInResult = ctx.read(DOUBLE_QUOTES_IN_FILTER);
        assertEquals(doubleQuoteInResult, doubleQuoteEqualsResult);

        final List<String> singleQuoteInResult = ctx.read(SINGLE_QUOTES_IN_FILTER);
        assertEquals(doubleQuoteInResult, singleQuoteInResult);
    }


    @Test
    public void testJsonPathQuotesJacksonJsonNode() {
        final Configuration jacksonJsonNode = Configuration.builder().jsonProvider(new JacksonJsonNodeJsonProvider()).mappingProvider(new JacksonMappingProvider()).build();
        final DocumentContext ctx = JsonPath.using(jacksonJsonNode).parse(JSON);

        final ArrayNode doubleQuoteEqualsResult = ctx.read(DOUBLE_QUOTES_EQUALS_FILTER);
        assertEquals("bar", doubleQuoteEqualsResult.get(0).asText());

        final ArrayNode singleQuoteEqualsResult = ctx.read(SINGLE_QUOTES_EQUALS_FILTER);
        assertEquals(doubleQuoteEqualsResult, singleQuoteEqualsResult);

        final ArrayNode doubleQuoteInResult = ctx.read(DOUBLE_QUOTES_IN_FILTER);
        assertEquals(doubleQuoteInResult, doubleQuoteEqualsResult);

        final ArrayNode singleQuoteInResult = ctx.read(SINGLE_QUOTES_IN_FILTER);
        assertEquals(doubleQuoteInResult, singleQuoteInResult);
    }

    @Test
    public void testJsonPathQuotesGson() {
        final Configuration gson = Configuration.builder().jsonProvider(new GsonJsonProvider()).mappingProvider(new GsonMappingProvider()).build();
        final DocumentContext ctx = JsonPath.using(gson).parse(JSON);

        final JsonArray doubleQuoteEqualsResult = ctx.read(DOUBLE_QUOTES_EQUALS_FILTER);
        assertEquals("bar", doubleQuoteEqualsResult.get(0).getAsString());

        final JsonArray singleQuoteEqualsResult = ctx.read(SINGLE_QUOTES_EQUALS_FILTER);
        assertEquals(doubleQuoteEqualsResult, singleQuoteEqualsResult);

        final JsonArray doubleQuoteInResult = ctx.read(DOUBLE_QUOTES_IN_FILTER);
        assertEquals(doubleQuoteInResult, doubleQuoteEqualsResult);

        final JsonArray singleQuoteInResult = ctx.read(SINGLE_QUOTES_IN_FILTER);
        assertEquals(doubleQuoteInResult, singleQuoteInResult);
    }

    @Test
    public void testJsonPathQuotesJsonOrg() {
        final Configuration jsonOrg = Configuration.builder().jsonProvider(new JsonOrgJsonProvider()).mappingProvider(new JsonOrgMappingProvider()).build();
        final DocumentContext ctx = JsonPath.using(jsonOrg).parse(JSON);

        final org.json.JSONArray doubleQuoteEqualsResult = ctx.read(DOUBLE_QUOTES_EQUALS_FILTER);
        assertEquals("bar", doubleQuoteEqualsResult.get(0));

        final org.json.JSONArray singleQuoteEqualsResult = ctx.read(SINGLE_QUOTES_EQUALS_FILTER);
        assertEquals(doubleQuoteEqualsResult.get(0), singleQuoteEqualsResult.get(0));

        final org.json.JSONArray doubleQuoteInResult = ctx.read(DOUBLE_QUOTES_IN_FILTER);
        assertEquals(doubleQuoteInResult.get(0), doubleQuoteEqualsResult.get(0));

        final org.json.JSONArray singleQuoteInResult = ctx.read(SINGLE_QUOTES_IN_FILTER);
        assertEquals(doubleQuoteInResult.get(0), singleQuoteInResult.get(0));
    }

    @Test
    public void testJsonPathQuotesJsonSmart() {
        final Configuration jsonSmart = Configuration.builder().jsonProvider(new JsonSmartJsonProvider()).mappingProvider(new JsonSmartMappingProvider()).build();
        final DocumentContext ctx = JsonPath.using(jsonSmart).parse(JSON);

        final net.minidev.json.JSONArray doubleQuoteEqualsResult = ctx.read(DOUBLE_QUOTES_EQUALS_FILTER);
        assertEquals("bar", doubleQuoteEqualsResult.get(0));

        final net.minidev.json.JSONArray singleQuoteEqualsResult = ctx.read(SINGLE_QUOTES_EQUALS_FILTER);
        assertEquals(doubleQuoteEqualsResult, singleQuoteEqualsResult);

        final net.minidev.json.JSONArray doubleQuoteInResult = ctx.read(DOUBLE_QUOTES_IN_FILTER);
        assertEquals(doubleQuoteInResult, doubleQuoteEqualsResult);

        final net.minidev.json.JSONArray singleQuoteInResult = ctx.read(SINGLE_QUOTES_IN_FILTER);
        assertEquals(doubleQuoteInResult, singleQuoteInResult);
    }

    @Test
    public void testJsonPathQuotesJakarta() {
        final Configuration gson = Configuration.builder().jsonProvider(new JakartaJsonProvider()).mappingProvider(new JakartaMappingProvider()).build();
        final DocumentContext ctx = JsonPath.using(gson).parse(JSON);

        final List<jakarta.json.JsonValue> doubleQuoteEqualsResult = ctx.read(DOUBLE_QUOTES_EQUALS_FILTER);
        // returned string is surrounded by double quotes in Jakarta EE JSON-P implementations 
        assertEquals("bar", ((jakarta.json.JsonString) doubleQuoteEqualsResult.get(0)).getChars());

        final List<jakarta.json.JsonValue> singleQuoteEqualsResult = ctx.read(SINGLE_QUOTES_EQUALS_FILTER);
        assertEquals(doubleQuoteEqualsResult, singleQuoteEqualsResult);

        final List<jakarta.json.JsonValue> doubleQuoteInResult = ctx.read(DOUBLE_QUOTES_IN_FILTER);
        assertEquals(doubleQuoteInResult, doubleQuoteEqualsResult);

        final List<jakarta.json.JsonValue> singleQuoteInResult = ctx.read(SINGLE_QUOTES_IN_FILTER);
        assertEquals(doubleQuoteInResult, singleQuoteInResult);
    }
}