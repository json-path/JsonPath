package com.jayway.jsonpath.matchers;

import org.junit.Test;

import static com.jayway.jsonpath.matchers.JsonPathMatchers.hasNoJsonPath;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

public class HasNoJsonPathTest {
    private static final String JSON_STRING = "{" +
            "\"none\": null," +
            "\"name\": \"Jessie\"" +
            "}";

    @Test
    public void shouldMatchMissingJsonPath() {
        assertThat(JSON_STRING, hasNoJsonPath("$.not_there"));
    }

    @Test
    public void shouldNotMatchExistingJsonPath() {
        assertThat(JSON_STRING, not(hasNoJsonPath("$.name")));
    }

    @Test
    public void shouldNotMatchExplicitNull() {
        assertThat(JSON_STRING, not(hasNoJsonPath("$.none")));
    }

    @Test
    public void shouldBeDescriptive() {
        assertThat(hasNoJsonPath("$.name"),
                hasToString(equalTo("is json without json path \"$['name']\"")));
    }

}
