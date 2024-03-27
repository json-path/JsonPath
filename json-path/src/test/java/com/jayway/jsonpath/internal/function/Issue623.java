package com.jayway.jsonpath.internal.function;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.Configurations;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RunWith(Parameterized.class)
public class Issue623 extends BaseFunctionTest{
    private static final Logger logger = LoggerFactory.getLogger(NumericPathFunctionTest.class);
    private Configuration conf;

    @Parameterized.Parameters
    public static Iterable<Configuration> configurations() {
        return Configurations.configurations();
    }

    public Issue623(Configuration conf) {
        logger.debug("Testing with configuration {}", conf.getClass().getName());
        this.conf = conf;
    }

    @Test
    public void joinFunctionSimpleTest() {
        verifyTextFunction(conf, "$.text.join()", "a b c d e f");
        verifyTextFunction(conf, "$.text.join(\"\\, \")", "a, b, c, d, e, f");
        verifyTextFunction(conf, "$.text.join(\"1\", \"2\", \"3\", \"-\")", "a-b-c-d-e-f-1-2-3");
    }

    @Test
    public void testJoinNestedTest() {
        String json = "{\"document\" : { " +
                "\"text\" : [ " +
                "{ \"value\": \"a\", \"int\": \"1\" }, " +
                "{ \"value\": \"b\", \"int\": \"2\" }, " +
                "{ \"value\": \"c\", \"int\": \"3\" }, " +
                "{ \"value\": \"d\", \"int\": \"4\" }, " +
                "{ \"value\": \"e\", \"int\": \"5\" }, " +
                "{ \"value\": \"f\", \"int\": \"6\" } ]}}";

        verifyFunction(conf, "join(" +
                "$.join($.document.text[*]['value'], \"\t\"), " +
                "$.join($.document.text[*]['int'], \"\t\"), " +
                "\"\n\")", json, "a\tb\tc\td\te\tf\n1\t2\t3\t4\t5\t6");
    }
}
