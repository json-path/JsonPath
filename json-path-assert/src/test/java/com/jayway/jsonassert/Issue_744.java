package com.jayway.jsonassert;

import org.junit.Test;

public class Issue_744 {
    @org.junit.Test
    public void test_assert_not_defined(){
        String json = "{\n" +
                "    \"array\": [\n" +
                "        { \"name\": \"object1\" },\n" +
                "        { \"name\": \"object2\" }\n" +
                "    ]\n" +
                "}";
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
        JsonAssert.with(json).assertNotDefined("array.*.name");
    }
}
