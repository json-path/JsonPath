package com.jayway.jsonpath;

import org.junit.Test;

public class Issue_487 {

    public static final Configuration jsonConf = Configuration.defaultConfiguration();

    //(expected = InvalidPathException.class)
    @Test
    public void test_read_with_comma_1() {
        // originally throws InvalidPathException
        DocumentContext dc = JsonPath.using(jsonConf).parse("{ \"key,\" : \"value\" }");
        Object ans = dc.read(JsonPath.compile("$['key,']"));
        //System.out.println(ans);
        assert (ans.toString().equals("value"));
    }

    @Test
    public void test_read_with_comma_2() {
        // originally passed
        DocumentContext dc = JsonPath.using(jsonConf).parse("{ \"key,\" : \"value\" }");
        Object ans = dc.read(JsonPath.compile("$['key\\,']"));
        //System.out.println(ans);
        assert (ans.toString().equals("value"));
    }
}
