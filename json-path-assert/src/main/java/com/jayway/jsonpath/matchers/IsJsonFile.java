package com.jayway.jsonpath.matchers;

import com.jayway.jsonpath.ReadContext;
import org.hamcrest.Matcher;

import java.io.File;

public class IsJsonFile extends IsJson<File> {

    IsJsonFile(Matcher<? super ReadContext> jsonMatcher) {
        super(jsonMatcher);
    }

    public static Matcher<File> isJsonFile(final Matcher<? super ReadContext> matcher) {
        return new IsJsonFile(matcher);
    }
}
