package com.jayway.jsonpath;

import org.junit.Test;

import java.util.List;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

public class InlineFilterTest extends BaseTest {

    private static ReadContext reader = JsonPath.parse(JSON_DOCUMENT);

    @Test
    public void root_context_can_be_referred_in_predicate() {
        List<Double> prices = reader.read("store.book[?(@.display-price <= $.max-price)].display-price", List.class);

        assertThat(prices).containsAll(asList(8.95D, 8.99D));
    }

    @Test
    public void multiple_context_object_can_be_refered() {

        List all = reader.read("store.book[ ?(@.category == @.category) ]", List.class);
        assertThat(all.size()).isEqualTo(4);

        List all2 = reader.read("store.book[ ?(@.category == @['category']) ]", List.class);
        assertThat(all2.size()).isEqualTo(4);

        List all3 = reader.read("store.book[ ?(@ == @) ]", List.class);
        assertThat(all3.size()).isEqualTo(4);

        List none = reader.read("store.book[ ?(@.category != @.category) ]", List.class);
        assertThat(none.size()).isEqualTo(0);

        List none2 = reader.read("store.book[ ?(@.category != @) ]", List.class);
        assertThat(none2.size()).isEqualTo(0);

    }

    @Test
    public void document_queries_are_cached() {

        Object read = reader.read("$.store.book[?(@.display-price <= $.max-price)]");

        System.out.println(read);

    }
}
