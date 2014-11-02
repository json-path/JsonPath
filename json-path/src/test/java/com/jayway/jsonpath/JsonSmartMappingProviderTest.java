package com.jayway.jsonpath;

import org.junit.Test;

import java.io.IOException;
import java.util.List;

import static com.jayway.jsonpath.JsonPath.parse;
import static com.jayway.jsonpath.JsonPath.using;
import static org.assertj.core.api.Assertions.assertThat;

public class JsonSmartMappingProviderTest {

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
    public void class_mapping() {

        Object map = Configuration.defaultConfiguration().jsonProvider().createMap();
        Configuration.defaultConfiguration().jsonProvider().setProperty(map, "eric", "eric-val");


        Gen gen = parse(map).read("$", Gen.class);

        assertThat(gen.eric).isEqualTo("eric-val");

    }

    @Test(expected = UnsupportedOperationException.class)
    public void test_type_ref() throws IOException {

        TypeRef<List<FooBarBaz>> typeRef = new TypeRef<List<FooBarBaz>>() {};

        List gen = parse(JSON).read("$", typeRef);
    }


    public static class FooBarBaz {
        public Gen gen;
        public String foo;
        public Long bar;
        public boolean baz;
    }


    public static class Gen {
        public String eric;
    }
}
