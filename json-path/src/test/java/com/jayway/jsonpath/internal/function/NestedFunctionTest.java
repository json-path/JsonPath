package com.jayway.jsonpath.internal.function;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.Configurations;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.InvalidJsonException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.List;

import static com.jayway.jsonpath.JsonPath.using;
import static org.junit.Assert.assertTrue;

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
    public void testSimpleLiteralArgument() {
        verifyMathFunction(conf, "$.sum(5)", 5.0);
        verifyMathFunction(conf, "$.sum(50)", 50.0);
    }

    @Test
    public void testStringConcat() {
        verifyTextFunction(conf, "$.text.concat()", "abcdef");
    }

    @Test
    public void testStringAndNumberConcat() {
        verifyTextAndNumberFunction(conf, "$.concat($.text[0], $.numbers[0])", "a1");
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

    @Test
    public void testParseString() {
        verifyTextFunction(conf, "$.parseable.parse().foo", "bar");
        verifyTextFunction(conf, "$.parseable.parse().array.sum()", 7.0);
    }

    @Test
    public void testIndefiniteParseString() {
        String json = "{ \"array\": [ { \"document\": \"{\\\"name\\\": \\\"foo\\\"}\" }, { \"document\": \"{\\\"name\\\": \\\"bar\\\"}\" } ] }";
        Object result = using(conf).parse(json).read("$.array[*].document.parse().name");
        Assert.assertTrue(conf.jsonProvider().isArray(result));
        Iterator<?> resultIter = conf.jsonProvider().toIterable(result).iterator();
        Assert.assertTrue(resultIter.hasNext());
        Assert.assertEquals("foo", conf.jsonProvider().unwrap(resultIter.next()));
        Assert.assertTrue(resultIter.hasNext());
        Assert.assertEquals("bar", conf.jsonProvider().unwrap(resultIter.next()));
        Assert.assertFalse(resultIter.hasNext());
    }

    @Test
    public void testParseOnNonStrings() {
        String json = "{ \"boolean\": true, \"number\": 12.34, \"object\": { \"foo\" : \"bar\" }, \"array\": [ 1, 2 ] }";
        DocumentContext doc = using(conf).parse(json);
        Assert.assertTrue((boolean) conf.jsonProvider().unwrap(doc.read("$.boolean.parse()")));
        Assert.assertEquals(12.34, (double) conf.jsonProvider().unwrap(doc.read("$.number.parse()")), 0.0);
        Assert.assertEquals("bar", conf.jsonProvider().unwrap(doc.read("$.object.parse().foo")));
        Assert.assertEquals(1, (int) conf.jsonProvider().unwrap(doc.read("$.array.parse()[0]")));
        Assert.assertEquals(2, (int) conf.jsonProvider().unwrap(doc.read("$.array.parse()[1]")));
    }

    @Test
    public void testParseOnMalformedJsonString() {
        String json = "{\"malformed\": \"{]\"}";
        try {
            using(conf).parse(json).read("$.malformed.parse()");
            Assert.fail("Should have thrown an InvalidJsonException");
        } catch (InvalidJsonException e) {
            Assert.assertEquals("String property at path $['malformed'] did not parse as valid JSON", e.getMessage());
        }
    }

    @Test
    public void testParseOnWellFormedJsonStringsThatAreNotObjects() {
        String json = "{\"string\": \"\\\"foo\\\"\", \"number\": \"123\", \"boolean\": \"true\", \"array\": \"[1, 2]\"}";
        DocumentContext doc = using(conf).parse(json);
        Assert.assertEquals("foo", conf.jsonProvider().unwrap(doc.read("$.string.parse()")));
        Assert.assertEquals(123, conf.jsonProvider().unwrap(doc.read("$.number.parse()")));
        Assert.assertEquals(true, conf.jsonProvider().unwrap(doc.read("$.boolean.parse()")));
        Iterator<?> arrayIter = conf.jsonProvider().toIterable(doc.read("$.array.parse()")).iterator();
        Assert.assertTrue(arrayIter.hasNext());
        Assert.assertEquals(1, arrayIter.next());
        Assert.assertTrue(arrayIter.hasNext());
        Assert.assertEquals(2, arrayIter.next());
        Assert.assertFalse(arrayIter.hasNext());
    }
}
