package com.jayway.jsonpath;

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
    public void default_mode_is_SLACK_MODE() throws Exception {
        assertEquals(JsonPath.SLACK_MODE, JsonPath.getMode());
    }

    @Test
    public void slack_mode_allows_single_quotes() throws Exception {
        assertEquals(JsonPath.read(SINGLE_QUOTE_JSON, "lhs"), "1.0 U.S. dollar");
        assertEquals(JsonPath.read(SINGLE_QUOTE_JSON, "rhs"), 6.39778892D);
    }

    @Test
    public void slack_mode_allows_no_quotes() throws Exception {
        assertEquals(JsonPath.read(NO_QUOTE_JSON, "lhs"), "1.0 U.S. dollar");
        assertEquals(JsonPath.read(NO_QUOTE_JSON, "rhs"), 6.39778892D);
    }

    @Test(expected = ParseException.class)
    public void strict_mode_does_not_accept_single_quotes() throws Exception {
    	JsonPath path = JsonPath.compile("lhs");
    	path.setMode(JsonPath.STRICT_MODE);
    	path.read(SINGLE_QUOTE_JSON);
    }

    @Test(expected = ParseException.class)
    public void strict_mode_does_not_accept_no_quotes() throws Exception {
    	JsonPath path = JsonPath.compile("lhs");
    	path.setMode(JsonPath.STRICT_MODE);
    	path.read(NO_QUOTE_JSON);
    }


}
