package com.jayway.jsonpath;

import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.jayway.jsonpath.JsonPath.using;
import static com.jayway.jsonpath.Option.*;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonMap;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

public class OptionsTest extends BaseTest {

    @Test(expected = PathNotFoundException.class)
    public void a_leafs_is_not_defaulted_to_null() {

        Configuration conf = Configuration.defaultConfiguration();

        assertThat(using(conf).parse("{\"foo\" : \"bar\"}").read("$.baz")).isNull();
    }

    @Test
    public void a_leafs_can_be_defaulted_to_null() {

        Configuration conf = Configuration.builder().options(DEFAULT_PATH_LEAF_TO_NULL).build();

        assertThat(using(conf).parse("{\"foo\" : \"bar\"}").read("$.baz", Object.class)).isNull();
    }

    @Test
    public void a_definite_path_is_not_returned_as_list_by_default() {

        Configuration conf = Configuration.defaultConfiguration();

        assertThat(using(conf).parse("{\"foo\" : \"bar\"}").read("$.foo")).isInstanceOf(String.class);
    }

    @Test
    public void a_definite_path_can_be_returned_as_list() {

        Configuration conf = Configuration.builder().options(ALWAYS_RETURN_LIST).build();

        assertThat(using(conf).parse("{\"foo\" : \"bar\"}").read("$.foo")).isInstanceOf(List.class);

        assertThat(using(conf).parse("{\"foo\": null}").read("$.foo")).isInstanceOf(List.class);
        List<Object> result = using(conf).parse("{\"bar\": {\"foo\": null}}").read("$..foo");
        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isNull();

        assertThat(using(conf).parse("{\"foo\": [1, 4, 8]}").read("$.foo")).asList()
                .containsExactly(Arrays.asList(1, 4, 8));
        assertThat(using(conf).parse("{\"bar\": {\"foo\": [1, 4, 8]}}").read("$..foo")).asList()
                .containsExactly(Arrays.asList(1, 4, 8));
    }

    @Test
    public void a_path_evaluation_is_returned_as_VALUE_by_default() {
        Configuration conf = Configuration.defaultConfiguration();

        assertThat(using(conf).parse("{\"foo\" : \"bar\"}").read("$.foo")).isEqualTo("bar");
    }

    @Test
    public void a_path_evaluation_can_be_returned_as_PATH_LIST() {
        Configuration conf = Configuration.builder().options(AS_PATH_LIST).build();

        List<String> pathList = using(conf).parse("{\"foo\" : \"bar\"}").read("$.foo");

        assertThat(pathList).containsOnly("$['foo']");
    }

    @Test
    public void multi_properties_are_merged_by_default() {

        Map<String, Object> model = new HashMap<String, Object>();
        model.put("a", "a");
        model.put("b", "b");
        model.put("c", "c");

        Configuration conf = Configuration.defaultConfiguration();

        Map<String, Object> result = using(conf).parse(model).read("$.['a', 'b']");

        //assertThat(result).isInstanceOf(List.class);
        //assertThat((List)result).containsOnly("a", "b");

        assertThat(result)
                .containsEntry("a", "a")
                .containsEntry("b", "b");
    }

    @Test
    public void when_property_is_required_exception_is_thrown() {
        List<Map<String, String>> model = asList(singletonMap("a", "a-val"),singletonMap("b", "b-val"));

        Configuration conf = Configuration.defaultConfiguration();

        assertThat(using(conf).parse(model).read("$[*].a", List.class)).containsExactly("a-val");


        conf = conf.addOptions(Option.REQUIRE_PROPERTIES);

        try{
            using(conf).parse(model).read("$[*].a", List.class);
            fail("Should throw PathNotFoundException");
        } catch (PathNotFoundException pnf){}
    }

    @Test
    public void when_property_is_required_exception_is_thrown_2() {
        Map<String, Object> model = new HashMap<String, Object>();
        model.put("a", singletonMap("a-key", "a-val"));
        model.put("b", singletonMap("b-key", "b-val"));

        Configuration conf = Configuration.defaultConfiguration();

        assertThat(using(conf).parse(model).read("$.*.a-key", List.class)).containsExactly("a-val");


        conf = conf.addOptions(Option.REQUIRE_PROPERTIES);

        try{
            using(conf).parse(model).read("$.*.a-key", List.class);
            fail("Should throw PathNotFoundException");
        } catch (PathNotFoundException pnf){}
    }

}
