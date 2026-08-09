package com.jayway.jsonpath.old;

import com.jayway.jsonpath.JsonPath;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * If you have a list
 * nums = [1, 3, 5, 7, 8, 13, 20]
 * then it is possible to slice by using a notation similar to element retrieval:
 * <p>
 * nums[3]   #equals 7, no slicing
 * nums[:3]  #equals [1, 3, 5], from index 0 (inclusive) until index 3 (exclusive)
 * nums[1:5] #equals [3, 5, 7, 8]
 * nums[-3:] #equals [8, 13, 20]
 * nums[3:] #equals [8, 13, 20]
 * <p>
 * Note that Python allows negative list indices. The index -1 represents the last element, -2 the penultimate element, etc.
 * Python also allows a step property by appending an extra colon and a value. For example:
 * <p>
 * nums[3::]  #equals [7, 8, 13, 20], same as nums[3:]
 * nums[::3]  #equals [1, 7, 20] (starting at index 0 and getting every third element)
 * nums[1:5:2] #equals [3, 7] (from index 1 until index 5 and getting every second element)
 */
public class ArraySlicingTest {

    public static final String JSON_ARRAY = "[1, 3, 5, 7, 8, 13, 20]";

    @Test
    public void get_by_position() {
        Integer result = JsonPath.read(JSON_ARRAY, "$[3]");
        assertEquals(7, result.intValue());
    }

    @Test
    public void get_from_index() {
        List<Integer> result = JsonPath.read(JSON_ARRAY, "$[:3]");
        assertThat(result, Matchers.contains(1, 3, 5));
    }

    @Test
    public void get_between_index() {
        List<Integer> result = JsonPath.read(JSON_ARRAY, "$[1:5]");
        assertThat(result, Matchers.contains(3, 5, 7, 8));
    }


    @Test
    public void get_between_index_2() {
        List<Integer> result = JsonPath.read(JSON_ARRAY, "$[0:1]");
        assertThat(result, Matchers.contains(1));
    }

    @Test
    public void get_between_index_3() {
        List<Integer> result = JsonPath.read(JSON_ARRAY, "$[0:2]");
        assertThat(result, Matchers.contains(1, 3));
    }

    @Test
    public void get_between_index_out_of_bounds() {
        List<Integer> result = JsonPath.read(JSON_ARRAY, "$[1:15]");
        assertThat(result, Matchers.contains(3, 5, 7, 8, 13, 20));
    }

    @Test
    public void get_from_tail_index() {
        List<Integer> result = JsonPath.read(JSON_ARRAY, "$[-3:]");
        assertThat(result, Matchers.contains(8, 13, 20));
    }

    @Test
    public void get_from_tail() {
        List<Integer> result = JsonPath.read(JSON_ARRAY, "$[3:]");
        assertThat(result, Matchers.contains(7, 8, 13, 20));
    }

    @Test
    public void get_indexes() {
        List<Integer> result = JsonPath.read(JSON_ARRAY, "$[0,1,2]");
        assertThat(result, Matchers.contains(1, 3, 5));
    }


    @Test
    public void slice_between_with_negative_from() {
        // Negative "from" must be normalized against the array length, like sliceFrom.
        // $[-2:4] on a 5-element array -> from index 3, to index 4 -> [3] (#1076).
        // Previously "from" was not normalized, so the slice looped from -2 and
        // wrapped around, producing a duplicated/shifted result.
        List<Integer> result = JsonPath.read("[0, 1, 2, 3, 4]", "$[-2:4]");
        assertThat(result, Matchers.contains(3));
    }

    @Test
    public void slice_between_with_negative_to() {
        // Negative "to" must be normalized against the array length, like sliceTo.
        // $[1:-1] on a 5-element array -> from 1, to 4 -> [1, 2, 3] (#1076).
        // Previously "to" was only clamped via Math.min(length, to), so a negative
        // "to" made from >= to and returned an empty list.
        List<Integer> result = JsonPath.read("[0, 1, 2, 3, 4]", "$[1:-1]");
        assertThat(result, Matchers.contains(1, 2, 3));
    }

}
