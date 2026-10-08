package com.jayway.jsonpath.internal.function;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.Configurations;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.spi.json.JsonSmartJsonProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CompiledFunctionReuseTest {

    private static final String FIRST = "{\"dataset\":[{\"value\":1},{\"value\":3},{\"value\":2}]}";
    private static final String SECOND = "{\"dataset\":[{\"value\":5},{\"value\":11},{\"value\":2}]}";

    static Iterable<Configuration> configurations() {
        return Configurations.configurations();
    }

    static List<Arguments> numericAggregations() {
        List<Arguments> arguments = new ArrayList<>();
        for (Configuration configuration : configurations()) {
            arguments.add(Arguments.of(configuration, "min", 1.0, 2.0));
            arguments.add(Arguments.of(configuration, "max", 3.0, 11.0));
            arguments.add(Arguments.of(configuration, "sum", 6.0, 18.0));
            arguments.add(Arguments.of(configuration, "avg", 2.0, 6.0));
            arguments.add(Arguments.of(configuration, "stddev", Math.sqrt(2.0 / 3.0), Math.sqrt(14.0)));
        }
        return arguments;
    }

    @ParameterizedTest
    @MethodSource("numericAggregations")
    void compiledAggregationRetainsItsWildcard(Configuration configuration, String function,
                                                 double firstExpected, double secondExpected) {
        JsonPath compiled = JsonPath.compile(function + "($.dataset[*].value)");

        assertEquals(firstExpected, ((Number) compiled.read(FIRST, configuration)).doubleValue(), 0.000001);
        assertEquals(secondExpected, ((Number) compiled.read(SECOND, configuration)).doubleValue(), 0.000001);
        assertEquals(firstExpected, ((Number) compiled.read(FIRST, configuration)).doubleValue(), 0.000001);
    }

    @ParameterizedTest
    @MethodSource("configurations")
    void cachedAggregationCanReadAnotherDocument(Configuration configuration) {
        String first = "{\"cachedDataset\":[{\"value\":1},{\"value\":3}]}";
        String second = "{\"cachedDataset\":[{\"value\":5},{\"value\":11}]}";
        String expression = "max($.cachedDataset[*].value)";

        assertEquals(3.0, ((Number) JsonPath.using(configuration).parse(first).read(expression)).doubleValue());
        assertEquals(11.0, ((Number) JsonPath.using(configuration).parse(second).read(expression)).doubleValue());
    }

    @ParameterizedTest
    @MethodSource("configurations")
    void lengthAndSizeRetainAnExistingWildcard(Configuration configuration) {
        String first = "{\"groups\":[{\"items\":[1,2]},{\"items\":[3]}]}";
        String second = "{\"groups\":[{\"items\":[9]},{\"items\":[8,7,6]}]}";
        for (String function : new String[]{"length", "size"}) {
            JsonPath compiled = JsonPath.compile(function + "($.groups[*].items)");
            assertEquals(3, ((Number) compiled.read(first, configuration)).intValue());
            assertEquals(4, ((Number) compiled.read(second, configuration)).intValue());
            assertEquals(3, ((Number) compiled.read(first, configuration)).intValue());
        }
    }

    @ParameterizedTest
    @MethodSource("configurations")
    void repeatedScanLengthDoesNotAccumulateWildcards(Configuration configuration) {
        String first = "{\"store\":{\"book\":[{\"value\":1},{\"value\":2}]}}";
        String second = "{\"store\":{\"book\":[{\"value\":3},{\"value\":4},{\"value\":5}]}}";
        for (String function : new String[]{"length", "size"}) {
            JsonPath compiled = JsonPath.compile("$..book." + function + "()");
            assertEquals(2, ((Number) compiled.read(first, configuration)).intValue());
            assertEquals(3, ((Number) compiled.read(second, configuration)).intValue());
            assertEquals(2, ((Number) compiled.read(first, configuration)).intValue());
        }
    }

    @Test
    void lengthCanBeRetriedAfterProviderFailure() {
        JsonSmartJsonProvider provider = new JsonSmartJsonProvider() {
            private boolean failOnce = true;

            @Override
            public Object getArrayIndex(Object value, int index) {
                if (failOnce && length(value) == 3) {
                    failOnce = false;
                    throw new IllegalStateException("provider failure");
                }
                return super.getArrayIndex(value, index);
            }
        };
        Configuration configuration = Configuration.builder().jsonProvider(provider).build();
        JsonPath compiled = JsonPath.compile("length($.groups[*].items)");
        String json = "{\"groups\":[{\"items\":[1,2,3]}]}";

        IllegalStateException failure = assertThrows(IllegalStateException.class,
                () -> compiled.read(json, configuration));
        assertEquals("provider failure", failure.getMessage());
        assertEquals(3, (int) compiled.read(json, configuration));
        assertEquals(3, (int) compiled.read(json, configuration));
    }
}
