package com.jayway.jsonpath.old.internal;

import org.junit.Test;

import java.util.List;
import java.util.Map;

import static com.jayway.jsonpath.JsonPath.read;
import static java.util.Collections.singletonMap;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.data.MapEntry.entry;

public class ArrayPathTokenTest extends TestBase {


    @Test
    public void array_can_select_single_index_by_context_length() {

        Map<String, Object> result = read(ARRAY, "$[(@.length-1)]");

        assertThat(result).contains(entry("foo", "foo-val-6"));
    }

    @Test
    public void array_can_select_multiple_indexes() {

        List<Map> result = read(ARRAY, "$[0,1]");

        assertThat(result).containsOnly(
                singletonMap("foo", "foo-val-0"),
                singletonMap("foo", "foo-val-1"));
    }

    @Test
    public void array_can_be_sliced_to_2() {

        List<Map> result = read(ARRAY, "$[:2]");

        assertThat(result).containsOnly(
                singletonMap("foo", "foo-val-0"),
                singletonMap("foo", "foo-val-1"));

    }

    @Test
    public void array_can_be_sliced_to_2_from_tail() {

        List<Map> result = read(ARRAY, "$[:-5]");

        assertThat(result).containsOnly(
                singletonMap("foo", "foo-val-0"),
                singletonMap("foo", "foo-val-1"));

    }

    @Test
    public void array_can_be_sliced_from_2() {

        List<Map> result = read(ARRAY, "$[5:]");

        assertThat(result).containsOnly(
                singletonMap("foo", "foo-val-5"),
                singletonMap("foo", "foo-val-6"));

    }

    @Test
    public void array_can_be_sliced_from_2_from_tail() {

        List<Map> result = read(ARRAY, "$[-2:]");

        assertThat(result).containsOnly(
                singletonMap("foo", "foo-val-5"),
                singletonMap("foo", "foo-val-6"));

    }

    @Test
    public void array_can_be_sliced_between() {

        List<Map> result = read(ARRAY, "$[2:4]");

        assertThat(result).containsOnly(
                singletonMap("foo", "foo-val-2"),
                singletonMap("foo", "foo-val-3"));

    }
}
