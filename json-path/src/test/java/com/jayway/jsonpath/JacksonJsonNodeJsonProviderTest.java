package com.jayway.jsonpath;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.jayway.jsonpath.spi.mapper.MappingException;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

import static com.jayway.jsonpath.JsonPath.using;
import static org.assertj.core.api.Assertions.assertThat;

public class JacksonJsonNodeJsonProviderTest extends BaseTest {

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
        ObjectNode node = using(JACKSON_JSON_NODE_CONFIGURATION).parse(JSON_DOCUMENT).read("$");
        assertThat(node.get("string-property").asText()).isEqualTo("string-value");
    }

    @Test
    public void always_return_same_object() { // Test because of Bug #211
    	DocumentContext context = using(JACKSON_JSON_NODE_CONFIGURATION).parse(JSON_DOCUMENT);
        ObjectNode node1 = context.read("$");
        ObjectNode child1 = new ObjectNode(JsonNodeFactory.instance);
        child1.put("name", "test");
        context.put("$", "child", child1);
        ObjectNode node2 = context.read("$");
        ObjectNode child2 = context.read("$.child");
        
        assertThat(node1).isSameAs(node2);
        assertThat(child1).isSameAs(child2);
    }

    @Test
    public void strings_are_unwrapped() {
        JsonNode node = using(JACKSON_JSON_NODE_CONFIGURATION).parse(JSON_DOCUMENT).read("$.string-property");
        String unwrapped = using(JACKSON_JSON_NODE_CONFIGURATION).parse(JSON_DOCUMENT).read("$.string-property", String.class);

        assertThat(unwrapped).isEqualTo("string-value");
        assertThat(unwrapped).isEqualTo(node.asText());
    }

    @Test
    public void ints_are_unwrapped() {
        JsonNode node = using(JACKSON_JSON_NODE_CONFIGURATION).parse(JSON_DOCUMENT).read("$.int-max-property");
        int unwrapped = using(JACKSON_JSON_NODE_CONFIGURATION).parse(JSON_DOCUMENT).read("$.int-max-property", int.class);
        assertThat(unwrapped).isEqualTo(Integer.MAX_VALUE);
        assertThat(unwrapped).isEqualTo(node.asInt());
    }

    @Test
    public void longs_are_unwrapped() {
        JsonNode node = using(JACKSON_JSON_NODE_CONFIGURATION).parse(JSON_DOCUMENT).read("$.long-max-property");
        long unwrapped = using(JACKSON_JSON_NODE_CONFIGURATION).parse(JSON_DOCUMENT).read("$.long-max-property", long.class);

        assertThat(unwrapped).isEqualTo(Long.MAX_VALUE);
        assertThat(unwrapped).isEqualTo(node.asLong());
    }


    @Test
    public void list_of_numbers() {
        ArrayNode objs = using(JACKSON_JSON_NODE_CONFIGURATION).parse(JSON_DOCUMENT).read("$.store.book[*].display-price");

        assertThat(objs.get(0).asDouble()).isEqualTo(8.95D);
        assertThat(objs.get(1).asDouble()).isEqualTo(12.99D);
        assertThat(objs.get(2).asDouble()).isEqualTo(8.99D);
        assertThat(objs.get(3).asDouble()).isEqualTo(22.99D);
    }

    @Test
    public void test_type_ref() throws IOException {
        TypeRef<List<FooBarBaz<Gen>>> typeRef = new TypeRef<List<FooBarBaz<Gen>>>() {};

        List<FooBarBaz<Gen>> list = using(JACKSON_JSON_NODE_CONFIGURATION).parse(JSON).read("$", typeRef);

        assertThat(list.get(0).gen.eric).isEqualTo("yepp");
    }

    @Test(expected = MappingException.class)
    public void test_type_ref_fail() throws IOException {
        TypeRef<List<FooBarBaz<Integer>>> typeRef = new TypeRef<List<FooBarBaz<Integer>>>() {};

        using(JACKSON_JSON_NODE_CONFIGURATION).parse(JSON).read("$", typeRef);
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

}
