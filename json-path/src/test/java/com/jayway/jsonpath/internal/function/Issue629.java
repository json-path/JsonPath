package com.jayway.jsonpath.internal.function;

import com.jayway.jsonpath.JsonPath;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertTrue;

public class Issue629 {
    @Test
    public void testUncloseParenthesis() throws IOException {
        try {
            JsonPath jsonPath = JsonPath.compile("$.A.B.C.D(");
            assert(false);
        }
        catch (Exception e) {
            assertTrue(e.getMessage().startsWith("Arguments to function:"));
        }
    }

    @Test
    public void testUncloseParenthesisWithNestedCall() throws IOException {
        try {
            JsonPath jsonPath = JsonPath.compile("$.A.B.C.sum(D()");
            assert(false);
        }
        catch (Exception e) {
            assertTrue(e.getMessage().startsWith("Arguments to function:"));
        }
    }
}
