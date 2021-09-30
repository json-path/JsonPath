package com.jayway.jsonassert;

import org.hamcrest.Matcher;

public interface JsonAsserter {

    /**
     * Asserts that object specified by path satisfies the condition specified by matcher.
     * If not, an AssertionError is thrown with information about the matcher
     * and failing value. Example:
     * <p/>
     * <code>
     * with(json).assertThat("items[0].name", equalTo("Bobby"))
     * .assertThat("items[0].age" , equalTo(24L))
     * </code>
     *
     * @param path    the json path specifying the value being compared
     * @param matcher an expression, built of Matchers, specifying allowed values
     * @param <T>     the static type accepted by the matcher
     * @return this to allow fluent assertion chains
     */
    <T> JsonAsserter assertThat(String path, Matcher<T> matcher);

    /**
     * @param path    the json path specifying the value being compared
     * @param matcher an expression, built of Matchers, specifying allowed values
     * @param message the explanation message
     * @param <T>     the static type that should be returned by the path
     * @return this to allow fluent assertion chains
     */
    <T> JsonAsserter assertThat(String path, Matcher<T> matcher, String message);

    /**
     * Asserts that object specified by path is equal to the expected value.
     * If they are not, an AssertionError is thrown with the given message.
     *
     * @param path     the json path specifying the value being compared
     * @param expected the expected value
     * @param <T>      the static type that should be returned by the path
     * @return this to allow fluent assertion chains
     */
    <T> JsonAsserter assertEquals(String path, T expected);

    <T> JsonAsserter assertEquals(String path, T expected, String message);

    /**
     * Checks that a path is not defined within a document. If the document contains the
     * given path, an AssertionError is thrown
     *
     * @param path the path to make sure not exists
     * @return this
     */
    JsonAsserter assertNotDefined(String path);

    JsonAsserter assertNotDefined(String path, String message);


    /**
     * Asserts that object specified by path is null. If it is not, an AssertionError
     * is thrown with the given message.
     *
     * @param path the json path specifying the value that should be null
     * @return this to allow fluent assertion chains
     */
    JsonAsserter assertNull(String path);
    JsonAsserter assertNull(String path, String message);

    /**
     * Asserts that object specified by path is NOT null. If it is, an AssertionError
     * is thrown with the given message.
     *
     * @param path the json path specifying the value that should be NOT null
     * @return this to allow fluent assertion chains
     */
    <T> JsonAsserter assertNotNull(String path);

    <T> JsonAsserter assertNotNull(String path, String message);

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
    JsonAsserter and();
}
