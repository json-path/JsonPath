package com.jayway.jsonpath.internal.function;

import static com.jayway.jsonpath.JsonPath.using;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.Configurations;
import java.util.Arrays;

import com.jayway.jsonpath.spi.json.JacksonJsonProvider;
import com.jayway.jsonpath.spi.mapper.JacksonMappingProvider;
import org.junit.Test;

/**
 * Test cases for functions
 * 
 * -first
 * -last
 * -index(X)
 * -distinct
 *
 * Created by git9527 on 6/11/22.
 */
public class SequentialPathFunctionTest extends BaseFunctionTest {

    private Configuration conf = Configurations.JACKSON_CONFIGURATION;

    @Test
    public void testFirstOfNumbers() throws Exception {
        verifyFunction(conf, "$.numbers.first()", NUMBER_SERIES, 1);
    }
    
    @Test
    public void testLastOfNumbers() throws Exception {
        verifyFunction(conf, "$.numbers.last()", NUMBER_SERIES, 10);
    }
    
    @Test
    public void testIndexOfNumbers() throws Exception {
        verifyFunction(conf, "$.numbers.index(0)", NUMBER_SERIES, 1);
        verifyFunction(conf, "$.numbers.index(-1)", NUMBER_SERIES, 10);
        verifyFunction(conf, "$.numbers.index(1)", NUMBER_SERIES, 2);
    }

    @Test
    public void testFirstOfText() throws Exception {
        verifyFunction(conf, "$.text.first()", TEXT_SERIES, "a");
    }

    @Test
    public void testLastOfText() throws Exception {
        verifyFunction(conf, "$.text.last()", TEXT_SERIES, "f");
    }

    @Test
    public void testIndexOfText() throws Exception {
        verifyFunction(conf, "$.text.index(0)", TEXT_SERIES, "a");
        verifyFunction(conf, "$.text.index(-1)", TEXT_SERIES, "f");
        verifyFunction(conf, "$.text.index(1)", TEXT_SERIES, "b");
    }

    @Test
    public void testDistinctOfText() {
        verifyFunction(conf, "$.text_with_duplicates.distinct()", TEXT_SERIES, Arrays.asList("a", "b"));
    }

    @Test
    public void testDistinctOfObjects() {
        final Object expectedValue = Configuration.defaultConfiguration().jsonProvider()
                .parse("[{\"a\":\"a-val\"}, {\"b\":\"b-val\"}]");
        verifyFunction(conf, "$.objects.distinct()", OBJECT_SERIES, expectedValue);
    }

    @Test
    public void testDistinctArrayOfObjects() {
        final Object expectedValue = Configuration.defaultConfiguration().jsonProvider()
                .parse("[{\"a\":[{\"a\":\"a-val\"}, {\"b\":\"b-val\"}]}, {\"b\":[{\"b\":\"b-val\"}]}]");
        verifyFunction(conf, "$.array_of_objects.distinct()", OBJECT_SERIES, expectedValue);
    }

    @Test
    public void testDistinctArrayOfArrays() {
        final Object expectedValue = Configuration.defaultConfiguration().jsonProvider()
                .parse("[[{\"a\":\"a-val\"}, {\"b\":\"b-val\"}], [{\"b\":\"b-val\"}]]");
        verifyFunction(conf, "$.array_of_arrays.distinct()", OBJECT_SERIES, expectedValue);
    }

    @Test
    public void testDistinctOfEmptyObjects() throws Exception {
        final Object expectedValue = Configuration.defaultConfiguration().jsonProvider().parse("[]");
        verifyFunction(conf, "$.empty.distinct()", OBJECT_SERIES, expectedValue);
    }
}
