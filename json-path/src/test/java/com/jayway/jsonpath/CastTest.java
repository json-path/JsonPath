package com.jayway.jsonpath;

import org.junit.Assert;
import org.junit.Test;

public class CastTest {

    public static final String JSON = "{\"sessionID\":7242750700467747000}" ;

    @Test
    public void result_can_be_cast_to_Long(){
        Long actual = JsonPath.read(JSON, "$.sessionID");
        Long expected = new Long("7242750700467747000");
        Assert.assertEquals(expected, actual);
    }
}
