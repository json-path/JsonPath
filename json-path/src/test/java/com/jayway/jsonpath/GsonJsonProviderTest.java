package com.jayway.jsonpath;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.jayway.jsonpath.spi.mapper.MappingException;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

import static com.jayway.jsonpath.JsonPath.using;
import static org.assertj.core.api.Assertions.assertThat;

public class GsonJsonProviderTest extends BaseTest {

    private static final String JSON =
            "[" +
            "{\n" +
            "   \"foo\" : \"foo0\",\n" +
            "   \"bar\" : 0,\n" +
            "   \"baz\" : true,\n" +
            "   \"gen\" : {\"eric\" : \"yepp\"}" +
            "}," +
            "{\n" +
            "   \"foo\" : \"foo1\",\n" +
            "   \"bar\" : 1,\n" +
            "   \"baz\" : true,\n" +
            "   \"gen\" : {\"eric\" : \"yepp\"}" +
            "}," +
            "{\n" +
            "   \"foo\" : \"foo2\",\n" +
            "   \"bar\" : 2,\n" +
            "   \"baz\" : true,\n" +
            "   \"gen\" : {\"eric\" : \"yepp\"}" +
            "}" +
            "]";

    @Test
    public void json_can_be_parsed() {
        JsonObject node =  using(GSON_CONFIGURATION).parse(JSON_DOCUMENT).read("$");
        assertThat(node.get("string-property").getAsString()).isEqualTo("string-value");
    }

    @Test
    public void strings_are_unwrapped() {
        JsonElement node =  using(GSON_CONFIGURATION).parse(JSON_DOCUMENT).read("$.string-property");
        String unwrapped =  using(GSON_CONFIGURATION).parse(JSON_DOCUMENT).read("$.string-property", String.class);

        assertThat(unwrapped).isEqualTo("string-value");
        assertThat(unwrapped).isEqualTo(node.getAsString());
    }

    @Test
    public void ints_are_unwrapped() {
        JsonElement node =  using(GSON_CONFIGURATION).parse(JSON_DOCUMENT).read("$.int-max-property");
        int unwrapped =  using(GSON_CONFIGURATION).parse(JSON_DOCUMENT).read("$.int-max-property", int.class);

        assertThat(unwrapped).isEqualTo(Integer.MAX_VALUE);
        assertThat(unwrapped).isEqualTo(node.getAsInt());
    }

    @Test
    public void longs_are_unwrapped() {
        JsonElement node =  using(GSON_CONFIGURATION).parse(JSON_DOCUMENT).read("$.long-max-property");
        long val =  using(GSON_CONFIGURATION).parse(JSON_DOCUMENT).read("$.long-max-property", Long.class);

        assertThat(val).isEqualTo(Long.MAX_VALUE);
        assertThat(val).isEqualTo(node.getAsLong());
    }

    @Test
    public void int_to_long_mapping() {
        assertThat(using(GSON_CONFIGURATION).parse("{\"val\": 1}").read("val", Long.class)).isEqualTo(1L);
    }

    @Test
    public void an_Integer_can_be_converted_to_a_Double() {
        assertThat(using(GSON_CONFIGURATION).parse("{\"val\": 1}").read("val", Double.class)).isEqualTo(1D);
    }

    @Test
    public void list_of_numbers() {
        JsonArray objs =  using(GSON_CONFIGURATION).parse(JSON_DOCUMENT).read("$.store.book[*].display-price");

        assertThat(objs.iterator()).extracting("asDouble").containsExactly(8.95D, 12.99D, 8.99D, 22.99D);

    }

    @Test
    public void an_object_can_be_mapped_to_pojo() {

        String json = "{\n" +
                "   \"foo\" : \"foo\",\n" +
                "   \"bar\" : 10,\n" +
                "   \"baz\" : true\n" +
                "}";


        TestClazz testClazz = JsonPath.using(GSON_CONFIGURATION).parse(json).read("$", TestClazz.class);

        assertThat(testClazz.foo).isEqualTo("foo");
        assertThat(testClazz.bar).isEqualTo(10L);
        assertThat(testClazz.baz).isEqualTo(true);

    }

    @Test
    public void test_type_ref() throws IOException {
        TypeRef<List<FooBarBaz<Gen>>> typeRef = new TypeRef<List<FooBarBaz<Gen>>>() {};

        List<FooBarBaz<Gen>> list = JsonPath.using(GSON_CONFIGURATION).parse(JSON).read("$", typeRef);

        assertThat(list.get(0).gen.eric).isEqualTo("yepp");
    }

    @Test(expected = MappingException.class)
    public void test_type_ref_fail() throws IOException {
        TypeRef<List<FooBarBaz<Integer>>> typeRef = new TypeRef<List<FooBarBaz<Integer>>>() {};

        using(GSON_CONFIGURATION).parse(JSON).read("$", typeRef);
    }

    public static class FooBarBaz<T> {
        public T gen;
        public String foo;
        public Long bar;
        public boolean baz;
    }


    public static class Gen {
        public String eric;
    }

    public static class TestClazz {
        public String foo;
        public Long bar;
        public boolean baz;
    }




}
