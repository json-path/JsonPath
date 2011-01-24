package com.jayway.jsonassert.impl;


import org.hamcrest.Matcher;
import org.hamcrest.MatcherAssert;

import java.text.ParseException;

import static org.hamcrest.Matchers.*;

/**
 * User: kalle stenflo
 * Date: 1/21/11
 * Time: 3:43 PM
 */
public class JSONAsserter {

    private final JSONReader reader;

    public static JSONAsserter with(String json) throws ParseException {
        return new JSONAsserter(JSONReader.parse(json));
    }

    private JSONAsserter(JSONReader reader) {
        this.reader = reader;
    }

    public <T> JSONAsserter assertThat(String path, Matcher<T> matcher) {

        MatcherAssert.assertThat((T) reader.get(path), matcher);

        return this;
    }

    public <T> JSONAsserter assertEquals(String path, T expected) {
        return assertThat(path, equalTo(expected));
    }

    public <T> JSONAsserter assertNull(String path) {
        return assertThat(path, nullValue());
    }

    public <T> JSONAsserter assertNotNull(String path) {
        return assertThat(path, notNullValue());
    }

    public JSONAsserter and(){
        return this;
    }

}
