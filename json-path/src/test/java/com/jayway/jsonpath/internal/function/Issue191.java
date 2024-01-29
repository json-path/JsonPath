package com.jayway.jsonpath.internal.function;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.Configurations;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.InputStream;


/**
 * TDD for Issue 191
 * <p>
 * Shows aggregation across fields rather than within a single entity.
 */
public class Issue191 {

    private Configuration conf = Configurations.GSON_CONFIGURATION;

    @Test
    public void testResultSetNumericComputation() {
        InputStream stream = ClassLoader.getSystemResourceAsStream("issue_191.json");
        Long value = JsonPath.parse(stream).read("$.sum($..timestamp)", Long.class);
        Assertions.assertEquals(Long.valueOf(35679716813L), value, "Expected the max function to consume the aggregation parameters and calculate the max over the result set");
    }

    @Test
    public void testResultSetNumericComputationTail() {
        InputStream stream = ClassLoader.getSystemResourceAsStream("issue_191.json");
        Long value = JsonPath.parse(stream).read("$..timestamp.sum()", Long.class);
        Assertions.assertEquals(Long.valueOf(35679716813L), value, "Expected the max function to consume the aggregation parameters and calculate the max over the result set");
    }

    @Test
    public void testResultSetNumericComputationRecursiveReplacement() {
        InputStream stream = ClassLoader.getSystemResourceAsStream("issue_191.json");
        Long value = JsonPath.parse(stream).read("$.max($..timestamp.avg(), $..timestamp.stddev())", Long.class);
        Assertions.assertEquals(Long.valueOf(1427188672L), value, "Expected the max function to consume the aggregation parameters and calculate the max over the result set");
    }

    @Test
    public void testMultipleResultSetSums() {
        InputStream stream = ClassLoader.getSystemResourceAsStream("issue_191.json");
        Long value = JsonPath.parse(stream).read("$.sum($..timestamp, $..cpus)", Long.class);
        Assertions.assertEquals(Long.valueOf(35679716835L), value, "Expected the max function to consume the aggregation parameters and calculate the max over the result set");
    }

    @Test
    public void testConcatResultSet() {
        InputStream stream = ClassLoader.getSystemResourceAsStream("issue_191.json");
        String concatResult = JsonPath.parse(stream).read("$.concat($..state)", String.class);
        Assertions.assertEquals(concatResult.length(), 806, "Expected a string length to be a concat of all of the states");
    }

    @Test
    public void testConcatWithNumericValueAsString() {
        InputStream stream = ClassLoader.getSystemResourceAsStream("issue_191.json");
        String concatResult = JsonPath.parse(stream).read("$.concat($..cpus)", String.class);
        Assertions.assertEquals(concatResult.length(), 489, "Expected a string length to be a concat of all of the cpus");
    }
}
