package com.jayway.jsonpath.matchers;

import com.jayway.jsonassert.JsonAssert;
import com.jayway.jsonpath.Configuration;

public class Issue_744 {
    public static final Configuration jsonConf = Configuration.defaultConfiguration();

    @org.junit.Test
    public void test_01(){
        String json = "{\n" +
                "    \"array\": [\n" +
                "        { \"name\": \"object1\" },\n" +
                "        { \"name\": \"object2\" }\n" +
                "    ]\n" +
                "}";
        JsonAssert.with(json).assertThat("array.*.fake", JsonAssert.emptyCollection());
        JsonAssert.with(json).assertNotDefined("array.*.fake");
    }

}
