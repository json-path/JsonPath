package com.jayway.jsonpath;

import org.junit.Test;

import java.util.List;
import java.util.Map;

import static com.jayway.jsonpath.JsonPath.parse;
import static com.jayway.jsonpath.JsonPath.using;
import static com.jayway.jsonpath.Option.AS_PATH_LIST;
import static org.assertj.core.api.Assertions.assertThat;

@SuppressWarnings("ALL")
public class ReturnTypeTest extends BaseTest {


    private static ReadContext reader = parse(JSON_DOCUMENT);

    @Test
    public void assert_strings_can_be_read() {
        assertThat((String)reader.read("$.string-property")).isEqualTo("string-value");
    }

    @Test
    public void assert_ints_can_be_read() {
        assertThat(reader.read("$.int-max-property", Integer.class)).isEqualTo(Integer.MAX_VALUE);
    }

    @Test
    public void assert_longs_can_be_read() {
        assertThat((Long)reader.read("$.long-max-property")).isEqualTo(Long.MAX_VALUE);
    }

    @Test
    public void assert_boolean_values_can_be_read() {
        assertThat((Boolean)reader.read("$.boolean-property")).isEqualTo(true);
    }

    @Test
    public void assert_null_values_can_be_read() {
        assertThat((String)reader.read("$.null-property")).isNull();
    }

    @Test
    public void assert_arrays_can_be_read() {
        /*
        Object result = reader.read("$.store.book");

        assertThat(reader.configuration().jsonProvider().isArray(result)).isTrue();

        assertThat(reader.configuration().jsonProvider().length(result)).isEqualTo(4);
        */
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

    @Test
    public void a_path_evaluation_can_be_returned_as_PATH_LIST() {
        Configuration conf = Configuration.builder().options(AS_PATH_LIST).build();

        List<String> pathList = using(conf).parse(JSON_DOCUMENT).read("$..author");

        assertThat(pathList).containsExactly(
                "$['store']['book'][0]['author']",
                "$['store']['book'][1]['author']",
                "$['store']['book'][2]['author']",
                "$['store']['book'][3]['author']");

    }

    @Test(expected = ClassCastException.class)
    public void class_cast_exception_is_thrown_when_return_type_is_not_expected() {
        List<String>  list = reader.read("$.store.book[0].author");
    }
}
