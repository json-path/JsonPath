package com.jayway.jsonpath.internal.text;

import com.jayway.jsonpath.JsonPath;
import org.junit.Test;

import static com.jayway.jsonpath.BaseTest.JSON_DOCUMENT;
import static org.junit.Assert.assertEquals;

/**
 * This is the test for the substring function
 * Idea from Issue#795 user peepshow-21
 */
public class SubstringTest {

    /**
     * This test for the substring of a simple string
     */
    @Test
    public void substringTestWithOneString() {
        String s = JsonPath.read("{'RfRaw':{'Data':'ABC436F601F405783CE00E55'}}", "$.RfRaw.Data.substring(16,22)");
        assertEquals("3CE00E", s);
    }

    /**
     * This test for the substring of a node
     */
    @Test
    public void substringTestWithOneNode() {
        String s = JsonPath.read("{'RfRaw':{'Data':'ABC436F601F405783CE00E55'}}", "$.RfRaw.substring(1,5)");
        assertEquals("Data", s);
    }

    /**
     * This test for the substring of nodes
     */
    @Test
    public void substringTestWithMulNode() {
        String s = JsonPath.read(JSON_DOCUMENT, "$.store.book.substring(3,11)");
        assertEquals("category", s);
    }
}
