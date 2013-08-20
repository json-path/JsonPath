package com.jayway.jsonpath;

import org.junit.Test;

import java.util.List;
import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;


/**
 * User: kalle
 * Date: 8/20/13
 * Time: 8:03 AM
 */
public class MultiAttributeTest {

    public final static String DOCUMENT =
            "{ \"store\": {\n" +
                    "    \"book\": [ \n" +
                    "      { \"category\": \"reference\",\n" +
                    "        \"author\": \"Nigel Rees\",\n" +
                    "        \"title\": \"Sayings of the Century\",\n" +
                    "        \"display-price\": 8.95\n" +
                    "      },\n" +
                    "      { \"category\": \"fiction\",\n" +
                    "        \"author\": \"Evelyn Waugh\",\n" +
                    "        \"title\": \"Sword of Honour\",\n" +
                    "        \"display-price\": 12.99\n" +
                    "      },\n" +
                    "      { \"category\": \"fiction\",\n" +
                    "        \"author\": \"Herman Melville\",\n" +
                    "        \"title\": \"Moby Dick\",\n" +
                    "        \"isbn\": \"0-553-21311-3\",\n" +
                    "        \"display-price\": 8.99\n" +
                    "      },\n" +
                    "      { \"category\": \"fiction\",\n" +
                    "        \"author\": \"J. R. R. Tolkien\",\n" +
                    "        \"title\": \"The Lord of the Rings\",\n" +
                    "        \"isbn\": \"0-395-19395-8\",\n" +
                    "        \"display-price\": 22.99\n" +
                    "      }\n" +
                    "    ],\n" +
                    "    \"bicycle\": {\n" +
                    "      \"color\": \"red\",\n" +
                    "      \"display-price\": 19.95,\n" +
                    "      \"foo:bar\": \"fooBar\",\n" +
                    "      \"dot.notation\": \"new\",\n" +
                    "      \"dash-notation\": \"dashes\"\n" +
                    "    }\n" +
                    "  }\n" +
                    "}";


    @Test
    public void multiple_attributes_from_array_content(){
        List<Map<String, Object>> matches = JsonPath.read(DOCUMENT, "$.store.book[*].['category', 'title']");

        assertEquals(4, matches.size());
        assertTrue(matches.get(1).containsKey("category"));
        assertTrue(matches.get(1).containsKey("title"));
        assertEquals(2, matches.get(1).size());
    }

    @Test
    public void multiple_attributes_from_single_object(){
        Map<String, Object> match = JsonPath.read(DOCUMENT, "$.store.bicycle['color', 'display-price']");

        assertTrue(match.containsKey("color"));
        assertTrue(match.containsKey("display-price"));
    }

}
