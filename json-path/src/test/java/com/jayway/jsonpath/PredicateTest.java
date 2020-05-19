package com.jayway.jsonpath;

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
    public void predicates_filters_with_name() {
        Predicate prefixfilter = new Predicate() {
            @Override
            public boolean apply(PredicateContext ctx) {
                return "abc".equals(ctx.getCurrentPath().getTagName());
            }
        };

        Object read = using(GSON_CONFIGURATION).parse("{\"abc\":{\"qwr\":4},\"acd\":2,\"d\":3}").read("$..*[?]",prefixfilter);
        System.out.println(read);
        assertThat(read);
    }
}
