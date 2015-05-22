package com.jayway.jsonpath;

import org.junit.Test;

import java.util.List;
import java.util.Map;

import com.jayway.jsonpath.internal.token.TokenStack;
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

            @Override
            public boolean check(TokenStack stack, int idx) {
                return false;
            }
        };

        assertThat(reader.read("$.store.book[?].isbn", List.class, booksWithISBN)).containsOnly("0-395-19395-8", "0-553-21311-3");
    }
}
