package com.jayway.jsonpath.internal.function;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.Configurations;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.spi.json.JacksonJsonProvider;
import org.junit.Test;

import java.io.InputStream;

import static org.junit.Assert.assertEquals;

/**
 * TDD for Issue 191
 *
 * Shows aggregation across fields rather than within a single entity.
 *
 */
public class Issue191 {

    private Configuration conf = Configurations.GSON_CONFIGURATION;

    @Test
    public void testResultSetNumericComputation() {
        InputStream stream = ClassLoader.getSystemResourceAsStream("issue_191.json");
        Long value = JsonPath.parse(stream).read("$.sum($..timestamp)", Long.class);
        assertEquals("Expected the max function to consume the aggregation parameters and calculate the max over the result set",
                Long.valueOf(35679716813L), value);
    }

    @Test
    public void testResultSetNumericComputationTail() {
        InputStream stream = ClassLoader.getSystemResourceAsStream("issue_191.json");
        Long value = JsonPath.parse(stream).read("$..timestamp.sum()", Long.class);
        assertEquals("Expected the max function to consume the aggregation parameters and calculate the max over the result set",
                Long.valueOf(35679716813L), value);
    }

    @Test
    public void testResultSetNumericComputationRecursiveReplacement() {
        InputStream stream = ClassLoader.getSystemResourceAsStream("issue_191.json");
        Long value = JsonPath.parse(stream).read("$.max($..timestamp.avg(), $..timestamp.stddev())", Long.class);
        assertEquals("Expected the max function to consume the aggregation parameters and calculate the max over the result set",
                Long.valueOf(1427188672L), value);
    }

    @Test
    public void testMultipleResultSetSums() {
        InputStream stream = ClassLoader.getSystemResourceAsStream("issue_191.json");
        Long value = JsonPath.parse(stream).read("$.sum($..timestamp, $..cpus)", Long.class);
        assertEquals("Expected the max function to consume the aggregation parameters and calculate the max over the result set",
                Long.valueOf(35679716835L), value);
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

    @Test
    public void testMultiple() {
        final Configuration STRICT_PROVIDER_CONFIGURATION = Configuration.builder().jsonProvider(new JacksonJsonProvider()).build();

        InputStream stream1 = ClassLoader.getSystemResourceAsStream("issue_191.json");
        DocumentContext documentContext1 = JsonPath.using(STRICT_PROVIDER_CONFIGURATION).parse(stream1);

        InputStream stream2 = ClassLoader.getSystemResourceAsStream("issue_191-2.json");
        DocumentContext documentContext2 = JsonPath.using(STRICT_PROVIDER_CONFIGURATION).parse(stream2);

        InputStream stream3 = ClassLoader.getSystemResourceAsStream("issue_191-3.json");
        DocumentContext documentContext3 = JsonPath.using(STRICT_PROVIDER_CONFIGURATION).parse(stream3);

        JsonPath jsonPathCpuMin = JsonPath.compile("$.min($..cpus)");
        JsonPath jsonPathDiskMin = JsonPath.compile("$.min($..disk)");
        JsonPath jsonPathMemMin = JsonPath.compile("$.min($..mem)");

        JsonPath jsonPathCpuMax = JsonPath.compile("$.max($..cpus)");
        JsonPath jsonPathDiskMax = JsonPath.compile("$.max($..disk)");
        JsonPath jsonPathMemMax = JsonPath.compile("$.max($..mem)");

        assertEquals(-2.44249065417534E-15, documentContext1.read(jsonPathCpuMin));
        assertEquals(1.0, documentContext2.read(jsonPathCpuMin));
        assertEquals(32.0, documentContext3.read(jsonPathCpuMin));

        assertEquals(1600.0, documentContext3.read(jsonPathMemMax));
        assertEquals(16.0, documentContext2.read(jsonPathMemMax));
        assertEquals(2744.0, documentContext1.read(jsonPathMemMax));

        assertEquals(512.0, documentContext2.read(jsonPathDiskMin));
        assertEquals(4096.0, documentContext3.read(jsonPathDiskMin));
        assertEquals(0.0, documentContext1.read(jsonPathDiskMin));

        assertEquals(3966.0, documentContext1.read(jsonPathDiskMax));
        assertEquals(2048.0, documentContext2.read(jsonPathDiskMax));
        assertEquals(204800.0, documentContext3.read(jsonPathDiskMax));

        assertEquals(0.0, documentContext1.read(jsonPathMemMin));
        assertEquals(2.0, documentContext2.read(jsonPathMemMin));
        assertEquals(32.0, documentContext3.read(jsonPathMemMin));

        assertEquals(2.0, documentContext1.read(jsonPathCpuMax));
        assertEquals(6.0, documentContext2.read(jsonPathCpuMax));
        assertEquals(100.0, documentContext3.read(jsonPathCpuMax));
    }
}
