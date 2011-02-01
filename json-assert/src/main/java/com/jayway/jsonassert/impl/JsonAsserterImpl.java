package com.jayway.jsonassert.impl;


import com.jayway.jsonassert.JsonAsserter;
import com.jayway.jsonassert.JsonPath;
import org.hamcrest.Matcher;
import org.hamcrest.MatcherAssert;

import static org.hamcrest.Matchers.*;

/**
 * User: kalle stenflo
 * Date: 1/21/11
 * Time: 3:43 PM
 */
public class JsonAsserterImpl implements JsonAsserter {

    private final JsonPath reader;


    /**
     * Instantiates a new JSONAsserter
     *
     * @param reader initialized with the JSON document to be asserted upon
     */
    public JsonAsserterImpl(JsonPath reader) {
        this.reader = reader;
    }

    /**
     * {@inheritDoc}
     */
    public JsonPath reader() {
        return reader;
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
	public <T> JsonAsserter assertThat(String path, Matcher<T> matcher) {
        MatcherAssert.assertThat((T) reader.get(path), matcher);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public <T> JsonAsserter assertEquals(String path, T expected) {
        return assertThat(path, equalTo(expected));
    }

    /**
     * {@inheritDoc}
     */
    public JsonAsserter assertNull(String path) {
        return assertThat(path, nullValue());
    }

    /**
     * {@inheritDoc}
     */
    public <T> JsonAsserter assertNotNull(String path) {
        return assertThat(path, notNullValue());
    }

    /**
     * {@inheritDoc}
     */
    public JsonAsserter and() {
        return this;
    }

}
