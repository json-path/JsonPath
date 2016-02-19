package com.jayway.jsonpath.matchers.helpers;

import com.jayway.jsonpath.ReadContext;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

/**
 * Dummy matchers to simplify testing
 */
public class TestingMatchers {

    public static final String MISMATCHED_TEXT = "with path mismatched";
    public static final String MATCH_TRUE_TEXT = "with path evaluated to <true>";

    public static Matcher<ReadContext> withPathEvaluatedTo(final boolean result) {
        return new TypeSafeMatcher<ReadContext>() {
            public void describeTo(Description description) {
                description.appendText("with path evaluated to ").appendValue(result);
            }

            @Override
            protected boolean matchesSafely(ReadContext ignored) {
                return result;
            }

            @Override
            protected void describeMismatchSafely(ReadContext ignore, Description mismatchDescription) {
                mismatchDescription.appendText(MISMATCHED_TEXT);
            }
        };
    }
}
