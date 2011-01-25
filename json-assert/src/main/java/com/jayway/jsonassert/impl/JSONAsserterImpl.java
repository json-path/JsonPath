package com.jayway.jsonassert.impl;


import com.jayway.jsonassert.JSONAsserter;
import com.jayway.jsonassert.JSONReader;
import org.hamcrest.Matcher;
import org.hamcrest.MatcherAssert;

import static org.hamcrest.Matchers.*;

/**
 * User: kalle stenflo
 * Date: 1/21/11
 * Time: 3:43 PM
 */
public class JSONAsserterImpl implements JSONAsserter {

    private final JSONReader reader;


    /**
     * Instantiates a new JSONAsserter
     *
     * @param reader initialized with the JSON document to be asserted upon
     */
    public JSONAsserterImpl(JSONReader reader) {
        this.reader = reader;
    }

    /**
     * {@inheritDoc}
     */
    public JSONReader reader() {
        return reader;
    }

    /**
     * {@inheritDoc}
     */
    public <T> JSONAsserter assertThat(String path, Matcher<T> matcher) {
        MatcherAssert.assertThat((T) reader.get(path), matcher);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public <T> JSONAsserter assertEquals(String path, T expected) {
        return assertThat(path, equalTo(expected));
    }

    /**
     * {@inheritDoc}
     */
    public JSONAsserter assertNull(String path) {
        return assertThat(path, nullValue());
    }

    /**
     * {@inheritDoc}
     */
    public <T> JSONAsserter assertNotNull(String path) {
        return assertThat(path, notNullValue());
    }

    /**
     * {@inheritDoc}
     */
    public JSONAsserter and() {
        return this;
    }

}
