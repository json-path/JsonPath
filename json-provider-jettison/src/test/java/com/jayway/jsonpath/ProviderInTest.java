package com.jayway.jsonpath;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.assertj.core.util.Lists;
import org.junit.Test;

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
    public void testJsonPathQuotes() throws Exception {
        final Configuration jackson = Configuration.defaultConfiguration();
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

}