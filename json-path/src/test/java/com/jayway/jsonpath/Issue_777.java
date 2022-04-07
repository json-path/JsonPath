package com.jayway.jsonpath;

import org.junit.Test;

public class Issue_777 {
    public static final Configuration jsonConf = Configuration.defaultConfiguration();

    @Test
    public void test_01_nested_path_in_filter_value() {
        String json = "{\n" +
                "    \"store\": {\n" +
                "        \"book\": [\n" +
                "            {\n" +
                "                \"category\": \"reference\",\n" +
                "                \"author\": \"Nigel Rees\",\n" +
                "                \"title\": \"Sayings of the Century\",\n" +
                "                \"price\": 8.95\n" +
                "            },\n" +
                "            {\n" +
                "                \"category\": \"fiction\",\n" +
                "                \"author\": \"Evelyn Waugh\",\n" +
                "                \"title\": \"Sword of Honour\",\n" +
                "                \"price\": 12.99\n" +
                "            },\n" +
                "            {\n" +
                "                \"category\": \"fiction\",\n" +
                "                \"author\": \"Herman Melville\",\n" +
                "                \"title\": \"Moby Dick\",\n" +
                "                \"isbn\": \"0-553-21311-3\",\n" +
                "                \"price\": 8.99\n" +
                "            },\n" +
                "            {\n" +
                "                \"category\": \"fiction\",\n" +
                "                \"author\": \"J. R. R. Tolkien\",\n" +
                "                \"title\": \"The Lord of the Rings\",\n" +
                "                \"isbn\": \"0-395-19395-8\",\n" +
                "                \"price\": 22.99\n" +
                "            }\n" +
                "        ],\n" +
                "        \"bicycle\": {\n" +
                "            \"color\": \"red\",\n" +
                "            \"price\": 19.95\n" +
                "        }\n" +
                "    },\n" +
                "    \"expensive\": 10\n" +
                "}";
        DocumentContext dc = JsonPath.using(jsonConf).parse(json);
        String result = dc.read("$.store.book[?(@.price == $.max($.store.book[*].price))].author").toString();
        System.out.println(result);
    }

    @Test
    public void test_02_nested_path_in_filter_value() {
        String json = "{\"list\": [{\"val\": 1, \"name\": \"val=1\"}, {\"val\": 2, \"name\": \"val=2\"}, {\"val\": 3,\"name\": \"val=3\"}]}";
        DocumentContext dc = JsonPath.using(jsonConf).parse(json);
        String result = dc.read("$.list[?(@.val == $.max($.list[*].val))].name").toString();
        System.out.println(result);
    }
}
