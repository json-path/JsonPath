package com.jayway.jsonpath;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static com.jayway.jsonpath.JsonPath.using;
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


}
