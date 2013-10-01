package com.jayway.jsonpath;

import org.hamcrest.Matchers;
import org.junit.Test;

import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * test defined in http://jsonpath.googlecode.com/svn/trunk/tests/jsonpath-test-js.html
 */
public class ComplianceTest {

    @Test
    public void test_one() throws Exception {

        String json = "{ \"a\": \"a\",\n" +
                "           \"b\": \"b\",\n" +
                "           \"c d\": \"e\" \n" +
                "         }";

        assertThat(JsonPath.<String>read(json, "$.a"), equalTo("a"));
        assertThat(JsonPath.<List<String>>read(json, "$.*"), hasItems("a", "b", "e"));
        assertThat(JsonPath.<List<String>>read(json, "$[*]"), hasItems("a", "b", "e"));
        assertThat(JsonPath.<String>read(json, "$['a']"), equalTo("a"));
        assertThat(JsonPath.<String>read(json, "$.['c d']"), is(equalTo("e")));
        assertThat(JsonPath.<List<String>>read(json, "$[*]"), hasItems("a", "b", "e"));
    }
    
    @Test
    public void test_two() throws Exception {
        String json = "[ 1, \"2\", 3.14, true, null ]";

        assertThat(JsonPath.<Integer>read(json, "$[0]"), is(equalTo(1)));
        assertThat(JsonPath.<Integer>read(json, "$[4]"), is(equalTo(null)));
        assertThat(JsonPath.<List<Comparable>>read(json, "$[*]"), hasItems(
                new Integer(1),
                new String("2"),
                new Double(3.14),
                new Boolean(true),
                (Comparable)null));

        List<Object> res = JsonPath.read(json, "$[-1:]");

        assertTrue(res.get(0) == null);
    }

    @Test
    public void test_three() throws Exception {
        String json = "{ \"points\": [\n" +
                "             { \"id\": \"i1\", \"x\":  4, \"y\": -5 },\n" +
                "             { \"id\": \"i2\", \"x\": -2, \"y\":  2, \"z\": 1 },\n" +
                "             { \"id\": \"i3\", \"x\":  8, \"y\":  3 },\n" +
                "             { \"id\": \"i4\", \"x\": -6, \"y\": -1 },\n" +
                "             { \"id\": \"i5\", \"x\":  0, \"y\":  2, \"z\": 1 },\n" +
                "             { \"id\": \"i6\", \"x\":  1, \"y\":  4 }\n" +
                "           ]\n" +
                "         }";

        assertThat(JsonPath.<Map<String, Comparable>>read(json, "$.points[1]"), allOf(
                Matchers.<String, Comparable>hasEntry("id", "i2"),
                Matchers.<String, Comparable>hasEntry("x", -2),
                Matchers.<String, Comparable>hasEntry("y", 2),
                Matchers.<String, Comparable>hasEntry("z", 1)
        ));

        assertThat(JsonPath.<Integer>read(json, "$.points[4].x"), equalTo(0));
        assertThat(JsonPath.<List<Integer>>read(json, "$.points[?(@.id == 'i4')].x"), hasItem(-6));
        assertThat(JsonPath.<List<Integer>>read(json, "$.points[*].x"), hasItems(4, -2, 8, -6, 0, 1));
        assertThat(JsonPath.<List<String>>read(json, "$.points[?(@.z)].id"), hasItems("i2", "i5"));
        assertThat(JsonPath.<String>read(json, "$.points[(@.length - 1)].id"), equalTo("i6"));
    }

}
