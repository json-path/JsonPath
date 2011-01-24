package com.jayway.jsonassert;

import com.jayway.jsonassert.impl.JSONAsserterImpl;

import java.text.ParseException;

/**
 * Created by IntelliJ IDEA.
 * User: kallestenflo
 * Date: 1/24/11
 * Time: 9:31 PM
 */
public class JSONAssert {

    /**
     * Creates a JSONAsserter instance that can be used to make
     * assertions on the provided JSON document or array.
     *
     * @param json the JSON document to create a JSONAsserter for
     * @return a JSON asserter initialized with the provided document
     * @throws ParseException when the given JSON could not be parsed
     */
    public static JSONAsserter with(String json) throws ParseException {
        return JSONAsserterImpl.with(json);
    }

}
