package com.jayway.jsonpath;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.jayway.jsonpath.spi.json.JacksonJsonNodeJsonProvider;
import com.jayway.jsonpath.spi.mapper.JacksonMappingProvider;
import com.jayway.jsonpath.spi.mapper.MappingException;
import org.junit.Test;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.UUID;

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

    ObjectMapper objectMapperDecimal = new ObjectMapper().configure(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS, true);
    Configuration JACKSON_JSON_NODE_CONFIGURATION_DECIMAL = Configuration
            .builder()
            .mappingProvider(new JacksonMappingProvider())
            .jsonProvider(new JacksonJsonNodeJsonProvider(objectMapperDecimal))
            .build();

    @Test
    public void bigdecimals_are_unwrapped() {
        final BigDecimal bd = BigDecimal.valueOf(Long.MAX_VALUE).add(BigDecimal.valueOf(10.5));
        final String json = "{\"bd-property\" : " + bd.toString() + "}";

        JsonNode node =  using(JACKSON_JSON_NODE_CONFIGURATION_DECIMAL).parse(json).read("$.bd-property");
        BigDecimal val =  using(JACKSON_JSON_NODE_CONFIGURATION_DECIMAL).parse(json).read("$.bd-property", BigDecimal.class);

        assertThat(node.isBigDecimal()).isTrue();
        assertThat(val).isEqualTo(bd);
        assertThat(val).isEqualTo(node.decimalValue());
    }

    @Test
    public void small_bigdecimals_are_unwrapped() {
        final BigDecimal bd = BigDecimal.valueOf(10.5);
        final String json = "{\"bd-property\" : " + bd.toString() + "}";

        JsonNode node =  using(JACKSON_JSON_NODE_CONFIGURATION_DECIMAL).parse(json).read("$.bd-property");
        BigDecimal val =  using(JACKSON_JSON_NODE_CONFIGURATION_DECIMAL).parse(json).read("$.bd-property", BigDecimal.class);

        assertThat(node.isBigDecimal()).isTrue();
        assertThat(val).isEqualTo(bd);
        assertThat(val).isEqualTo(node.decimalValue());
    }

    ObjectMapper objectMapperBigInteger = new ObjectMapper().configure(DeserializationFeature.USE_BIG_INTEGER_FOR_INTS, true);
    Configuration JACKSON_JSON_NODE_CONFIGURATION_Big_Integer = Configuration
            .builder()
            .mappingProvider(new JacksonMappingProvider())
            .jsonProvider(new JacksonJsonNodeJsonProvider(objectMapperBigInteger))
            .build();

    @Test
    public void bigintegers_are_unwrapped() {
        final BigInteger bi = BigInteger.valueOf(Long.MAX_VALUE).add(BigInteger.TEN);
        final String json = "{\"bi-property\" : " + bi.toString() + "}";

        JsonNode node =  using(JACKSON_JSON_NODE_CONFIGURATION_Big_Integer).parse(json).read("$.bi-property");
        BigInteger val =  using(JACKSON_JSON_NODE_CONFIGURATION_Big_Integer).parse(json).read("$.bi-property", BigInteger.class);

        assertThat(node.isBigInteger()).isTrue();
        assertThat(val).isEqualTo(bi);
        assertThat(val).isEqualTo(node.bigIntegerValue());
    }

    @Test
    public void small_bigintegers_are_unwrapped() {
        final BigInteger bi = BigInteger.valueOf(Long.MAX_VALUE);
        final String json = "{\"bi-property\" : " + bi.toString() + "}";

        JsonNode node =  using(JACKSON_JSON_NODE_CONFIGURATION_Big_Integer).parse(json).read("$.bi-property");
        BigInteger val =  using(JACKSON_JSON_NODE_CONFIGURATION_Big_Integer).parse(json).read("$.bi-property", BigInteger.class);

        assertThat(node.isBigInteger()).isTrue();
        assertThat(val).isEqualTo(bi);
        assertThat(val).isEqualTo(node.bigIntegerValue());
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

    @Test
    public void mapPropertyWithPOJO() {
        String someJson = "" +
                "{\n" +
                "  \"a\": \"a\",\n" +
                "  \"b\": \"b\"\n" +
                "}";
        ObjectMapper om = new ObjectMapper();
        om.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        Configuration c = Configuration
                .builder()
                .mappingProvider(new JacksonMappingProvider())
                .jsonProvider(new JacksonJsonNodeJsonProvider(om))
                .build();
        DocumentContext context = JsonPath.using(c).parse(someJson);
        String someJsonStr = context.jsonString();
        DocumentContext altered = context.map("$['a', 'b', 'c']", new MapFunction() {
            @Override
            public Object map(Object currentValue, Configuration configuration) {
                return currentValue;
            }
        });
        assertThat(altered.jsonString()).isEqualTo(someJsonStr);
    }

    @Test
    // https://github.com/json-path/JsonPath/issues/364
    public void setPropertyWithPOJO() {
      DocumentContext context = JsonPath.using(JACKSON_JSON_NODE_CONFIGURATION).parse("{}");
      UUID uuid = UUID.randomUUID();
      context.put("$", "data", new Data(uuid));
      String id = context.read("$.data.id", String.class);
      assertThat(id).isEqualTo(uuid.toString());
    }
    // https://github.com/json-path/JsonPath/issues/366
    public void empty_array_check_works() throws IOException {
      String json = "[" +
          "  {" +
          "    \"name\": \"a\"," +
          "    \"groups\": [{" +
          "      \"type\": \"phase\"," +
          "      \"name\": \"alpha\"" +
          "    }, {" +
          "      \"type\": \"not_phase\"," +
          "      \"name\": \"beta\"" +
          "    }]" +
          "  }, {" +
          "    \"name\": \"b\"," +
          "    \"groups\": [{" +
          "      \"type\": \"phase\"," +
          "      \"name\": \"beta\"" +
          "    }, {" +
          "      \"type\": \"not_phase\"," +
          "      \"name\": \"alpha\"" +
          "    }]" +
          "  }" +
          "]";
      ArrayNode node = using(JACKSON_JSON_NODE_CONFIGURATION).parse(json).read("$[?(@.groups[?(@.type == 'phase' && @.name == 'alpha')] empty false)]");
      assertThat(node.size()).isEqualTo(1);
      assertThat(node.get(0).get("name").asText()).isEqualTo("a");
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

    public static final class Data {
      @JsonProperty("id")
      UUID id;

      @JsonCreator
      Data(@JsonProperty("id") final UUID id) {
        this.id = id;
      }
    }

}
