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

    /**
     * Creates a new instance of a JSONAsserter that can be used to make
     * assertions on the provided JSON document or array.
     *
     * @param json the JSON document to create a JSONAsserter for
     * @return a JSON asserter initialized with the provided document
     * @throws ParseException
     */
    public static JSONAsserter with(String json) throws ParseException {
        return new JSONAsserter(JSONReader.parse(json));
    }

    /**
     * Instantiates a new JSONAsserter
     *
     * @param reader initialized with the JSON document to be asserted upon
     */
    private JSONAsserter(JSONReader reader) {
        this.reader = reader;
    }

    /**
     * Asserts that object specified by path satisfies the condition specified by matcher.
     * If not, an AssertionError is thrown with information about the matcher
     * and failing value. Example:
     * <p/>
     * <code>
     * with(json).assertThat("items[0].name", equalTo("Bobby"))
     *           .assertThat("items[0].age" , equalTo(24L))
     * </code>
     *
     * @param path    the json path specifying the value being compared
     * @param matcher an expression, built of Matchers, specifying allowed values
     * @param <T>     the static type accepted by the matcher
     * @return this to allow fluent assertion chains
     */
    public <T> JSONAsserter assertThat(String path, Matcher<T> matcher) {
        MatcherAssert.assertThat((T) reader.get(path), matcher);
        return this;
    }

    /**
     * Asserts that object specified by path is equal to the expected value.
     * If they are not, an AssertionError is thrown with the given message.
     *
     * @param path     the json path specifying the value being compared
     * @param expected the expected value
     * @param <T>      the static type that should be returned by the path
     * @return this to allow fluent assertion chains
     */
    public <T> JSONAsserter assertEquals(String path, T expected) {
        return assertThat(path, equalTo(expected));
    }

    /**
     * Asserts that object specified by path is null. If it is not, an AssertionError
     * is thrown with the given message.
     *
     * @param path the json path specifying the value that should be null
     * @return this to allow fluent assertion chains
     */
    public JSONAsserter assertNull(String path) {
        return assertThat(path, nullValue());
    }

    /**
     * Asserts that object specified by path is NOT null. If it is, an AssertionError
     * is thrown with the given message.
     *
     * @param path the json path specifying the value that should be NOT null
     * @return this to allow fluent assertion chains
     */
    public <T> JSONAsserter assertNotNull(String path) {
        return assertThat(path, notNullValue());
    }

    /**
     * Syntactic sugar to allow chaining assertions with a separating and() statement
     * <p/>
     * <p/>
     * <code>
     * with(json).assertThat("firstName", is(equalTo("Bobby"))).and().assertThat("lastName", is(equalTo("Ewing")))
     * </code>
     *
     * @return this to allow fluent assertion chains
     */
    public JSONAsserter and() {
        return this;
    }

}
