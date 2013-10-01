package com.jayway.jsonpath.internal2;

import com.jayway.jsonpath.spi.json.JsonProvider;
import com.jayway.jsonpath.spi.json.JsonProviderFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FilterPredicatePathFragmentTest {

    private static final Logger logger = LoggerFactory.getLogger(FilterPredicatePathFragmentTest.class);

    private JsonProvider jsonProvider = JsonProviderFactory.createProvider();

    private String SIMPLE_ARRAY = "[" +
            "{\n" +
            "   \"foo\" : \"foo-val-0\"\n" +
            "}," +
            "{\n" +
            "   \"foo\" : \"foo-val-1\"\n" +
            "}," +
            "{\n" +
            "   \"foo\" : \"foo-val-2\"\n" +
            "}," +
            "{\n" +
            "   \"foo\" : \"foo-val-3\"\n" +
            "}," +
            "{\n" +
            "   \"foo\" : \"foo-val-4\"\n" +
            "}," +
            "{\n" +
            "   \"foo\" : \"foo-val-5\"\n" +
            "}," +
            "{\n" +
            "   \"foo\" : \"foo-val-6\"\n" +
            "}" +
            "]";

    private String SIMPLE_ARRAY_2 = "[" +
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
    public void a_filter_predicate_can_be_evaluated_on_string_criteria() {

        PathEvaluationResult result = PathEvaluator.evaluate("$[?(@.foo == 'foo-val-1')]", SIMPLE_ARRAY, jsonProvider, Collections.EMPTY_SET);

        assertThat((String)JsonPath.read(result.getResult(), "[0].foo"), is("foo-val-1"));
    }

    @Test
    public void a_filter_predicate_can_be_evaluated_on_int_criteria() {

        PathEvaluationResult result = PathEvaluator.evaluate("$[?(@.int == 1)]", SIMPLE_ARRAY_2, jsonProvider, Collections.EMPTY_SET);

        assertThat((String)JsonPath.read(result.getResult(), "[0].foo"), is("foo-val-1"));
    }

    @Test
    public void a_filter_predicate_can_be_evaluated_on_decimal_criteria() {

        PathEvaluationResult result = PathEvaluator.evaluate("$[?(@.decimal == 0.1)]", SIMPLE_ARRAY_2, jsonProvider, Collections.EMPTY_SET);

        assertThat((String)JsonPath.read(result.getResult(), "[0].foo"), is("foo-val-1"));
    }

    @Test
    public void multiple_criteria_can_be_used() {

        PathEvaluationResult result = PathEvaluator.evaluate("$[?(@.decimal == 0.1 && @.int == 1)]", SIMPLE_ARRAY_2, jsonProvider, Collections.EMPTY_SET);

        assertThat((String)JsonPath.read(result.getResult(), "[0].foo"), is("foo-val-1"));
    }

    @Test
    public void field_existence_can_be_checked() {

        PathEvaluationResult result = PathEvaluator.evaluate("$[?(@.bool)]", SIMPLE_ARRAY_2, jsonProvider, Collections.EMPTY_SET);

        assertThat((String)JsonPath.read(result.getResult(), "[0].foo"), is("foo-val-7"));
    }

    @Test
    public void boolean_criteria_evaluates() {

        PathEvaluationResult result = PathEvaluator.evaluate("$[?(@.bool == true)]", SIMPLE_ARRAY_2, jsonProvider, Collections.EMPTY_SET);

        assertThat((String)JsonPath.read(result.getResult(), "[0].foo"), is("foo-val-7"));
    }
    */
}
