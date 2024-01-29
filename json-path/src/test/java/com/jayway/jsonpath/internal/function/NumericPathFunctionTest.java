package com.jayway.jsonpath.internal.function;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.Configurations;
import com.jayway.jsonpath.JsonPathException;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import static org.junit.jupiter.api.Assertions.assertEquals;


/**
 * Defines functional tests around executing:
 * <p>
 * - sum
 * - avg
 * - stddev
 * <p>
 * for each of the above, executes the test and verifies that the results are as expected based on a static input
 * and static output.
 * <p>
 * Created by mattg on 6/26/15.
 */
public class NumericPathFunctionTest extends BaseFunctionTest {

    public static Iterable<Configuration> configurations() {
        return Configurations.configurations();
    }


    @ParameterizedTest
    @MethodSource("configurations")
    public void testAverageOfDoubles(Configuration conf) {
        verifyMathFunction(conf, "$.numbers.avg()", 5.5);
    }

    @ParameterizedTest
    @MethodSource("configurations")
    public void testAverageOfEmptyListNegative(Configuration conf) {
        try {
            verifyMathFunction(conf, "$.empty.avg()", null);
        } catch (JsonPathException e) {
            assertEquals(e.getMessage(), "Aggregation function attempted to calculate value using empty array");
        }
    }

    @ParameterizedTest
    @MethodSource("configurations")
    public void testSumOfDouble(Configuration conf) {
        verifyMathFunction(conf, "$.numbers.sum()", (10d * (10d + 1d)) / 2d);
    }

    @ParameterizedTest
    @MethodSource("configurations")
    public void testSumOfEmptyListNegative(Configuration conf) {
        try {
            verifyMathFunction(conf, "$.empty.sum()", null);
        } catch (JsonPathException e) {
            assertEquals(e.getMessage(), "Aggregation function attempted to calculate value using empty array");
        }
    }

    @ParameterizedTest
    @MethodSource("configurations")
    public void testMaxOfDouble(Configuration conf) {
        verifyMathFunction(conf, "$.numbers.max()", 10d);
    }

    @ParameterizedTest
    @MethodSource("configurations")
    public void testMaxOfEmptyListNegative(Configuration conf) {
        try {
            verifyMathFunction(conf, "$.empty.max()", null);
        } catch (JsonPathException e) {
            assertEquals(e.getMessage(), "Aggregation function attempted to calculate value using empty array");
        }
    }

    @ParameterizedTest
    @MethodSource("configurations")
    public void testMinOfDouble(Configuration conf) {
        verifyMathFunction(conf, "$.numbers.min()", 1d);
    }

    @ParameterizedTest
    @MethodSource("configurations")
    public void testMinOfEmptyListNegative(Configuration conf) {
        try {
            verifyMathFunction(conf, "$.empty.min()", null);
        } catch (JsonPathException e) {
            assertEquals(e.getMessage(), "Aggregation function attempted to calculate value using empty array");
        }
    }


    @ParameterizedTest
    @MethodSource("configurations")
    public void testStdDevOfDouble(Configuration conf) {
        verifyMathFunction(conf, "$.numbers.stddev()", 2.8722813232690143d);
    }

    @ParameterizedTest
    @MethodSource("configurations")
    public void testStddevOfEmptyListNegative(Configuration conf) {
        try {
            verifyMathFunction(conf, "$.empty.stddev()", null);
        } catch (JsonPathException e) {
            assertEquals(e.getMessage(), "Aggregation function attempted to calculate value using empty array");
        }
    }

    /**
     * Expect that for an invalid function name we'll get back the original input to the function
     */
//    @Test
//    @Ignore
//    public void testInvalidFunctionNameNegative() {
//        JSONArray numberSeries = new JSONArray();
//        numberSeries.addAll(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10));
//        assertThat(using(conf).parse(NUMBER_SERIES).read("$.numbers.foo()")).isEqualTo(numberSeries);
//    }

}
