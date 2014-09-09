package com.jayway.jsonpath;

import org.junit.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SuppressWarnings("ALL")
public class ReturnTypeTest extends BaseTest {

    @Test
    public void assert_strings_can_be_read() {
        assertThat(JsonPath.read(JSON_DOCUMENT, "$.string-property")).isEqualTo("string-value");
    }

    @Test
    public void assert_ints_can_be_read() {
        assertThat(JsonPath.read(JSON_DOCUMENT, "$.int-property")).isEqualTo(Integer.MAX_VALUE);
    }

    @Test
    public void assert_longs_can_be_read() {
        assertThat(JsonPath.read(JSON_DOCUMENT, "$.long-property")).isEqualTo(Long.MAX_VALUE);
    }

    @Test
    public void assert_boolean_values_can_be_read() {
        assertThat(JsonPath.read(JSON_DOCUMENT, "$.boolean-property")).isEqualTo(true);
    }

    @Test
    public void assert_null_values_can_be_read() {
        assertThat(JsonPath.read(JSON_DOCUMENT, "$.null-property")).isNull();
    }

    @Test
    public void assert_arrays_can_be_read_2() {
        List<Object> list = JsonPath.read(JSON_DOCUMENT, "$.store.book");

        assertThat(list).hasSize(4);
    }

    @Test
    public void assert_maps_can_be_read() {
        /*
        assertThat(JsonPath.parse(JSON_DOCUMENT).read("$.store.book[0]", Map.class)
                .containsEntry("category", "reference")
                .containsEntry("author", "Nigel Rees")
                .containsEntry("title", "Sayings of the Century")
                .containsEntry("display-price", 8.95D);
                */
    }
}
