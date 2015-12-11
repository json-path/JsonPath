package com.jayway.jsonpath.internal.function;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.Configurations;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by matt@mjgreenwood.net on 12/10/15.
 */
@RunWith(Parameterized.class)
public class NestedFunctionTest extends BaseFunctionTest {
    private static final Logger logger = LoggerFactory.getLogger(NumericPathFunctionTest.class);

    private Configuration conf = Configurations.GSON_CONFIGURATION;

    public NestedFunctionTest(Configuration conf) {
        logger.debug("Testing with configuration {}", conf.getClass().getName());
        this.conf = conf;
    }

    @Parameterized.Parameters
    public static Iterable<Configuration> configurations() {
        return Configurations.configurations();
    }

    @Test
    public void testParameterAverageFunctionCall() {
        verifyMathFunction(conf, "$.avg($.numbers.min(), $.numbers.max())", 5.5);
    }

    @Test
    public void testArrayAverageFunctionCall() {
        verifyMathFunction(conf, "$.numbers.avg()", 5.5);
    }

    /**
     * This test calculates the following:
     *
     * For each number in $.numbers 1 -> 10 add each number up,
     * then add 1 (min), 10 (max)
     *
     * Alternatively 1+2+3+4+5+6+7+8+9+10+1+10 == 66
     */
    @Test
    public void testArrayAverageFunctionCallWithParameters() {
        verifyMathFunction(conf, "$.numbers.sum($.numbers.min(), $.numbers.max())", 66.0);
    }

    @Test
    public void testJsonInnerArgumentArray() {
        verifyMathFunction(conf, "$.sum(5, 3, $.numbers.max(), 2)", 20.0);
    }

    @Test
    public void testStringConcat() {
        verifyTextFunction(conf, "$.text.concat()", "abcdef");
    }

    @Test
    public void testStringConcatWithJSONParameter() {
        verifyTextFunction(conf, "$.text.concat(\"-\", \"ghijk\")", "abcdef-ghijk");
    }
}
