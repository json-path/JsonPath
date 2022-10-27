package com.jayway.jsonpath.internal.function;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.Configurations;
import org.junit.Test;

/**
 * Test cases for functions
 * 
 * -first
 * -last
 * -index(X)
 *
 * Created by git9527 on 6/11/22.
 */
public class SequentialPathFunctionTest extends BaseFunctionTest {

    private Configuration conf = Configurations.JACKSON_CONFIGURATION;

    @Test
    public void testFirstOfNumbers() throws Exception {
        verifyFunction(conf, "$.numbers.first()", NUMBER_SERIES, 1);
    }
    
    @Test
    public void testLastOfNumbers() throws Exception {
        verifyFunction(conf, "$.numbers.last()", NUMBER_SERIES, 10);
    }
    
    @Test
    public void testIndexOfNumbers() throws Exception {
        verifyFunction(conf, "$.numbers.index(0)", NUMBER_SERIES, 1);
        verifyFunction(conf, "$.numbers.index(-1)", NUMBER_SERIES, 10);
        verifyFunction(conf, "$.numbers.index(1)", NUMBER_SERIES, 2);
    }

    @Test
    public void testFirstOfText() throws Exception {
        verifyFunction(conf, "$.text.first()", TEXT_SERIES, "a");
    }

    @Test
    public void testLastOfText() throws Exception {
        verifyFunction(conf, "$.text.last()", TEXT_SERIES, "f");
    }

    @Test
    public void testIndexOfText() throws Exception {
        verifyFunction(conf, "$.text.index(0)", TEXT_SERIES, "a");
        verifyFunction(conf, "$.text.index(-1)", TEXT_SERIES, "f");
        verifyFunction(conf, "$.text.index(1)", TEXT_SERIES, "b");
    }
}
