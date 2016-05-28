package com.jayway.jsonpath.matchers;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.matchers.helpers.StrictParsingConfiguration;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.util.Collection;

import static com.jayway.jsonpath.matchers.JsonPathMatchers.*;
import static com.jayway.jsonpath.matchers.helpers.ResourceHelpers.resource;
import static com.jayway.jsonpath.matchers.helpers.ResourceHelpers.resourceAsFile;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

public class JsonPathMatchersTest {

    private static final String VALID_JSON = resource("example.json");
    private static final String BOOKS_JSON = resource("books.json");
    private static final String INVALID_JSON = "{ invalid-json }";
    private static final File BOOKS_JSON_FILE = resourceAsFile("books.json");

    @BeforeClass
    public static void setupStrictJsonParsing() {
        // NOTE: Evaluation depends on the default configuration of JsonPath
        Configuration.setDefaults(new StrictParsingConfiguration());
    }

    @AfterClass
    public static void setupDefaultJsonParsing() {
        Configuration.setDefaults(null);
    }

    @Test
    public void shouldMatchOnEmptyJsonObject() {
        assertThat("{}", isJson());
    }

    @Test
    public void shouldMatchOnJsonObject() {
        assertThat("{ \"hi\" : \"there\" }", isJson());
    }

    @Test
    public void shouldMatchOnEmptyJsonArray() {
        assertThat("[]", isJson());
    }

    @Test
    public void shouldMatchOnJsonArray() {
        assertThat("[\"hi\", \"there\"]", isJson());
    }

    @Test
    public void shouldMatchValidJson() {
        assertThat(VALID_JSON, isJson());
        assertThat(BOOKS_JSON, isJson());
    }

    @Test
    public void shouldNotMatchInvalidJson() {
        assertThat(INVALID_JSON, not(isJson()));
        assertThat(new Object(), not(isJson()));
        assertThat(new Object[]{}, not(isJson()));
        assertThat("hi there", not(isJson()));
        assertThat(new Integer(42), not(isJson()));
        assertThat(Boolean.TRUE, not(isJson()));
        assertThat(false, not(isJson()));
        assertThat(null, not(isJson()));
    }

    @Test
    public void shouldMatchExistingJsonPath() {
        assertThat(BOOKS_JSON, hasJsonPath("$.store.name"));
        assertThat(BOOKS_JSON, hasJsonPath("$.store.book[2].title"));
        assertThat(BOOKS_JSON, hasJsonPath("$.store.book[*].author"));
    }

    @Test
    public void shouldMatchExistingJsonPathAlternative() {
        assertThat(BOOKS_JSON, isJson(withJsonPath("$.store.name")));
        assertThat(BOOKS_JSON, isJson(withJsonPath("$.store.book[2].title")));
        assertThat(BOOKS_JSON, isJson(withJsonPath("$.store.book[*].author")));
    }

    @Test
    public void shouldNotMatchInvalidJsonWithPath() {
        assertThat(INVALID_JSON, not(hasJsonPath("$.path")));
        assertThat(new Object(), not(hasJsonPath("$.path")));
        assertThat("{}", not(hasJsonPath("$.path")));
        assertThat(null, not(hasJsonPath("$.path")));
    }

    @Test
    public void shouldNotMatchInvalidJsonWithPathAndValue() {
        assertThat(INVALID_JSON, not(hasJsonPath("$.path", anything())));
        assertThat(new Object(), not(hasJsonPath("$.path", anything())));
        assertThat(null, not(hasJsonPath("$.message", anything())));
    }

    @Test
    public void shouldNotMatchNonExistingJsonPath() {
        assertThat(BOOKS_JSON, not(hasJsonPath("$.not_there")));
        assertThat(BOOKS_JSON, not(hasJsonPath("$.store.book[5].title")));
        assertThat(BOOKS_JSON, not(hasJsonPath("$.store.book[*].not_there")));
    }

    @Test
    public void shouldNotMatchNonExistingJsonPathAlternative() {
        assertThat(BOOKS_JSON, not(isJson(withJsonPath("$.not_there"))));
        assertThat(BOOKS_JSON, not(isJson(withJsonPath("$.store.book[5].title"))));
        assertThat(BOOKS_JSON, not(isJson(withJsonPath("$.store.book[*].not_there"))));
    }

    @Test
    public void shouldMatchJsonPathWithStringValue() {
        assertThat(BOOKS_JSON, hasJsonPath("$.store.name", equalTo("Little Shop")));
        assertThat(BOOKS_JSON, hasJsonPath("$.store.book[2].title", equalTo("Moby Dick")));
    }

    @Test
    public void shouldMatchJsonPathWithIntegerValue() {
        assertThat(BOOKS_JSON, hasJsonPath("$.expensive", equalTo(10)));
    }

    @Test
    public void shouldMatchJsonPathWithDoubleValue() {
        assertThat(BOOKS_JSON, hasJsonPath("$.store.bicycle.price", equalTo(19.95)));
    }

    @Test
    public void shouldMatchJsonPathWithCollectionValue() {
        assertThat(BOOKS_JSON, hasJsonPath("$.store.book[*].author", instanceOf(Collection.class)));
        assertThat(BOOKS_JSON, hasJsonPath("$.store.book[*].author", hasSize(4)));
        assertThat(BOOKS_JSON, hasJsonPath("$.store.book[*].author", hasItem("Evelyn Waugh")));
        assertThat(BOOKS_JSON, hasJsonPath("$..book[2].title", hasItem("Moby Dick")));
    }

    @Test
    public void shouldMatchJsonPathOnFile() {
        assertThat(BOOKS_JSON_FILE, hasJsonPath("$.store.name", equalTo("Little Shop")));
    }

    @Test
    public void shouldNotMatchJsonPathOnNonExistingFile() {
        File nonExistingFile = new File("missing-file");
        assertThat(nonExistingFile, not(hasJsonPath("$..*", anything())));
    }

    @Test
    public void shouldMatchJsonPathOnParsedJsonObject() {
        Object json = Configuration.defaultConfiguration().jsonProvider().parse(BOOKS_JSON);
        assertThat(json, hasJsonPath("$.store.name", equalTo("Little Shop")));
    }

    @Test
    public void shouldMatchMissingJsonPath() {
        assertThat(BOOKS_JSON, hasNoJsonPath("$.not_there"));
    }

    @Test
    public void shouldNotMatchExistingJsonPath() {
        assertThat(BOOKS_JSON, not(hasNoJsonPath("$.store.name")));
    }
}
