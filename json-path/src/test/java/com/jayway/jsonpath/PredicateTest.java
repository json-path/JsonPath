package com.jayway.jsonpath;

import org.junit.Ignore;
import org.junit.Test;

import java.util.List;
import java.util.Map;

import static com.jayway.jsonpath.JsonPath.using;
import static org.assertj.core.api.Assertions.assertThat;

public class PredicateTest extends BaseTest {

    private static ReadContext reader = using(GSON_CONFIGURATION).parse(JSON_DOCUMENT);

    @Test
    public void predicates_filters_can_be_applied() {
        Predicate booksWithISBN = new Predicate() {
            @Override
            public boolean apply(PredicateContext ctx) {
                return ctx.item(Map.class).containsKey("isbn");
            }
        };

        assertThat(reader.read("$.store.book[?].isbn", List.class, booksWithISBN)).containsOnly("0-395-19395-8", "0-553-21311-3");
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

    @Ignore("not ready yet (requires compiler reimplementation)")
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

    @Ignore("not ready yet (requires compiler reimplementation)")
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

    @Ignore("not ready yet (requires compiler reimplementation)")
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

    @Ignore("not ready yet (requires compiler reimplementation)")
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

    @Ignore("not ready yet (requires compiler reimplementation)")
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
