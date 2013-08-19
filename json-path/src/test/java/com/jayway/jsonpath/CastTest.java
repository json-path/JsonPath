package com.jayway.jsonpath;

import org.junit.Assert;
import org.junit.Test;

/**
 * User: kalle
 * Date: 5/15/13
 * Time: 9:22 PM
 */
public class CastTest {

    public static final String JSON = "{\"sessionID\":7242750700467747000}" ;

    @Test
    public void result_can_be_cast_to_Long(){
        Long actual = JsonPath.read(JSON, "$.sessionID");
        Long expected = new Long("7242750700467747000");
        Assert.assertEquals(expected, actual);
    }
}
