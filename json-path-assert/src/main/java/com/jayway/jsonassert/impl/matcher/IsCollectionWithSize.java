/*
BSD License

Copyright (c) 2000-2006, www.hamcrest.org
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of
conditions and the following disclaimer. Redistributions in binary form must reproduce
the above copyright notice, this list of conditions and the following disclaimer in
the documentation and/or other materials provided with the distribution.

Neither the name of Hamcrest nor the names of its contributors may be used to endorse
or promote products derived from this software without specific prior written
permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY
EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT
SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED
TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR
BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY
WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH
DAMAGE.
*/
package com.jayway.jsonassert.impl.matcher;

import org.hamcrest.Description;
import org.hamcrest.Matcher;

import java.util.Collection;

import static org.hamcrest.core.IsEqual.equalTo;

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

    @Override
    public void describeTo(Description description) {
        description.appendText("a collection with size ")
            .appendDescriptionOf(sizeMatcher);
    }

    /**
     * Does collection size satisfy a given matcher?
     */
    public static <E> Matcher<? super Collection<? extends E>> hasSize(Matcher<? super Integer> size) {
        return new IsCollectionWithSize<E>(size);
    }

    /**
     * This is a shortcut to the frequently used hasSize(equalTo(x)).
     *
     * For example,  assertThat(hasSize(equal_to(x)))
     *          vs.  assertThat(hasSize(x))
     */
    public static <E> Matcher<? super Collection<? extends E>> hasSize(int size) {
        Matcher<? super Integer> matcher = equalTo(size);
        return IsCollectionWithSize.<E>hasSize(matcher);
    }
}
