package com.jayway.jsonpath.internal.function;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPathException;

/**
 * Defines functional tests around executing:
 *
 * - sum
 * - avg
 * - stddev
 *
 * for each of the above, executes the test and verifies that the results are as expected based on a static input
 * and static output.
 *
 * Created by mattg on 6/26/15.
 */
public class NumericPathFunctionTest extends BaseFunctionTest {

    private final Configuration conf = Configuration.defaultConfiguration();
    
    @Test
    public void testAverageOfDoubles() {
        verifyMathFunction(conf, "$.numbers.avg()", 5.5);
    }

    @Test
    public void testAverageOfEmptyListNegative() {
        try {
            verifyMathFunction(conf, "$.empty.avg()", null);
        } catch (JsonPathException e) {
            assertEquals(e.getMessage(), "Aggregation function attempted to calculate value using empty array");
        }
    }

    @Test
    public void testSumOfDouble() {
        verifyMathFunction(conf, "$.numbers.sum()", (10d * (10d + 1d)) / 2d);
    }

    @Test
    public void testSumOfEmptyListNegative() {
        try {
            verifyMathFunction(conf, "$.empty.sum()", null);
        } catch (JsonPathException e) {
            assertEquals(e.getMessage(), "Aggregation function attempted to calculate value using empty array");
        }
    }

    @Test
    public void testMaxOfDouble() {
        verifyMathFunction(conf, "$.numbers.max()", 10d);
    }

    @Test
    public void testMaxOfEmptyListNegative() {
        try {
            verifyMathFunction(conf, "$.empty.max()", null);
        } catch (JsonPathException e) {
            assertEquals(e.getMessage(), "Aggregation function attempted to calculate value using empty array");
        }
    }

    @Test
    public void testMinOfDouble() {
        verifyMathFunction(conf, "$.numbers.min()", 1d);
    }

    @Test
    public void testMinOfEmptyListNegative() {
        try {
            verifyMathFunction(conf, "$.empty.min()", null);
        } catch (JsonPathException e) {
            assertEquals(e.getMessage(), "Aggregation function attempted to calculate value using empty array");
        }
    }


    @Test
    public void testStdDevOfDouble() {
        verifyMathFunction(conf, "$.numbers.stddev()", 2.8722813232690143d);
    }

    @Test
    public void testStddevOfEmptyListNegative() {
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
