package com.jayway.jsonpath.matchers;

import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.JsonPathException;
import com.jayway.jsonpath.PathNotFoundException;
import com.jayway.jsonpath.ReadContext;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

public class WithJsonPath<T> extends TypeSafeMatcher<ReadContext> {
    private final JsonPath jsonPath;
    private final Matcher<T> resultMatcher;

    public WithJsonPath(JsonPath jsonPath, Matcher<T> resultMatcher) {
        this.jsonPath = jsonPath;
        this.resultMatcher = resultMatcher;
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
            T value = jsonPath.read(context.jsonString());
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
