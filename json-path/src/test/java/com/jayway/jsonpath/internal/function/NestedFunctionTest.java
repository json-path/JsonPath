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
    public void testAverageOfDoubles() {
        verifyMathFunction(conf, "$.avg({$.numbers.min()}, {$.numbers.max()})", 5.5);
    }
}
