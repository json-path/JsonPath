package com.jayway.jsonpath.internal.function;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;
import org.junit.Test;

import static org.junit.Assert.assertNull;

public class Issue628 {

    @Test
    public void nonexistant_property_returns_null_when_configured() {
        String document = "{}";
        Configuration config = Configuration.builder().options(Option.SUPPRESS_EXCEPTIONS).build();
        String nonExistentPath = "$.doesNotExist";

        assertNull(JsonPath.read(config.jsonProvider().parse(document), nonExistentPath));
    }
}