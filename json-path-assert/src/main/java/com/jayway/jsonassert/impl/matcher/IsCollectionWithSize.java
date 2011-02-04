package com.jayway.jsonassert.impl.matcher;

import static org.hamcrest.core.IsEqual.equalTo;

import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;

import java.util.Collection;

/**
 * Matches if collection size satisfies a nested matcher.
 */
public class IsCollectionWithSize<E> extends CollectionMatcher<Collection<? extends E>> {
    private final Matcher<? super Integer> sizeMatcher;

    public IsCollectionWithSize(Matcher<? super Integer> sizeMatcher) {
        this.sizeMatcher = sizeMatcher;
    }

    @Override
    public boolean matchesSafely(Collection<? extends E> item) {
        return sizeMatcher.matches(item.size());
    }

    public void describeTo(Description description) {
        description.appendText("a collection with size ")
            .appendDescriptionOf(sizeMatcher);
    }

    /**
     * Does collection size satisfy a given matcher?
     */
    @Factory
    public static <E> Matcher<? super Collection<? extends E>> hasSize(Matcher<? super Integer> size) {
        return new IsCollectionWithSize<E>(size);
    }

    /**
     * This is a shortcut to the frequently used hasSize(equalTo(x)).
     *
     * For example,  assertThat(hasSize(equal_to(x)))
     *          vs.  assertThat(hasSize(x))
     */
    @Factory
    public static <E> Matcher<? super Collection<? extends E>> hasSize(int size) {
        Matcher<? super Integer> matcher = equalTo(size);
        return IsCollectionWithSize.<E>hasSize(matcher);
    }
}