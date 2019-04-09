package com.jayway.jsonpath.matchers;

import com.jayway.jsonpath.InvalidPathException;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.ReadContext;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.StringDescription;
import org.junit.Test;

import java.util.Collection;
import java.util.List;

import static com.jayway.jsonpath.JsonPath.compile;
import static com.jayway.jsonpath.matchers.JsonPathMatchers.withJsonPath;
import static com.jayway.jsonpath.matchers.helpers.ResourceHelpers.resource;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

public class WithJsonPathTest {
    private static final ReadContext BOOKS_JSON = JsonPath.parse(resource("books.json"));

    @Test
    public void shouldMatchExistingCompiledJsonPath() {
        assertThat(BOOKS_JSON, withJsonPath(compile("$.expensive")));
        assertThat(BOOKS_JSON, withJsonPath(compile("$.store.bicycle")));
        assertThat(BOOKS_JSON, withJsonPath(compile("$.store.book[2].title")));
        assertThat(BOOKS_JSON, withJsonPath(compile("$.store.book[*].author")));
    }

    @Test
    public void shouldMatchExistingStringJsonPath() {
        assertThat(BOOKS_JSON, withJsonPath("$.expensive"));
        assertThat(BOOKS_JSON, withJsonPath("$.store.bicycle"));
        assertThat(BOOKS_JSON, withJsonPath("$.store.book[2].title"));
        assertThat(BOOKS_JSON, withJsonPath("$.store.book[*].author"));
    }

    @Test
    public void shouldNotMatchNonExistingJsonPath() {
        assertThat(BOOKS_JSON, not(withJsonPath(compile("$.not_there"))));
        assertThat(BOOKS_JSON, not(withJsonPath(compile("$.store.book[5].title"))));
        assertThat(BOOKS_JSON, not(withJsonPath(compile("$.store.book[1].not_there"))));
    }

    @Test
    public void shouldNotMatchNonExistingStringJsonPath() {
        assertThat(BOOKS_JSON, not(withJsonPath("$.not_there")));
        assertThat(BOOKS_JSON, not(withJsonPath("$.store.book[5].title")));
        assertThat(BOOKS_JSON, not(withJsonPath("$.store.book[1].not_there")));
    }

    @Test
    public void shouldMatchJsonPathEvaluatedToStringValue() {
        assertThat(BOOKS_JSON, withJsonPath(compile("$.store.bicycle.color"), equalTo("red")));
        assertThat(BOOKS_JSON, withJsonPath(compile("$.store.book[2].title"), equalTo("Moby Dick")));
        assertThat(BOOKS_JSON, withJsonPath("$.store.name", equalTo("Little Shop")));
        assertThat(BOOKS_JSON, withJsonPath("$.store.book[2].title", equalTo("Moby Dick")));
    }

    @Test
    public void shouldMatchJsonPathEvaluatedToIntegerValue() {
        assertThat(BOOKS_JSON, withJsonPath(compile("$.expensive"), equalTo(10)));
        assertThat(BOOKS_JSON, withJsonPath("$.expensive", equalTo(10)));
    }

    @Test
    public void shouldMatchJsonPathEvaluatedToDoubleValue() {
        assertThat(BOOKS_JSON, withJsonPath(compile("$.store.bicycle.price"), equalTo(19.95)));
        assertThat(BOOKS_JSON, withJsonPath("$.store.bicycle.price", equalTo(19.95)));
    }

    @Test
    public void shouldMatchJsonPathEvaluatedToCollectionValue() {
        assertThat(BOOKS_JSON, withJsonPath(compile("$.store.book[*].author"), instanceOf(List.class)));
        assertThat(BOOKS_JSON, withJsonPath(compile("$.store.book[*].author"), hasSize(4)));
        assertThat(BOOKS_JSON, withJsonPath(compile("$.store.book[*].author"), hasItem("Evelyn Waugh")));
        assertThat(BOOKS_JSON, withJsonPath(compile("$..book[2].title"), hasItem("Moby Dick")));
        assertThat(BOOKS_JSON, withJsonPath("$.store.book[*].author", instanceOf(Collection.class)));
        assertThat(BOOKS_JSON, withJsonPath("$.store.book[*].author", hasSize(4)));
        assertThat(BOOKS_JSON, withJsonPath("$.store.book[*].author", hasItem("Evelyn Waugh")));
        assertThat(BOOKS_JSON, withJsonPath("$..book[2].title", hasItem("Moby Dick")));
    }

    @Test(expected = InvalidPathException.class)
    public void shouldFailOnInvalidJsonPath() {
        withJsonPath("$[}");
    }

    @Test
    public void shouldNotMatchOnInvalidJson() {
        ReadContext invalidJson = JsonPath.parse("invalid-json");
        assertThat(invalidJson, not(withJsonPath("$.expensive", equalTo(10))));
    }

    @Test
    public void shouldBeDescriptive() {
        Matcher<? super ReadContext> matcher = withJsonPath("path", equalTo(2));
        Description description = new StringDescription();
        matcher.describeTo(description);
        assertThat(description.toString(), containsString("path"));
        assertThat(description.toString(), containsString("<2>"));
    }

    @Test
    public void shouldDescribeMismatchOfEvaluation() {
        Matcher<? super ReadContext> matcher = withJsonPath("expensive", equalTo(3));
        Description description = new StringDescription();
        matcher.describeMismatch(BOOKS_JSON, description);
        assertThat(description.toString(), containsString("expensive"));
        assertThat(description.toString(), containsString("<10>"));
    }

    @Test
    public void shouldDescribeMismatchOfPathNotFound() {
        Matcher<? super ReadContext> matcher = withJsonPath("not-here", equalTo(3));
        Description description = new StringDescription();
        matcher.describeMismatch(BOOKS_JSON, description);
        assertThat(description.toString(), containsString("not-here"));
        assertThat(description.toString(), containsString("was not found"));
    }

}
