package com.jayway.jsonpath.internal.function;

import com.jayway.jsonpath.JsonPath;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test for length function. Length class: {@link com.jayway.jsonpath.internal.function.text.Length}
 */
public class LengthFunctionTest {

    private static final String testString = "{\n" +
            "\t\"store\": {\n" +
            "\t\t\"book\": [[\"book0-1\",\"book0-2\"],\"book1\", \"book2\", [\"book3\"]]" +
            "\t},\n" +
            "    \"store1\": {\n" +
            "\t\t\"book\": [\"book4\", [\"book5-1\",\"book5-2\"], \"book6\",[\"book7\"]]\n" +
            "\t}\n" +
            "}";

    /**
     * Test case for definite path
     */
    @Test
    public void testDefinite(){
        assertThat((int)JsonPath.read(testString,"$.length($.store.book)")).isEqualTo(4);
        assertThat((int)JsonPath.read(testString,"$.length($.store.book[0])")).isEqualTo(2);
        assertThat((int)JsonPath.read(testString,"$.length($.store1.book)")).isEqualTo(4);
    }
    /**
     * Test case for indefinite path
     */
    @Test
    public void testIndefinite(){
        assertThat((int)JsonPath.read(testString,"$.length($..book)")).isEqualTo(8);
        assertThat((int)JsonPath.read(testString,"$.length($.store..book)")).isEqualTo(4);
        assertThat((int)JsonPath.read(testString,"$.length($.store1..book)")).isEqualTo(4);
        assertThat((int)JsonPath.read(testString,"$.length($.store1..book[1])")).isEqualTo(2);
    }

}
