package com.jayway.jsonpath.internal.function;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.Configurations;
import com.jayway.jsonpath.JsonPath;
import org.junit.Test;

import java.io.InputStream;

import static org.junit.Assert.assertEquals;

/**
 * TDD for Issue 191
 *
 * Shows aggregation across fields rather than within a single entity.
 *
 */
public class Issue191 extends com.jayway.jsonpath.internal.function.BaseFunctionTest {

    private Configuration conf = Configurations.GSON_CONFIGURATION;

    @Test
    public void testResultSetNumericComputation() {
        InputStream stream = ClassLoader.getSystemResourceAsStream("issue_191.json");
        Long value = JsonPath.parse(stream).read("$.max($..timestamp)", Long.class);
        assertEquals("Expected the max function to consume the aggregation parameters and calculate the max over the result set",
                Long.valueOf(1427310341), value);
    }

    @Test
    public void testConcatResultSet() {
        InputStream stream = ClassLoader.getSystemResourceAsStream("issue_191.json");
        String concatResult = JsonPath.parse(stream).read("$.concat($..state)", String.class);
        assertEquals("Expected a string length to be a concat of all of the states", concatResult.length(), 806);
    }

    @Test
    public void testConcatWithNumericValueAsString() {
        InputStream stream = ClassLoader.getSystemResourceAsStream("issue_191.json");
        String concatResult = JsonPath.parse(stream).read("$.concat($..cpus)", String.class);
        assertEquals("Expected a string length to be a concat of all of the cpus", concatResult.length(), 489);
    }
}
