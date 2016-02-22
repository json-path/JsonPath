package com.jayway.jsonpath.internal.function;

import static org.junit.Assert.assertEquals;
import static org.junit.runners.Parameterized.Parameters;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.Configurations;
import com.jayway.jsonpath.JsonPathException;
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
    }

    @Test
    public void testSumOfDoublesResultSet() {
        verifyExampleFunction(conf, "$.store.book[*].price.sum()", 53.92);
    }

}
