package com.jayway.jsonpath;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.Test;

import static com.jayway.jsonpath.JsonPath.using;
import static org.assertj.core.api.Assertions.assertThat;

public class JacksonTreeJsonProviderTest extends BaseTest {

    @Test
    public void json_can_be_parsed() {
        ObjectNode node =  using(JACKSON_TREE_CONFIGURATION).parse(JSON_DOCUMENT).read("$");
        assertThat(node.get("string-property").asText()).isEqualTo("string-value");
    }

    @Test
    public void strings_are_unwrapped() {
        JsonNode node =  using(JACKSON_TREE_CONFIGURATION).parse(JSON_DOCUMENT).read("$.string-property");
        String unwrapped =  using(JACKSON_TREE_CONFIGURATION).parse(JSON_DOCUMENT).read("$.string-property", String.class);

        assertThat(unwrapped).isEqualTo("string-value");
        assertThat(unwrapped).isEqualTo(node.asText());
    }

    @Test
    public void ints_are_unwrapped() {
        JsonNode node =  using(JACKSON_TREE_CONFIGURATION).parse(JSON_DOCUMENT).read("$.int-max-property");
        int unwrapped =  using(JACKSON_TREE_CONFIGURATION).parse(JSON_DOCUMENT).read("$.int-max-property", int.class);
        assertThat(unwrapped).isEqualTo(Integer.MAX_VALUE);
        assertThat(unwrapped).isEqualTo(node.asInt());
    }

    @Test
    public void longs_are_unwrapped() {
        JsonNode node =  using(JACKSON_TREE_CONFIGURATION).parse(JSON_DOCUMENT).read("$.long-max-property");
        long unwrapped =  using(JACKSON_TREE_CONFIGURATION).parse(JSON_DOCUMENT).read("$.long-max-property", long.class);

        assertThat(unwrapped).isEqualTo(Long.MAX_VALUE);
        assertThat(unwrapped).isEqualTo(node.asLong());
    }


    @Test
    public void list_of_numbers() {
        ArrayNode objs =  using(JACKSON_TREE_CONFIGURATION).parse(JSON_DOCUMENT).read("$.store.book[*].display-price");


        System.out.println(objs.toString());

    }


}
