package com.jayway.jsonpath;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.util.List;

import static com.jayway.jsonpath.JsonPath.using;
import static org.assertj.core.api.Assertions.assertThat;

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

    public static Iterable<Configuration> configurations() {
        return Configurations.objectMappingConfigurations();
    }


    @ParameterizedTest
    @MethodSource("configurations")
    public void list_of_numbers(Configuration conf) {

        TypeRef<List<Double>> typeRef = new TypeRef<List<Double>>() {
        };

        assertThat(using(conf).parse(JSON_DOCUMENT).read("$.store.book[*].display-price", typeRef)).containsExactly(8.95D, 12.99D, 8.99D, 22.99D);
    }


    @ParameterizedTest
    @MethodSource("configurations")
    public void test_type_ref(Configuration conf) throws IOException {
        TypeRef<List<FooBarBaz<Sub>>> typeRef = new TypeRef<List<FooBarBaz<Sub>>>() {
        };

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
