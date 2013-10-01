package com.jayway.jsonpath.internal2;

import com.jayway.jsonpath.spi.json.JsonProvider;
import com.jayway.jsonpath.spi.json.JsonProviderFactory;

public class WildcardPathFragmentTest {


    private JsonProvider jsonProvider = JsonProviderFactory.createProvider();

    private String ARRAY = "[" +
            "{\n" +
            "   \"foo\" : \"foo-val-0\",\n" +
            "   \"int\" : 0\n," +
            "   \"decimal\" : 0.0\n" +
            "}," +
            "{\n" +
            "   \"foo\" : \"foo-val-1\",\n" +
            "   \"int\" : 1,\n" +
            "   \"decimal\" : 0.1\n" +
            "}," +
            "{\n" +
            "   \"foo\" : \"foo-val-2\",\n" +
            "   \"int\" : 2,\n" +
            "   \"decimal\" : 0.2\n" +
            "}," +
            "{\n" +
            "   \"foo\" : \"foo-val-3\",\n" +
            "   \"int\" : 3,\n" +
            "   \"decimal\" : 0.3\n" +
            "}," +
            "{\n" +
            "   \"foo\" : \"foo-val-4\",\n" +
            "   \"int\" : 4,\n" +
            "   \"decimal\" : 0.4\n" +
            "}," +
            "{\n" +
            "   \"foo\" : \"foo-val-5\",\n" +
            "   \"int\" : 5,\n" +
            "   \"decimal\" : 0.5\n" +
            "}," +
            "{\n" +
            "   \"foo\" : \"foo-val-6\",\n" +
            "   \"int\" : 6,\n" +
            "   \"decimal\" : 0.6\n" +
            "}," +
            "{\n" +
            "   \"foo\" : \"foo-val-7\",\n" +
            "   \"int\" : 7,\n" +
            "   \"decimal\" : 0.7,\n" +
            "   \"bool\" : true\n" +
            "}" +
            "]";

    /*
    @Test
    public void wildcard_on_array_returns_all_items() {

        PathEvaluationResult result = PathEvaluator.evaluate("$[*]", ARRAY, jsonProvider, Collections.EMPTY_SET);

        assertTrue(jsonProvider.isArray(result.getResult()));
        assertEquals(8, jsonProvider.length(result.getResult()));

    }
    @Test
    public void wildcard_on_map_returns_all_attribute_values() {

        PathEvaluationResult result = PathEvaluator.evaluate("$[1].*", ARRAY, jsonProvider, Collections.EMPTY_SET);

        assertTrue(jsonProvider.isArray(result.getResult()));
        assertEquals(3, jsonProvider.length(result.getResult()));
    }
    */

}
