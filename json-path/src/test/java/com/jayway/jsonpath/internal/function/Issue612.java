package com.jayway.jsonpath.internal.function;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class Issue612 {
    @Test
    public void test() {
        Configuration config = Configuration.builder()
                .options(Option.SUPPRESS_EXCEPTIONS)
                .build();
        String json = "{\"1\":{\"2\":null}}";
        DocumentContext documentContext = JsonPath.using(config).parse(json);
        JsonPath query = JsonPath.compile("$.1.2.a.b.c");
        Assertions.assertNull(documentContext.read(query));
        Assertions.assertNull(documentContext.map(query, (object, configuration) -> object));
    }

    @Test
    public void test2() {
        Configuration config = Configuration.builder()
                .build();
        String json = "{\"1\":{\"2\":null}}";
        DocumentContext documentContext = JsonPath.using(config).parse(json);
        JsonPath query = JsonPath.compile("$.1.2.a.b.c");

        Assertions.assertThrows(Exception.class, () -> documentContext.map(query, (object, configuration) -> object));
    }
}
