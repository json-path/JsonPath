package com.jayway.jsonassert.impl;


import com.jayway.jsonassert.JsonAsserter;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathUtil;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.MatcherAssert;

import java.text.ParseException;
import java.util.List;

import static org.hamcrest.Matchers.*;

/**
 * User: kalle stenflo
 * Date: 1/21/11
 * Time: 3:43 PM
 */
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

        String reason = "When processing json path: " + path;

        if(PathUtil.isPathDefinite(path)){
            MatcherAssert.assertThat(reason, JsonPath.<T>readOne(jsonObject, path), matcher);
        }
        else {
           MatcherAssert.assertThat(reason, (T) JsonPath.<T>read(jsonObject, path), matcher);
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
