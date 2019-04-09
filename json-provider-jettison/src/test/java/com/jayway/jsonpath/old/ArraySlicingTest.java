package com.jayway.jsonpath.old;

import com.jayway.jsonpath.JsonPath;
import org.hamcrest.Matchers;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

/**
 *
 * If you have a list
 * nums = [1, 3, 5, 7, 8, 13, 20]
 * then it is possible to slice by using a notation similar to element retrieval:
 *
 * nums[3]   #equals 7, no slicing
 * nums[:3]  #equals [1, 3, 5], from index 0 (inclusive) until index 3 (exclusive)
 * nums[1:5] #equals [3, 5, 7, 8]
 * nums[-3:] #equals [8, 13, 20]
 * nums[3:] #equals [8, 13, 20]
 *
 * Note that Python allows negative list indices. The index -1 represents the last element, -2 the penultimate element, etc.
 * Python also allows a step property by appending an extra colon and a value. For example:
 *
 * nums[3::]  #equals [7, 8, 13, 20], same as nums[3:]
 * nums[::3]  #equals [1, 7, 20] (starting at index 0 and getting every third element)
 * nums[1:5:2] #equals [3, 7] (from index 1 until index 5 and getting every second element)

 *
 */
public class ArraySlicingTest {

    public static final String JSON_ARRAY = "[1, 3, 5, 7, 8, 13, 20]";

    @Test
    public void get_by_position(){
        Integer result = JsonPath.read(JSON_ARRAY, "$[3]");
        assertEquals(7, result.intValue());
    }

    @Test
    public void get_from_index(){
        List<Integer> result = JsonPath.read(JSON_ARRAY, "$[:3]");
        assertThat(result, Matchers.contains(1,3,5));
    }

    @Test
    public void get_between_index(){
        List<Integer> result = JsonPath.read(JSON_ARRAY, "$[1:5]");
        assertThat(result, Matchers.contains(3, 5, 7, 8));
    }


    @Test
    public void get_between_index_2(){
        List<Integer> result = JsonPath.read(JSON_ARRAY, "$[0:1]");
        assertThat(result, Matchers.contains(1));
    }

    @Test
    public void get_between_index_3(){
        List<Integer> result = JsonPath.read(JSON_ARRAY, "$[0:2]");
        assertThat(result, Matchers.contains(1,3));
    }

    @Test
    public void get_between_index_out_of_bounds(){
        List<Integer> result = JsonPath.read(JSON_ARRAY, "$[1:15]");
        assertThat(result, Matchers.contains(3, 5, 7, 8, 13, 20));
    }

    @Test
    public void get_from_tail_index(){
        List<Integer> result = JsonPath.read(JSON_ARRAY, "$[-3:]");
        assertThat(result, Matchers.contains(8, 13, 20));
    }

    @Test
    public void get_from_tail(){
        List<Integer> result  = JsonPath.read(JSON_ARRAY, "$[3:]");
        assertThat(result, Matchers.contains(7, 8, 13, 20));
    }

    @Test
    public void get_indexes(){
        List<Integer> result = JsonPath.read(JSON_ARRAY, "$[0,1,2]");
        assertThat(result, Matchers.contains(1,3,5));
    }

}
