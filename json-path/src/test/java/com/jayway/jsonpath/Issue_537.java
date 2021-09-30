package com.jayway.jsonpath;

import org.junit.Test;

import java.util.List;

public class Issue_537 {

    public static final Configuration jsonConf = Configuration.defaultConfiguration().addOptions(Option.SUPPRESS_EXCEPTIONS);

    @Test
    public void test_read(){  // originally passed
        Object ans = JsonPath.using(jsonConf).parse("{}").read("missing");
        assert(ans == null);
    }

    @Test
    public void test_renameKey(){  // originally throws PathNotFoundException
        List<Object> ans = JsonPath.using(jsonConf)
                .parse("{\"list\":[" +
                        "{\"data\":{\"old\":1}}," +
                        "{\"data\":{}}," +
                        "{\"data\":{\"old\":2}}" +
                        "]}")
                .renameKey("$..data", "old", "new")
                .read("$.list");
        assert(ans.toString().equals("[{\"data\":{\"new\":1}},{\"data\":{}},{\"data\":{\"new\":2}}]"));
    }
}
