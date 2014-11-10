package com.jayway.jsonpath;

import org.junit.Test;

import java.io.IOException;
import java.util.List;

import static com.jayway.jsonpath.JsonPath.using;
import static org.assertj.core.api.Assertions.assertThat;

public class JsonProviderTest extends BaseTest {

    private static final String JSON =
            "[" +
            "{\n" +
            "   \"foo\" : \"foo0\",\n" +
            "   \"bar\" : 0,\n" +
            "   \"baz\" : true,\n" +
            "   \"gen\" : {\"prop\" : \"yepp0\"}" +
            "}," +
            "{\n" +
            "   \"foo\" : \"foo1\",\n" +
            "   \"bar\" : 1,\n" +
            "   \"baz\" : true,\n" +
            "   \"gen\" : {\"prop\" : \"yepp1\"}" +
            "}," +
            "{\n" +
            "   \"foo\" : \"foo2\",\n" +
            "   \"bar\" : 2,\n" +
            "   \"baz\" : true,\n" +
            "   \"gen\" : {\"prop\" : \"yepp2\"}" +
            "}" +
            "]";

    @Test
    public void strings_are_unwrapped() {
        assertThat(using(JACKSON_CONFIGURATION).parse(JSON_DOCUMENT).read("$.string-property", String.class)).isEqualTo("string-value");
        assertThat(using(JACKSON_TREE_CONFIGURATION).parse(JSON_DOCUMENT).read("$.string-property", String.class)).isEqualTo("string-value");
        assertThat(using(JSON_SMART_CONFIGURATION).parse(JSON_DOCUMENT).read("$.string-property", String.class)).isEqualTo("string-value");
        assertThat(using(GSON_CONFIGURATION).parse(JSON_DOCUMENT).read("$.string-property", String.class)).isEqualTo("string-value");
    }

    @Test
    public void integers_are_unwrapped() {
        assertThat(using(JACKSON_CONFIGURATION).parse(JSON_DOCUMENT).read("$.int-max-property", Integer.class)).isEqualTo(Integer.MAX_VALUE);
        assertThat(using(JACKSON_TREE_CONFIGURATION).parse(JSON_DOCUMENT).read("$.int-max-property", Integer.class)).isEqualTo(Integer.MAX_VALUE);
        assertThat(using(JSON_SMART_CONFIGURATION).parse(JSON_DOCUMENT).read("$.int-max-property", Integer.class)).isEqualTo(Integer.MAX_VALUE);
        assertThat(using(GSON_CONFIGURATION).parse(JSON_DOCUMENT).read("$.int-max-property", Integer.class)).isEqualTo(Integer.MAX_VALUE);
    }

    @Test
    public void ints_are_unwrapped() {
        assertThat(using(JACKSON_CONFIGURATION).parse(JSON_DOCUMENT).read("$.int-max-property", int.class)).isEqualTo(Integer.MAX_VALUE);
        assertThat(using(JACKSON_TREE_CONFIGURATION).parse(JSON_DOCUMENT).read("$.int-max-property", int.class)).isEqualTo(Integer.MAX_VALUE);
        assertThat(using(JSON_SMART_CONFIGURATION).parse(JSON_DOCUMENT).read("$.int-max-property", int.class)).isEqualTo(Integer.MAX_VALUE);
        assertThat(using(GSON_CONFIGURATION).parse(JSON_DOCUMENT).read("$.int-max-property", int.class)).isEqualTo(Integer.MAX_VALUE);
    }

    @Test
    public void list_of_numbers() {

        TypeRef<List<Double>> typeRef = new TypeRef<List<Double>>() {};

        assertThat(using(JACKSON_TREE_CONFIGURATION).parse(JSON_DOCUMENT).read("$.store.book[*].display-price", typeRef)).containsExactly(8.95D, 12.99D, 8.99D, 22.99D);
        assertThat(using(JACKSON_CONFIGURATION).parse(JSON_DOCUMENT).read("$.store.book[*].display-price", typeRef)).containsExactly(8.95D, 12.99D, 8.99D, 22.99D);
        assertThat(using(GSON_CONFIGURATION).parse(JSON_DOCUMENT).read("$.store.book[*].display-price", typeRef)).containsExactly(8.95D, 12.99D, 8.99D, 22.99D);
    }

    @Test
    public void test_type_ref() throws IOException {
        TypeRef<List<FooBarBaz<Sub>>> typeRef = new TypeRef<List<FooBarBaz<Sub>>>() {};

        assertThat(using(JACKSON_CONFIGURATION).parse(JSON).read("$", typeRef)).extracting("foo").containsExactly("foo0", "foo1", "foo2");
        assertThat(using(JACKSON_TREE_CONFIGURATION).parse(JSON).read("$", typeRef)).extracting("foo").containsExactly("foo0", "foo1", "foo2");
        assertThat(using(GSON_CONFIGURATION).parse(JSON).read("$", typeRef)).extracting("foo").containsExactly("foo0", "foo1", "foo2");
    }


    public static class FooBarBaz<T> {
        public T gen;
        public String foo;
        public Long bar;
        public boolean baz;
    }


    public static class Sub {
        public String prop;
    }

}
