package com.jayway.jsonpath;

import org.junit.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SuppressWarnings("ALL")
public class ReturnTypeTest extends BaseTest {

    private static ReadContext reader = JsonPath.parse(JSON_DOCUMENT);

    @Test
    public void assert_strings_can_be_read() {
        assertThat(reader.read("$.string-property")).isEqualTo("string-value");
    }

    @Test
    public void assert_ints_can_be_read() {
        assertThat(reader.read("$.int-max-property")).isEqualTo(Integer.MAX_VALUE);
    }

    @Test
    public void assert_longs_can_be_read() {
        assertThat(reader.read("$.long-max-property")).isEqualTo(Long.MAX_VALUE);
    }

    @Test
    public void assert_boolean_values_can_be_read() {
        assertThat(reader.read("$.boolean-property")).isEqualTo(true);
    }

    @Test
    public void assert_null_values_can_be_read() {
        assertThat(reader.read("$.null-property")).isNull();
    }

    @Test
    public void assert_arrays_can_be_read() {
        assertThat(reader.read("$.store.book", List.class)).hasSize(4);
    }

    @Test
    public void assert_maps_can_be_read() {

        assertThat(reader.read("$.store.book[0]", Map.class))
                .containsEntry("category", "reference")
                .containsEntry("author", "Nigel Rees")
                .containsEntry("title", "Sayings of the Century")
                .containsEntry("display-price", 8.95D);

    }
}
