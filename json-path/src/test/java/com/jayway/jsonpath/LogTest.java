package com.jayway.jsonpath;

import com.jayway.jsonpath.internal.Log;
import org.junit.Test;

/**
 * User: kalle
 * Date: 8/28/13
 * Time: 10:40 AM
 */
public class LogTest {


    @Test
    public void logger_expands_templates() {

        Log.enableDebug();

        Log.debug("foo \n{}", "bar");

    }
}
