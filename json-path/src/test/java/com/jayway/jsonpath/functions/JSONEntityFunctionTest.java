package com.jayway.jsonpath.functions;

import org.junit.Test;

/**
 * Verifies methods that are helper implementations of functions for manipulating JSON entities, i.e.
 * length, etc.
 *
 * Created by mattg on 6/27/15.
 */
public class JSONEntityFunctionTest extends BaseFunctionTest {
    @Test
    public void testLengthOfTextArray() {
        // The length of JSONArray is an integer
        System.out.println(TEXT_SERIES);
        verifyFunction("$['text'].%length()", TEXT_SERIES, 6);
    }
    @Test
    public void testLengthOfNumberArray() {
        // The length of JSONArray is an integer
        verifyFunction("$.numbers.%length()", NUMBER_SERIES, 10);
    }
}
