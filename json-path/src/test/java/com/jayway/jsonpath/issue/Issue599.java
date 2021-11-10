package com.jayway.jsonpath.issue;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;
import com.jayway.jsonpath.spi.json.GsonJsonProvider;
import com.jayway.jsonpath.spi.mapper.GsonMappingProvider;
import org.junit.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class Issue599 {

    @Test
    public void test_issue_599() {
        String json = "[" +
                    "{\n" +
                    "   \"name\": \"Jack\",\n" +
                    "   \"gender\": \"male\"\n" +
                    "}, " +
                    "{\n" +
                    "   \"name\": \"Lisa\",\n" +
                    "   \"gender\": \"female\"\n" +
                    "}" +
                "]";
        Configuration config = Configuration.builder().options(Option.ALWAYS_RETURN_LIST)
                .jsonProvider(new GsonJsonProvider()).mappingProvider(new GsonMappingProvider()).build();
        DocumentContext ctx = JsonPath.parse(json, config);

        // Gson will parse a node as JsonPrimitive
        List<JsonPrimitive> list = ctx.read("$[*].name");
        assertThat(list).isInstanceOf(List.class);

        JsonElement element1 = new JsonPrimitive("Jack");
        JsonElement element2 = new JsonPrimitive("Lisa");
        assertThat(list.get(0)).isEqualTo(element1);
        assertThat(list.get(1)).isEqualTo(element2);
    }
}
