package com.jayway.jsonpath;

import org.junit.Test;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

public class Issue_825 extends BaseTest {
    @Test
    public void double_stream_test() {
        Stream<Double> stream = JsonPath.parse(JSON_DOCUMENT).read("store.book[?(@.display-price <= $.max-price)].display-price", Stream.class);
        assertThat(stream.collect(Collectors.toList())).containsAll(asList(8.95D, 8.99D));
    }

    @Test
    public void string_stream_test() {
        Stream<String> stream = JsonPath.parse(JSON_DOCUMENT).read("$.store.book.[*].title", Stream.class);
        assertThat(stream.collect(Collectors.toList())).containsAll(asList("Sayings of the Century", "Sword of Honour", "Moby Dick", "The Lord of the Rings"));
    }
}
