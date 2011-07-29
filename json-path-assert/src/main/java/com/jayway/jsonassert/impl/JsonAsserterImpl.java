package com.jayway.jsonassert.impl;


import com.jayway.jsonassert.JsonAsserter;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.json.JsonElement;
import com.jayway.jsonpath.json.JsonException;

import org.hamcrest.Matcher;

import static java.lang.String.format;
import static org.hamcrest.Matchers.*;

/**
 * User: kalle stenflo
 * Date: 1/21/11
 * Time: 3:43 PM
 */
public class JsonAsserterImpl implements JsonAsserter {


    private final JsonElement jsonObject;


    /**
     * Instantiates a new JSONAsserter
     *
     * @param jsonObject the object to make asserts on
     */
    public JsonAsserterImpl(JsonElement jsonObject) {
        this.jsonObject = jsonObject;
    }


    /**
     * {@inheritDoc}
     * @throws AssertionError 
     * @throws JsonException 
     */
    @SuppressWarnings("unchecked")
    public <T> JsonAsserter assertThat(String path, Matcher<T> matcher) throws JsonException, AssertionError {

        String reason = "When processing json path: " + path;

        JsonElement je = JsonPath.read(jsonObject, path);
        if (!( (je == null && matcher.matches(je)) || (je.isContainer() && matcher.matches(je)) || matcher.matches(je.toObject()) )  ) {

            System.out.println(JsonPath.read(jsonObject, path).toString());

            throw new AssertionError(reason + matcher.toString());
        }

        /*
       if (PathUtil.isPathDefinite(path)) {
           if (!matcher.matches(JsonPath.<T>readOne(jsonObject, path))) {
               throw new AssertionError(reason + matcher.toString());
           }
           //MatcherAssert.assertThat(reason, JsonPath.<T>readOne(jsonObject, path), matcher);
       } else {
           if (!matcher.matches(JsonPath.<T>read(jsonObject, path))) {
               throw new AssertionError(reason + matcher.toString());
           }
           //MatcherAssert.assertThat(reason, (T) JsonPath.<T>read(jsonObject, path), matcher);
       } */
        return this;
    }

    /**
     * {@inheritDoc}
     * @throws AssertionError 
     * @throws JsonException 
     */
    public <T> JsonAsserter assertEquals(String path, T expected) throws JsonException, AssertionError {
        return assertThat(path, equalTo(expected));
    }

    /**
     * {@inheritDoc}
     * @throws JsonException 
     */
    public JsonAsserter assertNotDefined(String path) throws JsonException {
        JsonElement o = JsonPath.read(jsonObject, path);

        if (!o.isJsonNull()) {
            throw new AssertionError(format("Document contains the path <%s> but was expected not to.", path));
        }
        return this;
    }

    /**
     * {@inheritDoc}
     * @throws AssertionError 
     * @throws JsonException 
     */
    public JsonAsserter assertNull(String path) throws JsonException, AssertionError {
        return assertThat(path, nullValue());
    }

    /**
     * {@inheritDoc}
     * @throws AssertionError 
     * @throws JsonException 
     */
    public <T> JsonAsserter assertNotNull(String path) throws JsonException, AssertionError {
        return assertThat(path, notNullValue());
    }

    /**
     * {@inheritDoc}
     */
    public JsonAsserter and() {
        return this;
    }

}
