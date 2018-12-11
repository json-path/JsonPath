package com.jayway.jsonpath.internal.function;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.Configurations;
import com.jayway.jsonpath.JsonPathException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertEquals;
import static org.junit.runners.Parameterized.Parameters;

/**
 * Defines functional tests around executing:
 *
 * - sum
 * - mult
 * - div
 * - avg
 * - stddev
 *
 * for each of the above, executes the test and verifies that the results are as expected based on a static input
 * and static output.
 *
 * Created by mattg on 6/26/15.
 */
@RunWith(Parameterized.class)
public class NumericPathFunctionTest extends BaseFunctionTest {

    private static final Logger logger = LoggerFactory.getLogger(NumericPathFunctionTest.class);

    private Configuration conf = Configurations.GSON_CONFIGURATION;

    public NumericPathFunctionTest(Configuration conf) {
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
    public void testMultOfDouble() {
        verifyMathFunction(conf, "$.numbers.mult()", 1d * 2d * 3d * 4d * 5d * 6d * 7d * 8d * 9d * 10d);
    }

    @Test
    public void testDivOfRootDoublePair() {
        verifyMathFunction(conf, "$.div(1,10)", 1d / 10d);
    }

    @Test
    public void testDivOfDoublePairCalledNotFromRoot() {
        verifyMathFunction(conf, "$.numbers.div(1,10)", 1d / 10d);
    }

    @Test
    public void testDivOfDoublePairNested() {
        verifyMathFunction(conf, "$.div($.numbers[0],$.numbers[-1])", 1d / 10d);
    }

    @Test
    public void testDivOfDoublePair() {
        verifyMathFunction(conf, "$.div(" + Double.MAX_VALUE + ',' + Double.MAX_VALUE + ')', 1d);
    }

    @Test
    public void testDivByZeroPositive() {
        try {
            verifyMathFunction(conf, "$.div(1,0)", Double.POSITIVE_INFINITY);
        } catch (JsonPathException e) {
            assertEquals(e.getMessage(), "Division by zero. Infinity is not a valid double value as per JSON specification");
        }
    }

    @Test
    public void testDivByZeroNegative() {
        try {
            verifyMathFunction(conf, "$.div(-1,0)", Double.NEGATIVE_INFINITY);
        } catch (JsonPathException e) {
            assertEquals(e.getMessage(), "Division by zero. Infinity is not a valid double value as per JSON specification");
        }
    }

    @Test
    public void testDivOfDouble() {
        verifyMathFunction(conf, "$.numbers.mult()", 1d * 2d * 3d * 4d * 5d * 6d * 7d * 8d * 9d * 10d);
    }

    @Test
    public void testDivOfMoreThanTwoArgsNegative() {
        try {
            verifyMathFunction(conf, "$.numbers.div()", null);
        } catch (JsonPathException e) {
            assertEquals(e.getMessage(), "Division function attempted to calculate value using other than 2 arguments");
        }
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
    public void testMultOfEmptyListNegative() {
        try {
            verifyMathFunction(conf, "$.empty.mult()", null);
        } catch (JsonPathException e) {
            assertEquals(e.getMessage(), "Aggregation function attempted to calculate value using empty array");
        }
    }

    @Test
    public void testDivOfEmptyListNegative() {
        try {
            verifyMathFunction(conf, "$.empty.div()", null);
        } catch (JsonPathException e) {
            assertEquals(e.getMessage(), "Division function attempted to calculate value using other than 2 arguments");
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
