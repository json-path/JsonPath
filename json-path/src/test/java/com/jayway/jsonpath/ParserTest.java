package com.jayway.jsonpath;

import org.junit.Ignore;
import org.junit.Test;

import java.text.ParseException;

import static junit.framework.Assert.assertEquals;

/**
 * Created by IntelliJ IDEA.
 * User: kallestenflo
 * Date: 6/25/11
 * Time: 4:16 PM
 */

public class ParserTest {


    private final static String SINGLE_QUOTE_JSON = "{'lhs': '1.0 U.S. dollar','rhs': 6.39778892, 'error': null, 'icc': true}";
    private final static String NO_QUOTE_JSON = "{lhs: 1.0 U.S. dollar, rhs: 6.39778892, error: null, icc: true}";

    @Test
    @Ignore
    public void default_mode_is_SLACK_MODE() throws Exception {
    //    assertEquals(JsonPath.SLACK_MODE, JsonPath.getMode());
    }

    @Test
    @Ignore
    public void slack_mode_allows_single_quotes() throws Exception {
        assertEquals(JsonPath.read(SINGLE_QUOTE_JSON, "lhs").toPrimitive(), "1.0 U.S. dollar");
        assertEquals(JsonPath.read(SINGLE_QUOTE_JSON, "rhs").toPrimitive(), 6.39778892D);
    }

    @Test
    @Ignore
    public void slack_mode_allows_no_quotes() throws Exception {
        assertEquals(JsonPath.read(NO_QUOTE_JSON, "lhs").getWrappedElement(), "1.0 U.S. dollar");
        assertEquals(JsonPath.read(NO_QUOTE_JSON, "rhs").getWrappedElement(), 6.39778892D);
    }



}
