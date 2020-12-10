package com.jayway.jsonpath.internal.function;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.Configurations;
import org.apache.commons.io.IOUtils;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashSet;

/**
 * Author: Sergey Saiyan sergey.sova42@gmail.com
 * Created at 21/02/2018.
 */
public class KeySetFunctionTest extends BaseFunctionTest {

    private Configuration conf = Configurations.JACKSON_CONFIGURATION;

    @Test
    public void testKeySet() throws Exception {
        String json = IOUtils.toString(getClass().getResourceAsStream("/keyset.json"));
        verifyFunction(conf, "$.data.keys()", json, new HashSet<String>(Arrays.asList("a", "b")));
    }
}
