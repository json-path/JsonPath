package com.jayway.jsonpath;

import org.junit.Test;

import static com.jayway.jsonpath.BaseTest.JSON_DOCUMENT;


/**
 * test for issue 762
 */

public class Issue_762 {
    @Test
    public void testParseJsonValue(){
        assert(JsonPath.parse(5).jsonString().equals("5"));
        assert(JsonPath.parse(5.0).jsonString().equals("5.0"));
        assert(JsonPath.parse(true).jsonString().equals("true"));
        assert(JsonPath.parse(false).jsonString().equals("false"));
    }
}
