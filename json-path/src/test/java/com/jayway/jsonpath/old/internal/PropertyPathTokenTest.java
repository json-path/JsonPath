package com.jayway.jsonpath.old.internal;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;
import com.jayway.jsonpath.PathNotFoundException;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static org.junit.Assert.assertNull;
import static org.assertj.core.api.Assertions.assertThat;

public class PropertyPathTokenTest {

    private static final Logger logger = LoggerFactory.getLogger(PropertyPathTokenTest.class);

    private String SIMPLE_MAP = "{\n" +
            "   \"foo\" : \"foo-val\",\n" +
            "   \"bar\" : \"bar-val\",\n" +
            "   \"baz\" : {\"baz-child\" : \"baz-child-val\"}\n" +
            "}";

    private String SIMPLE_ARRAY = "[" +
            "{\n" +
            "   \"foo\" : \"foo-val-0\",\n" +
            "   \"bar\" : \"bar-val-0\",\n" +
            "   \"baz\" : {\"baz-child\" : \"baz-child-val\"}\n" +
            "}," +
            "{\n" +
            "   \"foo\" : \"foo-val-1\",\n" +
            "   \"bar\" : \"bar-val-1\",\n" +
            "   \"baz\" : {\"baz-child\" : \"baz-child-val\"}\n" +
            "}" +
            "]";


    @Test
    public void property_not_found() {

        //String result = JsonPath.read(SIMPLE_MAP, "$.not-found");

        //assertThat(result).isNull();

        Configuration configuration = Configuration.builder().options(Option.SUPPRESS_EXCEPTIONS).build();

        String json = "{\"a\":{\"b\":1,\"c\":2}}";
        assertNull(JsonPath.parse(SIMPLE_MAP, configuration).read("$.not-found"));


    }

    @Test(expected = PathNotFoundException.class)
    public void property_not_found_deep() {

        String result = JsonPath.read(SIMPLE_MAP, "$.foo.not-found");

        assertThat(result).isNull();
    }

    @Test(expected = PathNotFoundException.class)
    public void property_not_found_option_throw() {

        //String result = JsonPath.using(Configuration.defaultConfiguration().setOptions(Option.THROW_ON_MISSING_PROPERTY)).parse(SIMPLE_MAP).read("$.not-found");
        String result = JsonPath.using(Configuration.defaultConfiguration()).parse(SIMPLE_MAP).read("$.not-found");

        assertThat(result).isNull();
    }

    @Test
    public void map_value_can_be_read_from_map() {

        String result = JsonPath.read(SIMPLE_MAP, "$.foo");

        assertThat(result).isEqualTo("foo-val");
    }

    @Test
    public void map_value_can_be_read_from_array() {

        List<String> result = JsonPath.read(SIMPLE_ARRAY, "$[*].foo");

        assertThat(result).containsOnly("foo-val-0", "foo-val-1");
    }

    @Test
    public void map_value_can_be_read_from_child_map() {

        String result = JsonPath.read(SIMPLE_MAP, "$.baz.baz-child");

        assertThat(result).isEqualTo("baz-child-val");

    }


}
