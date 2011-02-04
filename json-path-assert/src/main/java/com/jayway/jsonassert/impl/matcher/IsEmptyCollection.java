package com.jayway.jsonassert.impl.matcher;

import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;

import java.util.Collection;

/**
 * Tests if collection is empty.
 */
public class IsEmptyCollection<E> extends CollectionMatcher<Collection<E>> {

    @Override
    public boolean matchesSafely(Collection<E> item) {
        return item.isEmpty();
    }

    public void describeTo(Description description) {
        description.appendText("an empty collection");
    }

    /**
     * Matches an empty collection.
     */
    @Factory
    public static <E> Matcher<Collection<E>> empty() {
        return new IsEmptyCollection<E>();
    }
}