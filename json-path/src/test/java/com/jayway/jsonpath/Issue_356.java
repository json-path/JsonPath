package com.jayway.jsonpath;

import org.junit.Test;

import java.math.BigDecimal;
import java.util.LinkedHashMap;

import static com.jayway.jsonpath.Criteria.where;

/**
 * Test for issue 356
 */
public class Issue_356 {
    private final static String json = "{\n" +
            "  \"store\": {\n" +
            "    \"book\": [\n" +
            "      {\n" +
            "        \"title\": \"Sayings of the Century\",\n" +
            "        \"price\": {\n" +
            "          \"value\": 8.95,\n" +
            "          \"currency\": \"usd\"\n" +
            "        }\n" +
            "      },\n" +
            "      {\n" +
            "        \"title\": \"Sword of Honour\",\n" +
            "        \"price\": {\n" +
            "          \"value\": 12.99,\n" +
            "          \"currency\": \"usd\"\n" +
            "        }\n" +
            "      },\n" +
            "      {\n" +
            "        \"title\": \"Moby Dick\",\n" +
            "        \"price\": {\n" +
            "          \"value\": 8.99,\n" +
            "          \"currency\": \"usd\"\n" +
            "        }\n" +
            "      }\n" +
            "    ]\n" +
            "  }\n" +
            "}";
    @Test
    public void test1(){
        Object ans = JsonPath.parse(json).read("$..book[?]", Filter.filter(where("price.value").matches(new Predicate() {
            @Override
            public boolean apply(PredicateContext ctx) {
                // some custom logic with expecting value number in context
                return ((BigDecimal)ctx.item()).doubleValue() < 9;
            }
        })));
        // System.out.println(ans);
        assert(ans.toString().equals("[{\"title\":\"Sayings of the Century\"," +
                "\"price\":{\"value\":8.95,\"currency\":\"usd\"}}," +
                "{\"title\":\"Moby Dick\"," +
                "\"price\":{\"value\":8.99,\"currency\":\"usd\"}}]"));
    }

    @Test
    public void test2(){
        Object ans = JsonPath.parse(json).read("$..book[?]", Filter.filter(where("price").matches(new Predicate() {
            @Override
            public boolean apply(PredicateContext ctx) {
                // some custom logic with expecting value number in context
                return ((LinkedHashMap<String, Double>) ctx.item()).get("value") < 9;
            }
        })));
        // System.out.println(ans);
        assert(ans.toString().equals("[{\"title\":\"Sayings of the Century\"," +
                "\"price\":{\"value\":8.95,\"currency\":\"usd\"}}," +
                "{\"title\":\"Moby Dick\"," +
                "\"price\":{\"value\":8.99,\"currency\":\"usd\"}}]"));
    }

}
