package com.jayway.jsonpath.internal.function;

import net.minidev.json.JSONArray;
import com.jayway.jsonpath.JsonPath;
import org.junit.Test;


import static org.junit.Assert.assertEquals;

public class Issue273 {
    @Test
    public void testGetPropertyFromArray(){
        String json = "[\n" +
                "   [\n" +
                "      {\n" +
                "         \"category\" : \"reference\",\n" +
                "         \"author\" : \"Nigel Rees\",\n" +
                "         \"title\" : \"Sayings of the Century\",\n" +
                "         \"price\" : 8.95\n" +
                "      },\n" +
                "      {\n" +
                "         \"category\" : \"fiction\",\n" +
                "         \"author\" : \"Evelyn Waugh\",\n" +
                "         \"title\" : \"Sword of Honour\",\n" +
                "         \"price\" : 12.99\n" +
                "      },\n" +
                "      {\n" +
                "         \"category\" : \"fiction\",\n" +
                "         \"author\" : \"Herman Melville\",\n" +
                "         \"title\" : \"Moby Dick\",\n" +
                "         \"isbn\" : \"0-553-21311-3\",\n" +
                "         \"price\" : 8.99\n" +
                "      },\n" +
                "      {\n" +
                "         \"category\" : \"fiction\",\n" +
                "         \"author\" : \"J. R. R. Tolkien\",\n" +
                "         \"title\" : \"The Lord of the Rings\",\n" +
                "         \"isbn\" : \"0-395-19395-8\",\n" +
                "         \"price\" : 22.99\n" +
                "      }\n" +
                "   ],\n" +
                "   {\n" +
                "      \"color\" : \"red\",\n" +
                "      \"price\" : 19.95\n" +
                "   }\n" +
                "]\n";

        JSONArray arr = JsonPath.read(json,"$..[2].author");
        assertEquals(arr.get(0), "Herman Melville");
    }

    @Test
    public void testGetPropertyFromObject(){
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
                "}\n" +
                "                ";
        JSONArray arr = JsonPath.read(json,"$..[2].author");
        assertEquals(arr.get(0), "Herman Melville");
    }
}
