package com.jayway.jsonpath;

import org.junit.Test;
import java.util.List;

import static com.jayway.jsonpath.JsonPath.using;
import static org.assertj.core.api.Assertions.assertThat;

public class PatternPathTokenIntegrationTest extends BaseTest {

    final Configuration configuration = Configuration.defaultConfiguration().addOptions(Option.DEFAULT_PATH_LEAF_TO_NULL, Option.AS_PATH_LIST);

    @Test
    public void predicates_filters_with_name() {
        List<String> read = using(configuration).parse("{\"abc\":{\"qwr\":4},\"acd\":2,\"d\":3}").read("$.[/a.*/i]", List.class);
        assertThat(read).containsOnlyOnce("$['abc', 'acd']");
    }

    @Test
    public void predicates_filters_with_name_in_array() {
        List<String> read = using(configuration).parse("[{\"abc\":{\"qwr\":4},\"acd\":2,\"d\":3}]").read("$.[0:1][/a.*/i]", List.class);
        assertThat(read).containsOnlyOnce("$[0]['abc', 'acd']");
    }

    @Test
    public void predicates_filters_with_name_in_array_splitted() {
        List<String> read = using(configuration).parse("[{\"abc\":{\"qwr\":4},\"d\":3},{\"acd\":2,\"d\":3}]").read("$.[0:2][/a.*/i]", List.class);
        assertThat(read).containsOnlyOnce("$[0]['abc']","$[1]['acd']");
    }


    @Test
    public void scan_predicates_filters_with_name() {
        List<String> read = using(configuration).parse("{\"abc\":{\"qwr\":4},\"acd\":2,\"d\":3}").read("$..[/a.*/i]", List.class);
        assertThat(read).containsOnlyOnce("$['abc', 'acd']");
    }

    @Test
    public void scan_predicates_filters_with_name_in_array() {
        List<String> read = using(configuration).parse("[{\"abc\":{\"qwr\":4},\"acd\":2,\"d\":3}]").read("$..[/a.*/i]", List.class);
        assertThat(read).containsOnlyOnce("$[0]['abc', 'acd']");
    }

    @Test
    public void scan_predicates_filters_with_name_in_array_splitted() {
        List<String> read = using(configuration).parse("[{\"abc\":{\"qwr\":4},\"d\":3},{\"acd\":2,\"d\":3}]").read("$..[/a.*/i]", List.class);
        assertThat(read).containsOnlyOnce("$[0]['abc']", "$[1]['acd']");
    }

}
