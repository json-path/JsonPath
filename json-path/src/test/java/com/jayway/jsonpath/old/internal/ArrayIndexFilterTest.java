package com.jayway.jsonpath.old.internal;

import com.jayway.jsonpath.JsonPath;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ArrayIndexFilterTest {

    private static final String JSON = "[1, 3, 5, 7, 8, 13, 20]";

    @Test
    public void tail_does_not_throw_when_index_out_of_bounds() {
        List<Integer> result = JsonPath.parse(JSON).read("$[-10:]");
        assertThat(result, Matchers.contains(1, 3, 5, 7, 8, 13, 20));
    }

    @Test
    public void head_does_not_throw_when_index_out_of_bounds() {
        List<Integer> result = JsonPath.parse(JSON).read("$[:10]");
        assertThat(result, Matchers.contains(1, 3, 5, 7, 8, 13, 20));
    }

    @Test
    public void head_grabs_correct() {
        List<Integer> result = JsonPath.parse(JSON).read("$[:3]");
        assertThat(result, Matchers.contains(1, 3, 5));
    }


    @Test
    public void tail_grabs_correct() {
        List<Integer> result = JsonPath.parse(JSON).read("$[-3:]");
        assertThat(result, Matchers.contains(8, 13, 20));
    }

    @Test
    public void head_tail_grabs_correct() {
        List<Integer> result = JsonPath.parse(JSON).read("$[0:3]");
        assertThat(result, Matchers.contains(1, 3, 5));
    }

    @Test
    public void can_access_items_from_end_with_negative_index() {
        int result = JsonPath.parse(JSON).read("$[-3]");
        assertEquals(8, result);
    }

}
