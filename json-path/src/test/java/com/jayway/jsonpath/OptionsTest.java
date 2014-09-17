package com.jayway.jsonpath;

import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.jayway.jsonpath.JsonPath.using;
import static com.jayway.jsonpath.Option.ALWAYS_RETURN_LIST;
import static com.jayway.jsonpath.Option.AS_PATH_LIST;
import static com.jayway.jsonpath.Option.DEFAULT_PATH_LEAF_TO_NULL;
import static java.util.Collections.singletonMap;
import static org.assertj.core.api.Assertions.assertThat;

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
    public void multi_properties_are_not_merged_by_default() {

        Map<String, Object> model = new HashMap<String, Object>();
        model.put("a", "a");
        model.put("b", "b");
        model.put("c", "c");

        Configuration conf = Configuration.defaultConfiguration();

        Object result = using(conf).parse(model).read("$.['a', 'b']");

        assertThat(result).isInstanceOf(List.class);
        assertThat((List)result).containsOnly("a", "b");
    }

}
