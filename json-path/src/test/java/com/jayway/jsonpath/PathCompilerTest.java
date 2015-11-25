package com.jayway.jsonpath;

import org.junit.Ignore;
import org.junit.Test;

import java.util.List;

import static com.jayway.jsonpath.internal.path.PathCompiler.compile;
import static org.assertj.core.api.Assertions.assertThat;

public class PathCompilerTest {


    @Ignore("Backward compatibility <= 2.0.0")
    @Test(expected = InvalidPathException.class)
    public void a_path_must_start_with_$_or_at() {
        compile("x");
    }

    @Ignore("Backward compatibility <= 2.0.0")
    @Test(expected = InvalidPathException.class)
    public void a_square_bracket_may_not_follow_a_period() {
        compile("$.[");
    }

    @Test(expected = InvalidPathException.class)
    public void a_root_path_must_be_followed_by_period_or_bracket() {
        compile("$X");
    }

    @Test
    public void a_root_path_can_be_compiled() {
        assertThat(compile("$").toString()).isEqualTo("$");
        assertThat(compile("@").toString()).isEqualTo("@");
    }

    @Test(expected = InvalidPathException.class)
    public void a_path_may_not_end_with_period() {
        assertThat(compile("$.").toString());
        assertThat(compile("$.prop.").toString());
    }

    @Test(expected = InvalidPathException.class)
    public void a_path_may_not_end_with_scan() {
        assertThat(compile("$..").toString());
        assertThat(compile("$.prop..").toString());
    }

    @Test
    public void a_property_token_can_be_compiled() {
        assertThat(compile("$.prop").toString()).isEqualTo("$['prop']");
        assertThat(compile("$.1prop").toString()).isEqualTo("$['1prop']");
        assertThat(compile("$.@prop").toString()).isEqualTo("$['@prop']");
    }

    @Test
    public void a_bracket_notation_property_token_can_be_compiled() {
        assertThat(compile("$['prop']").toString()).isEqualTo("$['prop']");
        assertThat(compile("$['1prop']").toString()).isEqualTo("$['1prop']");
        assertThat(compile("$['@prop']").toString()).isEqualTo("$['@prop']");
        assertThat(compile("$[  '@prop'  ]").toString()).isEqualTo("$['@prop']");
        assertThat(compile("$[\"prop\"]").toString()).isEqualTo("$[\"prop\"]");
    }

    @Test
    public void a_multi_property_token_can_be_compiled() {
        assertThat(compile("$['prop0', 'prop1']").toString()).isEqualTo("$['prop0','prop1']");
        assertThat(compile("$[  'prop0'  , 'prop1'  ]").toString()).isEqualTo("$['prop0','prop1']");
    }

    @Test
    public void a_property_chain_can_be_compiled() {
        assertThat(compile("$.abc").toString()).isEqualTo("$['abc']");
        assertThat(compile("$.aaa.bbb").toString()).isEqualTo("$['aaa']['bbb']");
        assertThat(compile("$.aaa.bbb.ccc").toString()).isEqualTo("$['aaa']['bbb']['ccc']");
    }

    @Test(expected = InvalidPathException.class)
    public void a_property_may_not_contain_blanks() {
        assertThat(compile("$.foo bar").toString());
    }

    @Test
    public void a_wildcard_can_be_compiled() {
        assertThat(compile("$.*").toString()).isEqualTo("$[*]");
        assertThat(compile("$[*]").toString()).isEqualTo("$[*]");
        assertThat(compile("$[ * ]").toString()).isEqualTo("$[*]");
    }

    @Test
    public void a_wildcard_can_follow_a_property() {
        assertThat(compile("$.prop[*]").toString()).isEqualTo("$['prop'][*]");
        assertThat(compile("$['prop'][*]").toString()).isEqualTo("$['prop'][*]");
    }

    @Test
    public void an_array_index_path_can_be_compiled() {
        assertThat(compile("$[1]").toString()).isEqualTo("$[1]");
        assertThat(compile("$[1,2,3]").toString()).isEqualTo("$[1,2,3]");
        assertThat(compile("$[ 1 , 2 , 3 ]").toString()).isEqualTo("$[1,2,3]");
    }

    @Test
    public void an_array_slice_path_can_be_compiled() {
        assertThat(compile("$[-1:]").toString()).isEqualTo("$[-1:]");
        assertThat(compile("$[1:2]").toString()).isEqualTo("$[1:2]");
        assertThat(compile("$[:2]").toString()).isEqualTo("$[:2]");
    }

    @Test
    public void an_inline_criteria_can_be_parsed() {
        assertThat(compile("$[?(@.foo == 'bar')]").toString()).isEqualTo("$[?]");
        assertThat(compile("$[?(@.foo == \"bar\")]").toString()).isEqualTo("$[?]");
    }

    @Test
    public void a_placeholder_criteria_can_be_parsed() {

        Predicate p = new Predicate() {
            @Override
            public boolean apply(PredicateContext ctx) {
                return false;
            }
        };
        assertThat(compile("$[?]", p).toString()).isEqualTo("$[?]");
        assertThat(compile("$[?,?]", p, p).toString()).isEqualTo("$[?,?]");
        assertThat(compile("$[?,?,?]", p, p, p).toString()).isEqualTo("$[?,?,?]");
    }

    @Test
    public void a_scan_token_can_be_parsed() {
        assertThat(compile("$..['prop']..[*]").toString()).isEqualTo("$..['prop']..[*]");
    }

    @Test
    public void issue_predicate_can_have_escaped_backslash_in_prop() {
        String json = "{\n"
                + "    \"logs\": [\n"
                + "        {\n"
                + "            \"message\": \"it\\\\\",\n"
                + "            \"id\": 2\n"
                + "        }\n"
                + "    ]\n"
                + "}";
        // message: it\ -> (after json escaping) -> "it\\" -> (after java escaping) -> "\"it\\\\\""

        List<String> result = JsonPath.read(json, "$.logs[?(@.message == 'it\\\\')].message");

        assertThat(result).containsExactly("it\\");
    }

    @Test
    public void issue_predicate_can_have_bracket_in_regex() {
        String json = "{\n"
                + "    \"logs\": [\n"
                + "        {\n"
                + "            \"message\": \"(it\",\n"
                + "            \"id\": 2\n"
                + "        }\n"
                + "    ]\n"
                + "}";

        List<String> result = JsonPath.read(json, "$.logs[?(@.message =~ /\\(it/)].message");

        assertThat(result).containsExactly("(it");
    }

    @Test
    public void issue_predicate_can_have_and_in_regex() {
        String json = "{\n"
                + "    \"logs\": [\n"
                + "        {\n"
                + "            \"message\": \"it\",\n"
                + "            \"id\": 2\n"
                + "        }\n"
                + "    ]\n"
                + "}";

        List<String> result = JsonPath.read(json, "$.logs[?(@.message =~ /&&|it/)].message");

        assertThat(result).containsExactly("it");
    }

    @Test
    public void issue_predicate_can_have_and_in_prop() {
        String json = "{\n"
                + "    \"logs\": [\n"
                + "        {\n"
                + "            \"message\": \"&& it\",\n"
                + "            \"id\": 2\n"
                + "        }\n"
                + "    ]\n"
                + "}";

        List<String> result = JsonPath.read(json, "$.logs[?(@.message == '&& it')].message");

        assertThat(result).containsExactly("&& it");
    }

    @Test
    public void issue_predicate_brackets_must_change_priorities() {
        String json = "{\n"
                + "    \"logs\": [\n"
                + "        {\n"
                + "            \"id\": 2\n"
                + "        }\n"
                + "    ]\n"
                + "}";

        List<String> result = JsonPath.read(json, "$.logs[?(@.message && (@.id == 1 || @.id == 2))].id");
        assertThat(result).isEmpty();

        result = JsonPath.read(json, "$.logs[?((@.id == 2 || @.id == 1) && @.message)].id");
        assertThat(result).isEmpty();
    }

    @Test
    public void issue_predicate_can_have_square_bracket_in_prop() {
        String json = "{\n"
                + "    \"logs\": [\n"
                + "        {\n"
                + "            \"message\": \"] it\",\n"
                + "            \"id\": 2\n"
                + "        }\n"
                + "    ]\n"
                + "}";

        List<String> result = JsonPath.read(json, "$.logs[?(@.message == '] it')].message");

        assertThat(result).containsExactly("] it");
    }
}
