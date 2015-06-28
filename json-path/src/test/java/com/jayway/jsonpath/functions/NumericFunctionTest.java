package com.jayway.jsonpath.functions;

import com.jayway.jsonpath.Configuration;
import net.minidev.json.JSONArray;
import org.junit.Test;

import java.util.Arrays;

import static com.jayway.jsonpath.JsonPath.using;
import static org.assertj.core.api.Assertions.assertThat;

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
public class NumericFunctionTest extends BaseFunctionTest {

    @Test
    public void testAverageOfDoubles() {
        verifyMathFunction("$.numbers.%average()", (10d * (10d + 1d)) / 2d);
    }

    @Test
    public void testSumOfDouble() {
        verifyMathFunction("$.numbers.%sum()", (10d * (10d + 1d)) / 2d);
    }

    @Test
    public void testMaxOfDouble() {
        verifyMathFunction("$.numbers.%max()", 10d);
    }

    @Test
    public void testMinOfDouble() {
        verifyMathFunction("$.numbers.%min()", 1d);
    }

    @Test
    public void testStdDevOfDouble() {
        verifyMathFunction("$.numbers.%stddev()", 1d);
    }

    /**
     * Expect that for an invalid function name we'll get back the original input to the function
     */
    @Test
    public void testInvalidFunctionNameNegative() {
        Configuration conf = Configuration.defaultConfiguration();
        JSONArray numberSeries = new JSONArray();
        numberSeries.addAll(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10));
        assertThat(using(conf).parse(NUMBER_SERIES).read("$.numbers.%foo()")).isEqualTo(numberSeries);
    }

}
