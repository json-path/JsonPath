package com.jayway.jsonpath.matchers;

import com.jayway.jsonpath.ReadContext;
import org.hamcrest.Matcher;

public class IsJsonString extends IsJson<String> {

    IsJsonString(Matcher<? super ReadContext> jsonMatcher) {
        super(jsonMatcher);
    }

    public static Matcher<String> isJsonString(final Matcher<? super ReadContext> matcher) {
        return new IsJsonString(matcher);
    }
}
