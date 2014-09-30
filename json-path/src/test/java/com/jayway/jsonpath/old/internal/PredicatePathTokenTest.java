package com.jayway.jsonpath.old.internal;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import org.junit.Test;

import java.util.List;
import java.util.Map;

import static java.util.Collections.singletonMap;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;

public class PredicatePathTokenTest {

    private static final Object ARRAY = Configuration.defaultConfiguration().jsonProvider().parse(
            "[" +
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
                    "]");

    private static Object  ARRAY2 = Configuration.defaultConfiguration().jsonProvider().parse(
            "[" +
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
            "]");


    @Test
    public void a_filter_predicate_can_be_evaluated_on_string_criteria() {

        List<Map> result = JsonPath.read (ARRAY, "$[?(@.foo == 'foo-val-1')]");

        assertThat(result).containsOnly(singletonMap("foo", "foo-val-1"));
    }

    @Test
    public void a_filter_predicate_can_be_evaluated_on_int_criteria() {

        List<Map> result = JsonPath.read (ARRAY2, "$[?(@.int == 1)]");

        assertThat(result).hasSize(1);
        assertThat(result.get(0)).contains(entry("int", 1));
    }

    @Test
    public void a_filter_predicate_can_be_evaluated_on_decimal_criteria() {

        List<Map> result = JsonPath.read (ARRAY2, "$[?(@.decimal == 0.1)]");

        assertThat(result).hasSize(1);
        assertThat(result.get(0)).contains(entry("decimal", 0.1));
    }

    @Test
    public void multiple_criteria_can_be_used() {

        List<Map> result = JsonPath.read (ARRAY2, "$[?(@.decimal == 0.1 && @.int == 1)]");

        assertThat(result).hasSize(1);
        assertThat(result.get(0)).contains(entry("foo", "foo-val-1"));
    }

    @Test
    public void field_existence_can_be_checked() {

        List<Map> result = JsonPath.read (ARRAY2, "$[?(@.bool)]");

        assertThat(result).hasSize(1);
        assertThat(result.get(0)).contains(entry("foo", "foo-val-7"));
    }

    @Test
    public void boolean_criteria_evaluates() {

        List<Map> result = JsonPath.read (ARRAY2, "$[?(@.bool == true)]");

        assertThat(result).hasSize(1);
        assertThat(result.get(0)).contains(entry("foo", "foo-val-7"));
    }

}
