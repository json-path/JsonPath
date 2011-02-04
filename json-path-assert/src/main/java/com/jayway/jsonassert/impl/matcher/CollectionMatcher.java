package com.jayway.jsonassert.impl.matcher;

import org.hamcrest.BaseMatcher;

import java.util.Collection;

public abstract class CollectionMatcher<C extends Collection<?>> extends BaseMatcher<C> {
    @SuppressWarnings("unchecked")
    public boolean matches(Object item) {
        if (!(item instanceof Collection)) {
            return false;
        }
        return matchesSafely((C)item);
    }

    protected abstract boolean matchesSafely(C collection);
}