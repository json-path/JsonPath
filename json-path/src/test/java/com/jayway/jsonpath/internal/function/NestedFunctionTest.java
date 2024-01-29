package com.jayway.jsonpath.internal.function;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.Configurations;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.jayway.jsonpath.JsonPath.using;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Created by matt@mjgreenwood.net on 12/10/15.
 */
public class NestedFunctionTest extends BaseFunctionTest {
    private static final Logger logger = LoggerFactory.getLogger(NumericPathFunctionTest.class);


    public static Iterable<Configuration> configurations() {
        return Configurations.configurations();
    }


    @ParameterizedTest
    @MethodSource("configurations")
    public void testParameterAverageFunctionCall(Configuration conf) {
        verifyMathFunction(conf, "$.avg($.numbers.min(), $.numbers.max())", 5.5);
    }


    @ParameterizedTest
    @MethodSource("configurations")
    public void testArrayAverageFunctionCall(Configuration conf) {
        verifyMathFunction(conf, "$.numbers.avg()", 5.5);
    }

    /**
     * This test calculates the following:
     * <p>
     * For each number in $.numbers 1 -> 10 add each number up,
     * then add 1 (min), 10 (max)
     * <p>
     * Alternatively 1+2+3+4+5+6+7+8+9+10+1+10 == 66
     */

    @ParameterizedTest
    @MethodSource("configurations")
    public void testArrayAverageFunctionCallWithParameters(Configuration conf) {
        verifyMathFunction(conf, "$.numbers.sum($.numbers.min(), $.numbers.max())", 66.0);
    }


    @ParameterizedTest
    @MethodSource("configurations")
    public void testJsonInnerArgumentArray(Configuration conf) {
        verifyMathFunction(conf, "$.sum(5, 3, $.numbers.max(), 2)", 20.0);
    }


    @ParameterizedTest
    @MethodSource("configurations")
    public void testSimpleLiteralArgument(Configuration conf) {
        verifyMathFunction(conf, "$.sum(5)", 5.0);
        verifyMathFunction(conf, "$.sum(50)", 50.0);
    }


    @ParameterizedTest
    @MethodSource("configurations")
    public void testStringConcat(Configuration conf) {
        verifyTextFunction(conf, "$.text.concat()", "abcdef");
    }


    @ParameterizedTest
    @MethodSource("configurations")
    public void testStringAndNumberConcat(Configuration conf) {
        verifyTextAndNumberFunction(conf, "$.concat($.text[0], $.numbers[0])", "a1");
    }


    @ParameterizedTest
    @MethodSource("configurations")
    public void testStringConcatWithJSONParameter(Configuration conf) {
        verifyTextFunction(conf, "$.text.concat(\"-\", \"ghijk\")", "abcdef-ghijk");
    }


    @ParameterizedTest
    @MethodSource("configurations")
    public void testAppendNumber(Configuration conf) {
        verifyMathFunction(conf, "$.numbers.append(11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 0).avg()", 10.0);
    }

    /**
     * Aggregation function should ignore text values
     */

    @ParameterizedTest
    @MethodSource("configurations")
    public void testAppendTextAndNumberThenSum(Configuration conf) {
        verifyMathFunction(conf, "$.numbers.append(\"0\", \"11\").sum()", 55.0);
    }


    @ParameterizedTest
    @MethodSource("configurations")
    public void testErrantCloseBraceNegative(Configuration conf) {
        try {
            using(conf).parse(this.NUMBER_SERIES).read("$.numbers.append(0, 1, 2}).avg()");
            assert (false);
        } catch (Exception e) {
            assertTrue(e.getMessage().startsWith("Unexpected close brace"));
        }
    }


    @ParameterizedTest
    @MethodSource("configurations")
    public void testErrantCloseBracketNegative(Configuration conf) {
        try {
            using(conf).parse(this.NUMBER_SERIES).read("$.numbers.append(0, 1, 2]).avg()");
            assert (false);
        } catch (Exception e) {
            assertTrue(e.getMessage().startsWith("Unexpected close bracket"));
        }
    }


    @ParameterizedTest
    @MethodSource("configurations")
    public void testUnclosedFunctionCallNegative(Configuration conf) {
        try {
            using(conf).parse(this.NUMBER_SERIES).read("$.numbers.append(0, 1, 2");
            assert (false);
        } catch (Exception e) {
            assertTrue(e.getMessage().startsWith("Arguments to function: 'append'"));
        }
    }

}
