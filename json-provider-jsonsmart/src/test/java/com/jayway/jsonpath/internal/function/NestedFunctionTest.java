package com.jayway.jsonpath.internal.function;

import static com.jayway.jsonpath.JsonPath.using;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.jayway.jsonpath.Configuration;

/**
 * Created by matt@mjgreenwood.net on 12/10/15.
 */
public class NestedFunctionTest extends BaseFunctionTest {

    private Configuration conf = Configuration.defaultConfiguration();

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
    public void testSimpleLiteralArgument() {
        verifyMathFunction(conf, "$.sum(5)", 5.0);
        verifyMathFunction(conf, "$.sum(50)", 50.0);
    }

    @Test
    public void testStringConcat() {
        verifyTextFunction(conf, "$.text.concat()", "abcdef");
    }

    @Test
    public void testStringConcatWithJSONParameter() {
        verifyTextFunction(conf, "$.text.concat(\"-\", \"ghijk\")", "abcdef-ghijk");
    }

    @Test
    public void testAppendNumber() {
        verifyMathFunction(conf, "$.numbers.append(11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 0).avg()", 10.0);
    }

    /**
     * Aggregation function should ignore text values
     */
    @Test
    public void testAppendTextAndNumberThenSum() {
        verifyMathFunction(conf, "$.numbers.append(\"0\", \"11\").sum()", 55.0);
    }

    @Test
    public void testErrantCloseBraceNegative() {
        try {
            using(conf).parse(this.NUMBER_SERIES).read("$.numbers.append(0, 1, 2}).avg()");
            assert(false);
        }
        catch (Exception e) {
            assertTrue(e.getMessage().startsWith("Unexpected close brace"));
        }
    }

    @Test
    public void testErrantCloseBracketNegative() {
        try {
            using(conf).parse(this.NUMBER_SERIES).read("$.numbers.append(0, 1, 2]).avg()");
            assert(false);
        }
        catch (Exception e) {
            assertTrue(e.getMessage().startsWith("Unexpected close bracket"));
        }
    }

    @Test
    public void testUnclosedFunctionCallNegative() {
        try {
            using(conf).parse(this.NUMBER_SERIES).read("$.numbers.append(0, 1, 2");
            assert(false);
        }
        catch (Exception e) {
            assertTrue(e.getMessage().startsWith("Arguments to function: 'append'"));
        }
    }

}
