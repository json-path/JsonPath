package com.jayway.jsonpath;

import org.junit.Test;

import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;

/**
 * Created by IntelliJ IDEA.
 * User: kallestenflo
 * Date: 2/29/12
 * Time: 8:42 AM
 */
public class IssuesTest {
    @Test
    public void issue_7() throws Exception {

        String json = "{ \"foo\" : [\n" +
                "  { \"id\": 1 },  \n" +
                "  { \"id\": 2 },  \n" +
                "  { \"id\": 3 }\n" +
                "  ] }";


        assertNull(JsonPath.read(json, "$.foo.id"));
    }
    
    @Test
    public void issue_11() throws Exception {
        String json = "{ \"foo\" : [] }";
        List<String> result = JsonPath.read(json, "$.foo[?(@.rel= 'item')][0].uri");

        System.out.println(JsonPath.compile("$.foo[?(@.rel= 'item')][0].uri").isPathDefinite());
        System.out.println(JsonPath.compile("$.foo[?(@.rel= 'item')][0]").isPathDefinite());
        System.out.println(JsonPath.compile("$.foo[?(@.rel= 'item')]").isPathDefinite());

        assertTrue(result.isEmpty());
    }

    
    @Test
    public void issue_29_b() throws Exception {
        String json = "{\"list\": [ { \"a\":\"atext\", \"b\":{ \"b-a\":\"batext\", \"b-b\":\"bbtext\" } }, { \"a\":\"atext2\", \"b\":{ \"b-a\":\"batext2\", \"b-b\":\"bbtext2\" } } ] }";
        List<String> result = JsonPath.read(json, "$.list[?]", Filter.filter(Criteria.where("b.b-a").eq("batext2")));
       
        assertTrue(result.size() == 1);
    }

}
