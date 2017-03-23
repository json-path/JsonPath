package com.jayway.jsonpath.matchers;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.matchers.helpers.StrictParsingConfiguration;
import com.jayway.jsonpath.matchers.helpers.TestingMatchers;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.StringDescription;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;

import static com.jayway.jsonpath.matchers.JsonPathMatchers.isJson;
import static com.jayway.jsonpath.matchers.helpers.ResourceHelpers.resource;
import static com.jayway.jsonpath.matchers.helpers.ResourceHelpers.resourceAsFile;
import static com.jayway.jsonpath.matchers.helpers.TestingMatchers.withPathEvaluatedTo;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

public class IsJsonTest {
    private static final String VALID_JSON = resource("example.json");
    private static final String INVALID_JSON = "{ invalid-json }";
    private static final String BOOKS_JSON_STRING = resource("books.json");
    private static final File BOOKS_JSON_FILE = resourceAsFile("books.json");
    private static final Object BOOKS_JSON_PARSED = parseJson(BOOKS_JSON_STRING);

    @BeforeClass
    public static void setupStrictJsonParsing() {
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
        assertThat(BOOKS_JSON_STRING, isJson());
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
    public void shouldMatchJsonObjectEvaluatedToTrue() {
        assertThat(BOOKS_JSON_PARSED, isJson(withPathEvaluatedTo(true)));
    }

    @Test
    public void shouldNotMatchJsonObjectEvaluatedToFalse() {
        assertThat(BOOKS_JSON_PARSED, not(isJson(withPathEvaluatedTo(false))));
    }

    @Test
    public void shouldMatchJsonStringEvaluatedToTrue() {
        assertThat(BOOKS_JSON_STRING, isJson(withPathEvaluatedTo(true)));
    }

    @Test
    public void shouldNotMatchJsonStringEvaluatedToFalse() {
        assertThat(BOOKS_JSON_STRING, not(isJson(withPathEvaluatedTo(false))));
    }

    @Test
    public void shouldMatchJsonFileEvaluatedToTrue() {
        assertThat(BOOKS_JSON_FILE, isJson(withPathEvaluatedTo(true)));
    }

    @Test
    public void shouldNotMatchJsonFileEvaluatedToFalse() {
        assertThat(BOOKS_JSON_FILE, not(isJson(withPathEvaluatedTo(false))));
    }

    @Test
    public void shouldNotMatchNonExistingJsonFile() {
        File nonExistingFile = new File("missing-file");
        assertThat(nonExistingFile, not(isJson()));
    }

    @Test
    public void shouldBeDescriptive() {
        Matcher<Object> matcher = isJson(withPathEvaluatedTo(true));
        Description description = new StringDescription();
        matcher.describeTo(description);
        assertThat(description.toString(), startsWith("is json"));
        assertThat(description.toString(), containsString(TestingMatchers.MATCH_TRUE_TEXT));
    }

    @Test
    public void shouldDescribeMismatchOfValidJson() {
        Matcher<Object> matcher = isJson(withPathEvaluatedTo(true));
        Description description = new StringDescription();
        matcher.describeMismatch(BOOKS_JSON_STRING, description);
        assertThat(description.toString(), containsString(TestingMatchers.MISMATCHED_TEXT));
    }

    @Test
    public void shouldDescribeMismatchOfInvalidJson() {
        Matcher<Object> matcher = isJson(withPathEvaluatedTo(true));
        Description description = new StringDescription();
        matcher.describeMismatch("invalid-json", description);
        assertThat(description.toString(), containsString("\"invalid-json\""));
    }

    private static Object parseJson(String json) {
        return Configuration.defaultConfiguration().jsonProvider().parse(json);
    }

}
