package com.jayway.jsonpath.matchers;

import com.jayway.jsonpath.*;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

import static org.hamcrest.Matchers.*;

public class WithJsonPath<T> extends TypeSafeMatcher<ReadContext> {
    private final JsonPath jsonPath;
    private final Matcher<T> resultMatcher;

    private WithJsonPath(JsonPath jsonPath, Matcher<T> resultMatcher) {
        this.jsonPath = jsonPath;
        this.resultMatcher = resultMatcher;
    }

    public static Matcher<? super ReadContext> withJsonPath(String jsonPath, Predicate... filters) {
        return withJsonPath(JsonPath.compile(jsonPath, filters));
    }

    public static Matcher<? super ReadContext> withJsonPath(JsonPath jsonPath) {
        return withJsonPath(jsonPath, not(anyOf(nullValue(), empty())));
    }

    public static <T> Matcher<? super ReadContext> withJsonPath(String jsonPath, Matcher<T> resultMatcher) {
        return withJsonPath(JsonPath.compile(jsonPath), resultMatcher);
    }

    public static <T> Matcher<? super ReadContext> withJsonPath(final JsonPath jsonPath, final Matcher<T> resultMatcher) {
        return new WithJsonPath<T>(jsonPath, resultMatcher);
    }

    @Override
    protected boolean matchesSafely(ReadContext context) {
        try {
            T value = context.read(jsonPath);
            return resultMatcher.matches(value);
        } catch (JsonPathException e) {
            return false;
        }
    }

    public void describeTo(Description description) {
        description
                .appendText("with json path ")
                .appendValue(jsonPath.getPath())
                .appendText(" evaluated to ")
                .appendDescriptionOf(resultMatcher);
    }

    @Override
    protected void describeMismatchSafely(ReadContext context, Description mismatchDescription) {
        try {
            T value = jsonPath.read(context.json());
            mismatchDescription
                    .appendText("json path ")
                    .appendValue(jsonPath.getPath())
                    .appendText(" was evaluated to ")
                    .appendValue(value);
        } catch (PathNotFoundException e) {
            mismatchDescription
                    .appendText("json path ")
                    .appendValue(jsonPath.getPath())
                    .appendText(" was not found in ")
                    .appendValue(context.json());
        } catch (JsonPathException e) {
            mismatchDescription
                    .appendText("was ")
                    .appendValue(context.json());
        }
    }

}
