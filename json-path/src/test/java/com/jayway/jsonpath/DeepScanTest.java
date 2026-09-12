package com.jayway.jsonpath;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.jayway.jsonpath.JsonPath.parse;
import static com.jayway.jsonpath.JsonPath.using;
import static com.jayway.jsonpath.TestUtils.assertEvaluationThrows;
import static java.util.Collections.singletonMap;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Deep scan is indefinite, so certain "illegal" actions become a no-op instead of a path evaluation exception.
 */
public class DeepScanTest extends BaseTest {

    @Test
    public void when_deep_scanning_non_array_subscription_is_ignored() {
        Object result = JsonPath.parse("{\"x\": [0,1,[0,1,2,3,null],null]}").read("$..[2][3]");
        assertThat(result).asList().containsOnly(3);
        result = JsonPath.parse("{\"x\": [0,1,[0,1,2,3,null],null], \"y\": [0,1,2]}").read("$..[2][3]");
        assertThat(result).asList().containsOnly(3);

        result = JsonPath.parse("{\"x\": [0,1,[0,1,2],null], \"y\": [0,1,2]}").read("$..[2][3]");
        assertThat(result).asList().isEmpty();
    }

    @Test
    public void when_deep_scanning_null_subscription_is_ignored() {
        Object result = JsonPath.parse("{\"x\": [null,null,[0,1,2,3,null],null]}").read("$..[2][3]");
        assertThat(result).asList().containsOnly(3);
        result = JsonPath.parse("{\"x\": [null,null,[0,1,2,3,null],null], \"y\": [0,1,null]}").read("$..[2][3]");
        assertThat(result).asList().containsOnly(3);
    }

    @Test
    public void when_deep_scanning_array_index_oob_is_ignored() {
        Object result = JsonPath.parse("{\"x\": [0,1,[0,1,2,3,10],null]}").read("$..[4]");
        assertThat(result).asList().containsOnly(10);

        result = JsonPath.parse("{\"x\": [null,null,[0,1,2,3]], \"y\": [null,null,[0,1]]}").read("$..[2][3]");
        assertThat(result).asList().containsOnly(3);
    }

    @Test
    public void definite_upstream_illegal_array_access_throws() {
        assertEvaluationThrows("{\"foo\": {\"bar\": null}}", "$.foo.bar.[5]", PathNotFoundException.class);
        assertEvaluationThrows("{\"foo\": {\"bar\": null}}", "$.foo.bar.[5, 10]", PathNotFoundException.class);

        assertEvaluationThrows("{\"foo\": {\"bar\": 4}}", "$.foo.bar.[5]", PathNotFoundException.class);
        assertEvaluationThrows("{\"foo\": {\"bar\": 4}}", "$.foo.bar.[5, 10]", PathNotFoundException.class);

        assertEvaluationThrows("{\"foo\": {\"bar\": []}}", "$.foo.bar.[5]", PathNotFoundException.class);
    }

    @Test
    public void when_deep_scanning_illegal_property_access_is_ignored() {
        Object result = JsonPath.parse("{\"x\": {\"foo\": {\"bar\": 4}}, \"y\": {\"foo\": 1}}").read("$..foo");
        assertThat(result).asList().hasSize(2);

        result = JsonPath.parse("{\"x\": {\"foo\": {\"bar\": 4}}, \"y\": {\"foo\": 1}}").read("$..foo.bar");
        assertThat(result).asList().containsOnly(4);
        result = JsonPath.parse("{\"x\": {\"foo\": {\"bar\": 4}}, \"y\": {\"foo\": 1}}").read("$..[*].foo.bar");
        assertThat(result).asList().containsOnly(4);
        result = JsonPath.parse("{\"x\": {\"foo\": {\"baz\": 4}}, \"y\": {\"foo\": 1}}").read("$..[*].foo.bar");
        assertThat(result).asList().isEmpty();
    }

    @Test
    public void when_deep_scanning_illegal_predicate_is_ignored() {
        Object result = JsonPath.parse("{\"x\": {\"foo\": {\"bar\": 4}}, \"y\": {\"foo\": 1}}").read(
                "$..foo[?(@.bar)].bar");
        assertThat(result).asList().containsOnly(4);

        result = JsonPath.parse("{\"x\": {\"foo\": {\"bar\": 4}}, \"y\": {\"foo\": 1}}").read(
                "$..[*]foo[?(@.bar)].bar");
        assertThat(result).asList().containsOnly(4);
    }

    @Test
    public void when_deep_scanning_require_properties_is_ignored_on_scan_target() {
        final Configuration conf = Configuration.defaultConfiguration().addOptions(Option.REQUIRE_PROPERTIES);

        Object result = JsonPath.parse("[{\"x\": {\"foo\": {\"x\": 4}, \"x\": null}, \"y\": {\"x\": 1}}, {\"x\": []}]").read(
                "$..x");
        assertThat(result).asList().hasSize(5);


        List<Integer> result1 = JsonPath.using(conf).parse("{\"foo\": {\"bar\": 4}}").read("$..foo.bar");
        assertThat(result1).containsExactly(4);

        assertEvaluationThrows("{\"foo\": {\"baz\": 4}}", "$..foo.bar", PathNotFoundException.class, conf);
    }

    @Test
    public void when_deep_scanning_require_properties_is_ignored_on_scan_target_but_not_on_children() {
        final Configuration conf = Configuration.defaultConfiguration().addOptions(Option.REQUIRE_PROPERTIES);

        assertEvaluationThrows("{\"foo\": {\"baz\": 4}}", "$..foo.bar", PathNotFoundException.class, conf);
    }

    @Test
    public void when_deep_scanning_leaf_multi_props_work() {
        Object result = JsonPath.parse("[{\"a\": \"a-val\", \"b\": \"b-val\", \"c\": \"c-val\"}, [1, 5], {\"a\": \"a-val\"}]").read(
                "$..['a', 'c']");
        // This is current deep scan semantics: only objects containing all properties specified in multiprops token are
        // considered.
        assertThat(result).asList().hasSize(1);
        result = ((List)result).get(0);

        assertThat(result).isInstanceOf(Map.class);
        assertThat((Map)result).hasSize(2).containsEntry("a", "a-val").containsEntry("c", "c-val");

        // But this semantics changes when DEFAULT_PATH_LEAF_TO_NULL comes into play.
        Configuration conf = Configuration.defaultConfiguration().addOptions(Option.DEFAULT_PATH_LEAF_TO_NULL);
        result = using(conf).parse("[{\"a\": \"a-val\", \"b\": \"b-val\", \"c\": \"c-val\"}, [1, 5], {\"a\": \"a-val\"}]").read(
                "$..['a', 'c']");
        // todo: deep equality test, but not tied to any json provider
        assertThat(result).asList().hasSize(2);
        for (final Object node : (List)result) {
            assertThat(node).isInstanceOf(Map.class);
            assertThat((Map)node).hasSize(2).containsEntry("a", "a-val");
        }
    }

    @Test
    public void when_deep_scanning_wildcard_before_leaf_property_works() {
        Object result = JsonPath.parse(JSON_DOCUMENT).read("$..*.title");
        assertThat(result).asList().containsExactly(
                "Sayings of the Century", "Sword of Honour", "Moby Dick", "The Lord of the Rings");
    }

    @Test
    public void when_deep_scanning_single_array_index_before_leaf_property_works() {
        Object result = JsonPath.parse(JSON_DOCUMENT).read("$..[2].author");
        assertThat(result).asList().containsExactly("Herman Melville");
    }

    @Test
    public void when_deep_scanning_multiple_array_indexes_before_leaf_property_works() {
        Object result = JsonPath.parse(JSON_DOCUMENT).read("$..[0,2].author");
        assertThat(result).asList().containsExactly("Nigel Rees", "Herman Melville");
    }

    @Test
    public void when_deep_scanning_function_parameter_with_scan_wildcard_does_not_throw() {
        // $..* feeds Length through a wildcard that is mounted on the parameter
        // path without a previous link; the leaf filter must not dereference it
        // the function mounts its wildcard on the parameter path without a
        // previous link, so the leaf filter must not dereference it; the
        // values match the completed deep scan results after this fix
        Object result = JsonPath.parse(JSON_DOCUMENT).read("$..*.length()");
        assertThat(result).isEqualTo(31);

        result = JsonPath.parse(JSON_DOCUMENT).read("$..[0].length()");
        assertThat(result).isEqualTo(18);

        result = JsonPath.parse(JSON_DOCUMENT).read("$..[*].length()");
        assertThat(result).isEqualTo(31);
    }

    @Test
    public void when_deep_scanning_double_wildcard_after_fix() {
        // the same fix family: a wildcard fed by a deep scan used to drop
        // every result, so the second wildcard received nothing
        Object result = JsonPath.parse(JSON_DOCUMENT).read("$..*.*");
        assertThat(result).asList().hasSize(31);
    }

    @Test
    public void when_deep_scanning_slice_before_leaf_property_accepts_all_elements() {
        // documented boundary: a slice upstream accepts every fed element, the
        // exact [from,to) range would need the array length at the leaf
        Object result = JsonPath.parse(JSON_DOCUMENT).read("$..[1:3].author");
        assertThat(result).asList().containsExactly(
                "Nigel Rees", "Evelyn Waugh", "Herman Melville", "J. R. R. Tolkien");
    }

    @Test
    public void when_deep_scanning_negative_index_before_leaf_property_returns_empty() {
        // pre-existing: negative indexes never match the fed element indexes
        Object result = JsonPath.parse(JSON_DOCUMENT).read("$..[-1].author");
        assertThat(result).asList().isEmpty();
    }

    @Test
    public void require_single_property_ok() {

        List json = new ArrayList() {{
            add(singletonMap("a", "a0"));
            add(singletonMap("a", "a1"));
        }};

        Configuration configuration = JSON_SMART_CONFIGURATION.addOptions(Option.REQUIRE_PROPERTIES);

        Object result = JsonPath.using(configuration).parse(json).read("$..a");

        assertThat(result).asList().containsExactly("a0","a1");
    }

    @Test
    public void require_single_property() {

        List json = new ArrayList() {{
            add(singletonMap("a", "a0"));
            add(singletonMap("b", "b2"));
        }};

        Configuration configuration = JSON_SMART_CONFIGURATION.addOptions(Option.REQUIRE_PROPERTIES);

        Object result = JsonPath.using(configuration).parse(json).read("$..a");

        assertThat(result).asList().containsExactly("a0");
    }

    @Test
    public void require_multi_property_all_match() {

        final Map ab = new HashMap(){{
            put("a", "aa");
            put("b", "bb");
        }};

        List json = new ArrayList() {{
            add(ab);
            add(ab);
        }};

        Configuration configuration = JSON_SMART_CONFIGURATION.addOptions(Option.REQUIRE_PROPERTIES);

        List<Map<String, String>> result = JsonPath.using(configuration).parse(json).read("$..['a', 'b']");

        assertThat(result).containsExactly(ab, ab);
    }

    @Test
    public void require_multi_property_some_match() {

        final Map ab = new HashMap(){{
            put("a", "aa");
            put("b", "bb");
        }};

        final Map ad = new HashMap(){{
            put("a", "aa");
            put("d", "dd");
        }};

        List json = new ArrayList() {{
            add(ab);
            add(ad);
        }};

        Configuration configuration = JSON_SMART_CONFIGURATION.addOptions(Option.REQUIRE_PROPERTIES);

        List<Map<String, String>>  result = JsonPath.using(configuration).parse(json).read("$..['a', 'b']");

        assertThat(result).containsExactly(ab);
    }

    @Test
    public void scan_for_single_property() {
        final Map a = new HashMap(){{
            put("a", "aa");
        }};
        final Map b = new HashMap(){{
            put("b", "bb");
        }};
        final Map ab = new HashMap(){{
            put("a", a);
            put("b", b);
        }};
        final Map b_ab = new HashMap(){{
            put("b", b);
            put("ab", ab);
        }};
        List json = new ArrayList() {{
            add(a);
            add(b);
            add(b_ab);
        }};
        assertThat(parse(json).read("$..['a']", List.class)).containsExactly("aa", a, "aa");
    }

    @Test
    public void scan_for_property_path() {
        final Map a = new HashMap(){{
            put("a", "aa");
        }};
        final Map x = new HashMap(){{
            put("x", "xx");
        }};
        final Map y = new HashMap(){{
            put("a", x);
        }};
        final Map z = new HashMap(){{
            put("z", y);
        }};
        List json = new ArrayList() {{
            add(a);
            add(x);
            add(y);
            add(z);
        }};
        assertThat(parse(json).read("$..['a'].x", List.class)).containsExactly("xx", "xx");
    }

    @Test
    public void scan_for_property_path_missing_required_property() {
        final Map a = new HashMap(){{
            put("a", "aa");
        }};
        final Map x = new HashMap(){{
            put("x", "xx");
        }};
        final Map y = new HashMap(){{
            put("a", x);
        }};
        final Map z = new HashMap(){{
            put("z", y);
        }};
        List json = new ArrayList() {{
            add(a);
            add(x);
            add(y);
            add(z);
        }};
        assertThat(using(JSON_SMART_CONFIGURATION.addOptions(Option.REQUIRE_PROPERTIES)).parse(json).read("$..['a'].x", List.class)).containsExactly("xx", "xx");
    }


    @Test
    public void scans_can_be_filtered() {

        final Map brown = singletonMap("val", "brown");
        final Map white = singletonMap("val", "white");

        final Map cow = new HashMap(){{
            put("mammal", true);
            put("color", brown);
        }};
        final Map dog = new HashMap(){{
            put("mammal", true);
            put("color", white);
        }};
        final Map frog = new HashMap(){{
            put("mammal", false);
        }};
        List animals = new ArrayList() {{
            add(cow);
            add(dog);
            add(frog);
        }};
        assertThat(using(JSON_SMART_CONFIGURATION.addOptions(Option.REQUIRE_PROPERTIES)).parse(animals).read("$..[?(@.mammal == true)].color", List.class)).containsExactly(brown, white);
    }

    @Test
    public void scan_with_a_function_filter() {
        List result = JsonPath.parse(JSON_DOCUMENT).read("$..*[?(@.length() > 5)]");
        assertThat(result).hasSize(1);
    }

    @Test
    public void deepScanPathDefault() {
        executeScanPath();
    }

    @Test
    public void deepScanPathRequireProperties() {
        executeScanPath(Option.REQUIRE_PROPERTIES);
    }

    private void executeScanPath(Option... options) {
        String json = "{'index': 'index', 'data': {'array': [{ 'object1': { 'name': 'robert'} }]}}";
        Map<String, Object> expected = new HashMap<String, Object>() {{
            put("object1", new HashMap<String, String>() {{
                put("name", "robert");
            }});
        }};

        Configuration configuration = Configuration
                .builder()
                .options(options)
                .build();

        List<Map<String, Object>> result = JsonPath
                .using(configuration)
                .parse(json)
                .read("$..array[0]");
        assertThat(result.get(0)).isEqualTo(expected);
    }
}
