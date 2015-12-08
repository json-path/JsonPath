package com.jayway.jsonpath.matchers;

import com.jayway.jsonpath.ReadContext;
import org.hamcrest.Matcher;

import static com.jayway.jsonpath.matchers.WithJsonPath.withJsonPath;
import static org.hamcrest.Matchers.*;

public class JsonPathMatchers {

    private JsonPathMatchers() {
        throw new AssertionError("prevent instantiation");
    }

    public static Matcher<? super Object> hasJsonPath(String jsonPath) {
        return hasJsonPath(jsonPath, not(anyOf(nullValue(), empty())));
    }

    public static <T> Matcher<? super Object> hasJsonPath(final String jsonPath, final Matcher<T> resultMatcher) {
        return IsJson.isJson(withJsonPath(jsonPath, resultMatcher));
    }

    public static Matcher<Object> isJson() {
        return IsJson.isJson(withJsonPath("$..*"));
    }

    public static Matcher<Object> isJson(final Matcher<? super ReadContext> matcher) {
        return IsJson.isJson(matcher);
    }
}
