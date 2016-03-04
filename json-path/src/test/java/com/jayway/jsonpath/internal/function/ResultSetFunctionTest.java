package com.jayway.jsonpath.internal.function;

import static org.junit.runners.Parameterized.Parameters;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.Configurations;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Defines functional tests around executing functions on result sets.
 */
@RunWith(Parameterized.class)
public class ResultSetFunctionTest extends BaseFunctionTest {

    private static final Logger logger = LoggerFactory.getLogger(ResultSetFunctionTest.class);

    private Configuration conf = Configurations.GSON_CONFIGURATION;

    public ResultSetFunctionTest(Configuration conf) {
        logger.debug("Testing with configuration {}", conf.getClass().getName());
        this.conf = conf;
    }

    @Parameters
    public static Iterable<Configuration> configurations() {
        return Configurations.configurations();
    }

    @Test
    public void testMaxOfDoublesResultSet() {
        verifyExampleFunction(conf, "$.store.book[*].price.max()", 22.99);
        verifyExampleFunction(conf, "$.store..price.max()", 22.99);
    }

    @Test
    public void testMinOfDoublesResultSet() {
        verifyExampleFunction(conf, "$.store.book[*].price.min()", 8.95);
        verifyExampleFunction(conf, "$.store..price.min()", 8.95);
    }

    @Test
    public void testSumOfDoublesResultSet() {
        verifyExampleFunction(conf, "$.store.book[*].price.sum()", 53.92);
        verifyExampleFunction(conf, "$.store..price.sum()", 73.87);
    }

    @Test
    public void testAvgOfDoublesResultSet() {
        verifyExampleFunction(conf, "$.store.book[*].price.avg()", 13.48);
        verifyExampleFunction(conf, "$.store..price.avg()", 14.774000000000001);
    }

    @Test
    public void testLengthOfDoublesResultSet() {
        verifyExampleFunction(conf, "$.store.book[*].price.length()", 4);
        verifyExampleFunction(conf, "$.store..price.length()", 5);
    }

    @Test
    public void testLengthOfBooksResultSet() {
        verifyExampleFunction(conf, "$.store.book.length()", 4);
    }
}
