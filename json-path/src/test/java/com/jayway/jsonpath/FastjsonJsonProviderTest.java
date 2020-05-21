package com.jayway.jsonpath;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONCreator;
import com.alibaba.fastjson.annotation.JSONField;
import com.jayway.jsonpath.spi.json.FastjsonJsonProvider;
import com.jayway.jsonpath.spi.mapper.FastjsonMappingProvider;
import com.jayway.jsonpath.spi.mapper.MappingException;
import org.junit.Test;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.UUID;

import static com.jayway.jsonpath.JsonPath.using;
import static org.assertj.core.api.Assertions.assertThat;

public class FastjsonJsonProviderTest extends BaseTest {

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
        Object node =  using(FASTJSON_CONFIGURATION).parse(JSON_DOCUMENT).read("$");
        assertThat(((JSONObject)node).get("string-property").toString()).isEqualTo("string-value");
    }

    @Test
    public void strings_are_unwrapped() {
        Object node =  using(FASTJSON_CONFIGURATION).parse(JSON_DOCUMENT).read("$.string-property");
        String unwrapped =  using(FASTJSON_CONFIGURATION).parse(JSON_DOCUMENT).read("$.string-property", String.class);

        assertThat(unwrapped).isEqualTo("string-value");
        assertThat(unwrapped).isEqualTo(node);
    }

    @Test
    public void ints_are_unwrapped() {
        Object node =  using(FASTJSON_CONFIGURATION).parse(JSON_DOCUMENT).read("$.int-max-property");
        int unwrapped =  using(FASTJSON_CONFIGURATION).parse(JSON_DOCUMENT).read("$.int-max-property", int.class);

        assertThat(unwrapped).isEqualTo(Integer.MAX_VALUE);
        assertThat(unwrapped).isEqualTo(node);
    }

    @Test
    public void longs_are_unwrapped() {
        Object node =  using(FASTJSON_CONFIGURATION).parse(JSON_DOCUMENT).read("$.long-max-property");
        long val =  using(FASTJSON_CONFIGURATION).parse(JSON_DOCUMENT).read("$.long-max-property", Long.class);

        assertThat(val).isEqualTo(Long.MAX_VALUE);
        assertThat(val).isEqualTo(node);
    }

    @Test
    public void floats_are_unwrapped() {
        // number end with 'F' will be parsed to Float value, or it will be parsed to BigDecimal
        final String json = "{\"float-property\" : 56.78F}";

        Object node =  using(FASTJSON_CONFIGURATION).parse(json).read("$.float-property");
        Float val =  using(FASTJSON_CONFIGURATION).parse(json).read("$.float-property", Float.class);

        assertThat(val).isEqualTo(56.78F);
        assertThat(val).isEqualTo(node);
    }


    @Test
    public void doubles_are_unwrapped() {
        // number end with 'D' will be parsed to Double value, or it will be parsed to BigDecimal
        final String json = "{\"double-property\" : 56.78D}";

        Object node =  using(FASTJSON_CONFIGURATION).parse(json).read("$.double-property");
        Double val =  using(FASTJSON_CONFIGURATION).parse(json).read("$.double-property", Double.class);

        assertThat(val).isEqualTo(56.78D);
        assertThat(val).isEqualTo(node);
    }

    @Test
    public void bigdecimals_are_unwrapped() {
        final BigDecimal bd = BigDecimal.valueOf(Long.MAX_VALUE).add(BigDecimal.valueOf(10.5));
        final String json = "{\"bd-property\" : " + bd.toString() + "}";

        Object node =  using(FASTJSON_CONFIGURATION).parse(json).read("$.bd-property");
        BigDecimal val =  using(FASTJSON_CONFIGURATION).parse(json).read("$.bd-property", BigDecimal.class);

        assertThat(val).isEqualTo(bd);
        assertThat(val).isEqualTo(node);
    }

    @Test
    public void small_bigdecimals_are_unwrapped() {
        final BigDecimal bd = BigDecimal.valueOf(10.5);
        final String json = "{\"bd-property\" : " + bd.toString() + "}";

        Object node =  using(FASTJSON_CONFIGURATION).parse(json).read("$.bd-property");
        BigDecimal val =  using(FASTJSON_CONFIGURATION).parse(json).read("$.bd-property", BigDecimal.class);

        assertThat(val).isEqualTo(bd);
        assertThat(val).isEqualTo(node);
    }

    @Test
    public void bigintegers_are_unwrapped() {
        final BigInteger bi = BigInteger.valueOf(Long.MAX_VALUE).add(BigInteger.TEN);
        final String json = "{\"bi-property\" : " + bi.toString() + "}";

        Object node =  using(FASTJSON_CONFIGURATION).parse(json).read("$.bi-property");
        BigInteger val =  using(FASTJSON_CONFIGURATION).parse(json).read("$.bi-property", BigInteger.class);

        assertThat(val).isEqualTo(bi);
        assertThat(val).isEqualTo(node);
    }

    @Test
    public void small_bigintegers_are_unwrapped() {
        final BigInteger bi = BigInteger.valueOf(Long.MAX_VALUE);
        final String json = "{\"bi-property\" : " + bi.toString() + "}";

        Object node =  using(FASTJSON_CONFIGURATION).parse(json).read("$.bi-property");
        BigInteger val =  using(FASTJSON_CONFIGURATION).parse(json).read("$.bi-property", BigInteger.class);

        assertThat(val).isEqualTo(bi);
        assertThat(val.longValue()).isEqualTo(node);
    }

    @Test
    public void int_to_long_mapping() {
        assertThat(using(FASTJSON_CONFIGURATION).parse("{\"val\": 1}").read("val", Long.class)).isEqualTo(1L);
    }

    @Test
    public void an_Integer_can_be_converted_to_a_Double() {
        assertThat(using(FASTJSON_CONFIGURATION).parse("{\"val\": 1}").read("val", Double.class)).isEqualTo(1D);
    }

    @Test
    public void list_of_numbers() {
        JSONArray objs = using(FASTJSON_CONFIGURATION).parse(JSON_DOCUMENT).read("$.store.book[*].display-price");

        assertThat(objs.iterator()).extractingResultOf("doubleValue").containsExactly(8.95D, 12.99D, 8.99D, 22.99D);
    }

    @Test
    public void an_object_can_be_mapped_to_pojo() {

        String json = "{\n" +
                "   \"foo\" : \"foo\",\n" +
                "   \"bar\" : 10,\n" +
                "   \"baz\" : true\n" +
                "}";

        TestClazz testClazz = JsonPath.using(FASTJSON_CONFIGURATION).parse(json).read("$", TestClazz.class);

        assertThat(testClazz.foo).isEqualTo("foo");
        assertThat(testClazz.bar).isEqualTo(10L);
        assertThat(testClazz.baz).isEqualTo(true);

    }

    @Test
    public void test_type_ref() {
        TypeRef<List<FooBarBaz<Gen>>> typeRef = new TypeRef<List<FooBarBaz<Gen>>>() {};

        List<FooBarBaz<Gen>> list = using(FASTJSON_CONFIGURATION).parse(JSON).read("$", typeRef);

        assertThat(list.get(0).gen.eric).isEqualTo("yepp");
    }

    @Test(expected = MappingException.class)
    public void test_type_ref_fail() {
        TypeRef<List<FooBarBaz<Integer>>> typeRef = new TypeRef<List<FooBarBaz<Integer>>>() {};

        using(FASTJSON_CONFIGURATION).parse(JSON).read("$", typeRef);
    }
    
    @Test
    // https://github.com/json-path/JsonPath/issues/351
    public void no_error_when_mapping_null() {
      
      Configuration configuration = Configuration
          .builder()
          .mappingProvider(new FastjsonMappingProvider())
          .jsonProvider(new FastjsonJsonProvider())
          .options(Option.DEFAULT_PATH_LEAF_TO_NULL, Option.SUPPRESS_EXCEPTIONS)
          .build();
      
      String json = "{\"M\":[]}";

      String result = JsonPath.using(configuration).parse(json).read("$.M[0].A[0]", String.class);

      assertThat(result).isNull();
    }

    @Test
    public void setPropertyWithPOJO() {
        DocumentContext context = JsonPath.using(FASTJSON_CONFIGURATION).parse("{}");
        UUID uuid = UUID.randomUUID();
        context.put("$", "data", new Data(uuid));
        String id = context.read("$.data.id", String.class);
        assertThat(id).isEqualTo(uuid.toString());
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

    public static final class Data {
        @JSONField(name = "id")
        UUID id;

        @JSONCreator
        Data(@JSONField(name = "id") final UUID id) {
            this.id = id;
        }

        public UUID getId() {
            return id;
        }
    }
}
