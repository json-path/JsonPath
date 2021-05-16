package com.jayway.jsonpath.internal.function;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.jayway.jsonpath.*;
import com.jayway.jsonpath.spi.json.JacksonJsonNodeJsonProvider;
import com.jayway.jsonpath.spi.mapper.JacksonMappingProvider;
import org.junit.Assert;
import org.junit.Test;

import static com.jayway.jsonpath.JsonPath.using;
import static com.jayway.jsonpath.internal.path.PathCompiler.fail;

public class Issue612 {
    @Test
    public void test() {
        Configuration config = Configuration.builder()
                .options(Option.SUPPRESS_EXCEPTIONS)
                .build();
        String json = "{\"1\":{\"2\":null}}";
        DocumentContext documentContext = JsonPath.using(config).parse(json);
        JsonPath query = JsonPath.compile("$.1.2.a.b.c");
        Assert.assertNull(documentContext.read(query));
        Assert.assertNull(documentContext.map(query, (object, configuration) -> object));
    }
    @Test(expected = Exception.class)
    public void test2() {
        Configuration config = Configuration.builder()
                .build();
        String json = "{\"1\":{\"2\":null}}";
        DocumentContext documentContext = JsonPath.using(config).parse(json);
        JsonPath query = JsonPath.compile("$.1.2.a.b.c");
        documentContext.map(query, (object, configuration) -> object);
    }
}
