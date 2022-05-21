package com.jayway.jsonassert;

import org.junit.Test;

//CS304 (manually written) Issue link: https://github.com/json-path/JsonPath/issues/744
public class Issue_744 {
    @Test
    public void test_assert_not_defined(){
        String json = "{\n" +
                "    \"array\": [\n" +
                "        { \"name\": \"object1\" },\n" +
                "        { \"name\": \"object2\" }\n" +
                "    ]\n" +
                "}";
        // test not defined path, it will not throw AssertionError
        JsonAssert.with(json).assertNotDefined("array.*.fake");
    }

    @Test(expected = AssertionError.class)
    public void test_assert_defined_throw_exception() {
        String json = "{\n" +
                "    \"array\": [\n" +
                "        { \"name\": \"object1\" },\n" +
                "        { \"name\": \"object2\" }\n" +
                "    ]\n" +
                "}";
        // test defined path, it will throw AssertionError
        JsonAssert.with(json).assertNotDefined("array.*.name");
    }
}
