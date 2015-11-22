package com.jayway.jsonpath.functions;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.Configurations;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.runners.Parameterized.Parameters;

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
@RunWith(Parameterized.class)
public class NumericFunctionTest extends BaseFunctionTest {

    private static final Logger logger = LoggerFactory.getLogger(NumericFunctionTest.class);

    private Configuration conf = Configurations.GSON_CONFIGURATION;

    public NumericFunctionTest(Configuration conf) {
        logger.debug("Testing with configuration {}", conf.getClass().getName());
        this.conf = conf;
    }

    @Parameters
    public static Iterable<Configuration> configurations() {
        return Configurations.configurations();
    }


    @Test
    public void testAverageOfDoubles() {
        verifyMathFunction(conf, "$.numbers.avg()", 5.5);
    }

    @Test
    public void testSumOfDouble() {
        verifyMathFunction(conf, "$.numbers.sum()", (10d * (10d + 1d)) / 2d);
    }

    @Test
    public void testMaxOfDouble() {
        verifyMathFunction(conf, "$.numbers.max()", 10d);
    }

    @Test
    public void testMinOfDouble() {
        verifyMathFunction(conf, "$.numbers.min()", 1d);
    }

    @Test
    public void testStdDevOfDouble() {
        verifyMathFunction(conf, "$.numbers.stddev()", 2.8722813232690143d);
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
