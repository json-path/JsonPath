package com.jayway.jsonpath;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.IOException;
import java.util.List;

import static com.jayway.jsonpath.JsonPath.using;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(Parameterized.class)
public class JsonProviderTestObjectMapping extends BaseTest {

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

    private final Configuration conf;

    public JsonProviderTestObjectMapping(Configuration conf) {
        this.conf = conf;
    }

    @Parameterized.Parameters
    public static Iterable<Configuration> configurations() {
        return Configurations.objectMappingConfigurations();
    }

    @Test
    public void list_of_numbers() {

        TypeRef<List<Double>> typeRef = new TypeRef<List<Double>>() {};

        assertThat(using(conf).parse(JSON_DOCUMENT).read("$.store.book[*].display-price", typeRef)).containsExactly(8.95D, 12.99D, 8.99D, 22.99D);
    }

    @Test
    public void test_type_ref() throws IOException {
        TypeRef<List<FooBarBaz<Sub>>> typeRef = new TypeRef<List<FooBarBaz<Sub>>>() {};

        assertThat(using(conf).parse(JSON).read("$", typeRef)).extracting("foo").containsExactly("foo0", "foo1", "foo2");
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
