package com.jayway.jsonpath;

import com.jayway.jsonpath.internal.spi.json.JacksonJsonProvider;
import com.jayway.jsonpath.internal.spi.mapper.JacksonMappingProvider;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class JacksonTest {

    private static Configuration config = Configuration
            .builder()
            .mappingProvider(new JacksonMappingProvider())
            .jsonProvider(new JacksonJsonProvider())
            .build();


    @Test
    public void an_object_can_be_mapped_to_pojo() {

        String json = "{\n" +
                "   \"foo\" : \"foo\",\n" +
                "   \"bar\" : 10,\n" +
                "   \"baz\" : true\n" +
                "}";


        FooBarBaz fooBarBaz = JsonPath.using(config).parse(json).read("$", FooBarBaz.class);

        assertThat(fooBarBaz.foo).isEqualTo("foo");
        assertThat(fooBarBaz.bar).isEqualTo(10L);
        assertThat(fooBarBaz.baz).isEqualTo(true);

    }

    public static class FooBarBaz {
        public String foo;
        public Long bar;
        public boolean baz;
    }

}
