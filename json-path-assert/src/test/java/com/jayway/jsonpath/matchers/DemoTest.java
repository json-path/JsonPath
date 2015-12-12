package com.jayway.jsonpath.matchers;

import org.junit.Ignore;
import org.junit.Test;

import java.io.File;

import static com.jayway.jsonpath.matchers.JsonPathMatchers.*;
import static com.jayway.jsonpath.matchers.helpers.ResourceHelpers.resource;
import static com.jayway.jsonpath.matchers.helpers.ResourceHelpers.resourceAsFile;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

@Ignore
public class DemoTest {
    @Test
    public void shouldFailOnJsonString() {
        String json = resource("books.json");
        assertThat(json, isJson(withJsonPath("$.store.name", equalTo("The Shop"))));
    }

    @Test
    public void shouldFailOnJsonFile() {
        File json = resourceAsFile("books.json");
        assertThat(json, isJson(withJsonPath("$.store.name", equalTo("The Shop"))));
    }

    @Test
    public void shouldFailOnInvalidJsonString() {
        String json = resource("invalid.json");
        assertThat(json, isJson(withJsonPath("$.store.name", equalTo("The Shop"))));
    }

    @Test
    public void shouldFailOnInvalidJsonFile() {
        File json = resourceAsFile("invalid.json");
        assertThat(json, isJson(withJsonPath("$.store.name", equalTo("The Shop"))));
    }

    @Test
    public void shouldFailOnTypedJsonString() {
        String json = resource("books.json");
        assertThat(json, isJsonString(withJsonPath("$.store.name", equalTo("The Shop"))));
    }

    @Test
    public void shouldFailOnTypedJsonFile() {
        File json = resourceAsFile("books.json");
        assertThat(json, isJsonFile(withJsonPath("$.store.name", equalTo("The Shop"))));
    }

    @Test
    public void shouldFailOnTypedInvalidJsonString() {
        String json = resource("invalid.json");
        assertThat(json, isJsonString(withJsonPath("$.store.name", equalTo("The Shop"))));
    }

    @Test
    public void shouldFailOnTypedInvalidJsonFile() {
        File json = resourceAsFile("invalid.json");
        assertThat(json, isJsonFile(withJsonPath("$.store.name", equalTo("The Shop"))));
    }

    @Test
    public void shouldFailOnNonExistingJsonPath() {
        String json = resource("books.json");
        assertThat(json, hasJsonPath("$.not-here"));
    }

    @Test
    public void shouldFailOnExistingJsonPath() {
        String json = resource("books.json");
        assertThat(json, hasNoJsonPath("$.store.name"));
    }

    @Test
    public void shouldFailOnExistingJsonPathAlternative() {
        String json = resource("books.json");
        assertThat(json, isJson(withoutJsonPath("$.store.name")));
    }
}
