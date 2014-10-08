package com.jayway.jsonpath;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class EscapeTest extends BaseTest {

    @Test
    public void urls_are_not_escaped() {

        String json = "[" +
                "\"https://a/b/1\"," +
                "\"https://a/b/2\"," +
                "\"https://a/b/3\"" +
                "]";

        String resAsString = JsonPath.using(JSON_SMART_CONFIGURATION).parse(json).read("$").toString();

        assertThat(resAsString).contains("https://a/b/1");

    }
}
