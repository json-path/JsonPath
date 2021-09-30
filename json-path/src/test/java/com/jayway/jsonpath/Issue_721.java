package com.jayway.jsonpath;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;
import org.junit.Test;

public class Issue_721 {

    public static final Configuration jsonConf = Configuration.defaultConfiguration().addOptions(Option.SUPPRESS_EXCEPTIONS);

    @Test
    public void test_delete_1(){  // originally throws PathNotFoundException
        DocumentContext dc = JsonPath.using(jsonConf)
                    .parse("{\"top\": {\"middle\": null}}")
                    .delete(JsonPath.compile("$.top.middle.bottom"));
        Object ans = dc.read("$");
        //System.out.println(ans);
        assert(ans.toString().equals("{top={middle=null}}"));
    }

    @Test
    public void test_delete_2(){  // originally passed
        DocumentContext dc = JsonPath.using(jsonConf)
                .parse("[" +
                        "{\"top\": {\"middle\": null}}," +
                        "{\"top\": {\"middle\": {}  }}," +
                        "{\"top\": {\"middle\": {bottom: 2}  }}," +
                        "]")
                .delete(JsonPath.compile("$[*].top.middle.bottom"));
        Object ans = dc.read("$");
        //System.out.println(ans);
        assert(ans.toString().equals("[{\"top\":{\"middle\":null}},{\"top\":{\"middle\":{}}},{\"top\":{\"middle\":{}}}]"));
    }
}
