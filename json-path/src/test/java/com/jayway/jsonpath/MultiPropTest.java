package com.jayway.jsonpath;

import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.jayway.jsonpath.JsonPath.using;
import static com.jayway.jsonpath.TestUtils.assertEvaluationThrows;
import static org.assertj.core.api.Assertions.assertThat;

public class MultiPropTest {

    @Test
    public void multi_prop_can_be_read_from_root() {

        Map<String, Object> model = new HashMap<String, Object>(){{
            put("a", "a-val");
            put("b", "b-val");
            put("c", "c-val");
        }};

        Configuration conf = Configuration.defaultConfiguration();

        assertThat(using(conf).parse(model).read("$['a', 'b']", Map.class))
                .containsEntry("a", "a-val")
                .containsEntry("b", "b-val");

        // current semantics: absent props are skipped
        assertThat(using(conf).parse(model).read("$['a', 'd']", Map.class))
                .hasSize(1).containsEntry("a", "a-val");
    }

    @Test
    public void multi_props_can_be_defaulted_to_null() {

        Map<String, Object> model = new HashMap<String, Object>(){{
            put("a", "a-val");
            put("b", "b-val");
            put("c", "c-val");
        }};

        Configuration conf = Configuration.defaultConfiguration().addOptions(Option.DEFAULT_PATH_LEAF_TO_NULL);

        assertThat(using(conf).parse(model).read("$['a', 'd']", Map.class))
                .containsEntry("a", "a-val")
                .containsEntry("d", null);
    }

    @Test(expected = PathNotFoundException.class)
    public void multi_props_can_be_required() {

        Map<String, Object> model = new HashMap<String, Object>(){{
            put("a", "a-val");
            put("b", "b-val");
            put("c", "c-val");
        }};

        Configuration conf = Configuration.defaultConfiguration().addOptions(Option.REQUIRE_PROPERTIES);

        using(conf).parse(model).read("$['a', 'x']", Map.class);
    }

    @Test
    public void multi_props_can_be_non_leafs() {
        Object result = JsonPath.parse("{\"a\": {\"v\": 5}, \"b\": {\"v\": 4}, \"c\": {\"v\": 1}}").read(
                "$['a', 'c'].v");
        assertThat(result).asList().containsOnly(5, 1);
    }

    @Test
    public void nonexistent_non_leaf_multi_props_ignored() {
        Object result = JsonPath.parse("{\"a\": {\"v\": 5}, \"b\": {\"v\": 4}, \"c\": {\"v\": 1}}").read(
                "$['d', 'a', 'c', 'm'].v");
        assertThat(result).asList().containsOnly(5, 1);
    }

    @Test
    public void multi_props_with_post_filter() {
        Object result = JsonPath.parse("{\"a\": {\"v\": 5}, \"b\": {\"v\": 4}, \"c\": {\"v\": 1, \"flag\": true}}").read(
                "$['a', 'c'][?(@.flag)].v");
        assertThat(result).asList().containsOnly(1);
    }

    @Test
    public void deep_scan_does_not_affect_non_leaf_multi_props() {
        // deep scan + multiprop is quite redundant scenario, but it's not forbidden, so we'd better check
        final String json = "{\"v\": [[{}, 1, {\"a\": {\"v\": 5}, \"b\": {\"v\": 4}, \"c\": {\"v\": 1, \"flag\": true}}]]}";
        Object result = JsonPath.parse(json).read("$..['a', 'c'].v");
        assertThat(result).asList().containsOnly(5, 1);

        result = JsonPath.parse(json).read("$..['a', 'c'][?(@.flag)].v");
        assertThat(result).asList().containsOnly(1);
    }

    @Test
    public void multi_props_can_be_in_the_middle() {
        final String json = "{\"x\": [null, {\"a\": {\"v\": 5}, \"b\": {\"v\": 4}, \"c\": {\"v\": 1}}]}";
        Object result = JsonPath.parse(json).read("$.x[1]['a', 'c'].v");
        assertThat(result).asList().containsOnly(5, 1);
        result = JsonPath.parse(json).read("$.x[*]['a', 'c'].v");
        assertThat(result).asList().containsOnly(5, 1);
        result = JsonPath.parse(json).read("$[*][*]['a', 'c'].v");
        assertThat(result).asList().containsOnly(5, 1);

        result = JsonPath.parse(json).read("$.x[1]['d', 'a', 'c', 'm'].v");
        assertThat(result).asList().containsOnly(5, 1);
        result = JsonPath.parse(json).read("$.x[*]['d', 'a', 'c', 'm'].v");
        assertThat(result).asList().containsOnly(5, 1);
    }

    @Test
    public void non_leaf_multi_props_can_be_required() {
        final Configuration conf = Configuration.defaultConfiguration().addOptions(Option.REQUIRE_PROPERTIES);
        final String json = "{\"a\": {\"v\": 5}, \"b\": {\"v\": 4}, \"c\": {\"v\": 1}}";

        assertThat((List)using(conf).parse(json).read("$['a', 'c'].v")).asList().containsOnly(5, 1);
        assertEvaluationThrows(json, "$['d', 'a', 'c', 'm'].v", PathNotFoundException.class, conf);
    }
}
