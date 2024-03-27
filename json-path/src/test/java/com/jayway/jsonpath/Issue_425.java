package com.jayway.jsonpath;


import org.junit.Test;

/**
 * test for issue 425
 */
public class Issue_425 {

    @Test
    public void testCheckPathFunction() {
        Boolean valid= JsonPath.isPathValid("{\n" +
                "   \"a\": [1,2,3],\n" +
                "   \"b\": [4,5,6]\n" +
                "}", "$.c");
        assert (valid.equals(Boolean.FALSE));
        valid= JsonPath.isPathValid("{\n" +
                "   \"a\": [1,2,3],\n" +
                "   \"b\": [4,5,6]\n" +
                "}", "$.a");
        assert (valid.equals(Boolean.TRUE));
    }
}

