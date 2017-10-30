package com.jayway.jsonpath.matchers;

import static com.jayway.jsonpath.matchers.JsonPathMatchers.isJsonFile;
import static com.jayway.jsonpath.matchers.helpers.ResourceHelpers.resourceAsFile;
import static com.jayway.jsonpath.matchers.helpers.TestingMatchers.MATCH_TRUE_TEXT;
import static com.jayway.jsonpath.matchers.helpers.TestingMatchers.MISMATCHED_TEXT;
import static com.jayway.jsonpath.matchers.helpers.TestingMatchers.withPathEvaluatedTo;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.startsWith;
import static org.junit.Assert.assertThat;

import java.io.File;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.StringDescription;
import org.junit.Test;

public class IsJsonFileTest {
    private static final File BOOKS_JSON = resourceAsFile("books.json");
    private static final File INVALID_JSON = resourceAsFile("invalid.json");

    @Test
    public void shouldMatchJsonFileEvaluatedToTrue() {
        assertThat(BOOKS_JSON, isJsonFile(withPathEvaluatedTo(true)));
    }

    @Test
    public void shouldNotMatchJsonFileEvaluatedToFalse() {
        assertThat(BOOKS_JSON, not(isJsonFile(withPathEvaluatedTo(false))));
    }

    @Test
    public void shouldNotMatchInvalidJsonFile() {
        assertThat(INVALID_JSON, not(isJsonFile(withPathEvaluatedTo(true))));
        assertThat(INVALID_JSON, not(isJsonFile(withPathEvaluatedTo(false))));
    }

    @Test
    public void shouldBeDescriptive() {
        Matcher<File> matcher = isJsonFile(withPathEvaluatedTo(true));
        Description description = new StringDescription();
        matcher.describeTo(description);
        assertThat(description.toString(), startsWith("is json"));
        assertThat(description.toString(), containsString(MATCH_TRUE_TEXT));
    }

    @Test
    public void shouldDescribeMismatchOfValidJson() {
        Matcher<File> matcher = isJsonFile(withPathEvaluatedTo(true));
        Description description = new StringDescription();
        matcher.describeMismatch(BOOKS_JSON, description);
        assertThat(description.toString(), containsString(MISMATCHED_TEXT));
    }

    @Test
    public void shouldDescribeMismatchOfInvalidJson() {
        Matcher<File> matcher = isJsonFile(withPathEvaluatedTo(true));
        Description description = new StringDescription();
        matcher.describeMismatch(INVALID_JSON, description);
        assertThat(description.toString(), containsString("invalid.json"));
        assertThat(description.toString(), containsString("invalid-json"));
    }
}
