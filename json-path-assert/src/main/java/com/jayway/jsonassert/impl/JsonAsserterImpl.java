package com.jayway.jsonassert.impl;


import com.jayway.jsonassert.JsonAssert;
import com.jayway.jsonassert.JsonAsserter;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;
import com.jayway.jsonpath.PathNotFoundException;
import org.hamcrest.Matcher;

import static java.lang.String.format;
import static org.hamcrest.Matchers.*;

public class JsonAsserterImpl implements JsonAsserter {

    private final Object jsonObject;

    /**
     * Instantiates a new JSONAsserter
     *
     * @param jsonObject the object to make asserts on
     */
    public JsonAsserterImpl(Object jsonObject) {
        this.jsonObject = jsonObject;
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public <T> JsonAsserter assertThat(String path, Matcher<T> matcher) {
        T obj = null;
        
        try {
            obj = JsonPath.<T>read(jsonObject, path);
        } catch (Exception e) {
            final AssertionError assertionError = new AssertionError(String.format("Error reading JSON path [%s]", path));
            assertionError.initCause(e);
            throw assertionError;
        }

        if (!matcher.matches(obj)) {

            throw new AssertionError(String.format("JSON path [%s] doesn't match.\nExpected:\n%s\nActual:\n%s", path, matcher.toString(), obj));
        }
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public <T> JsonAsserter assertThat(String path, Matcher<T> matcher, String message) {
        T obj = JsonPath.<T>read(jsonObject, path);
        if (!matcher.matches(obj)) {
            throw new AssertionError(String.format("JSON Assert Error: %s\nExpected:\n%s\nActual:\n%s", message, matcher.toString(), obj));
        }
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
    public JsonAsserter assertNotDefined(String path) {
        try {
            // create a configuration and add Option.REQUIRE_PROPERTIES. Because the default behaviour without
            // this option will always not throw PathNotFoundException
            // CS304 Issue link: https://github.com/json-path/JsonPath/issues/744
            Configuration c = Configuration.defaultConfiguration().addOptions(Option.REQUIRE_PROPERTIES);
            // if path is defined in jsonObject, read() will throw PathNotFoundException
            JsonPath.using(c).parse(jsonObject).read(path);
            throw new AssertionError(format("Document contains the path <%s> but was expected not to.", path));
        } catch (PathNotFoundException e) {
        }
        return this;
    }

    @Override
    public JsonAsserter assertNotDefined(String path, String message) {
        try {
            // CS304 Issue link: https://github.com/json-path/JsonPath/issues/744
            Configuration c = Configuration.defaultConfiguration().addOptions(Option.REQUIRE_PROPERTIES);
            JsonPath.using(c).parse(jsonObject).read(path);
            throw new AssertionError(format("Document contains the path <%s> but was expected not to.", path));
        } catch (PathNotFoundException e) {
        }
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public JsonAsserter assertNull(String path) {
        return assertThat(path, nullValue());
    }

    @Override
    public JsonAsserter assertNull(String path, String message) {
        return assertThat(path, nullValue(), message);
    }

    @Override
    public <T> JsonAsserter assertEquals(String path, T expected, String message) {
        return assertThat(path, equalTo(expected),message);
    }

    /**
     * {@inheritDoc}
     */
    public <T> JsonAsserter assertNotNull(String path) {
        return assertThat(path, notNullValue());
    }

    @Override
    public <T> JsonAsserter assertNotNull(String path, String message) {
        return assertThat(path, notNullValue(), message);
    }

    /**
     * {@inheritDoc}
     */
    public JsonAsserter and() {
        return this;
    }

}
