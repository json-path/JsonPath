package com.jayway.jsonpath.old;

import com.jayway.jsonpath.JsonPath;
import org.junit.Test;

public class DocumentationPageTests {


    public static final String JSON = "{ \n" +
            "    \"store\": {\n" +
            "        \"book\": [ \n" +
            "            { \n" +
            "                \"category\": \"reference\",\n" +
            "                \"author\": \"Nigel Rees\",\n" +
            "                \"title\": \"Sayings of the Century\",\n" +
            "                \"price\": 8.95\n" +
            "            },\n" +
            "            { \n" +
            "                \"category\": \"fiction\",\n" +
            "                \"author\": \"Evelyn Waugh\",\n" +
            "                \"title\": \"Sword of Honour\",\n" +
            "                \"price\": 12.99\n" +
            "            },\n" +
            "            { \n" +
            "                \"category\": \"fiction\",\n" +
            "                \"author\": \"Herman Melville\",\n" +
            "                \"title\": \"Moby Dick\",\n" +
            "                \"isbn\": \"0-553-21311-3\",\n" +
            "                \"price\": 8.99\n" +
            "            },\n" +
            "            { \n" +
            "                \"category\": null,\n" +
            "                \"author\": \"J. R. R. Tolkien\",\n" +
            "                \"title\": \"The Lord of the Rings\",\n" +
            "                \"isbn\": \"0-395-19395-8\",\n" +
            "                \"price\": 22.99,\n" +
            "                \"edition\": {\n" +
            "                   \"release-date\": \"2013-01-22\"\n" +
            "                }\n" +
            "            }\n" +
            "        ],\n" +
            "        \"bicycle\": {\n" +
            "            \"color\": \"red\",\n" +
            "            \"price\": 19.95\n" +
            "         }\n" +
            "    }\n" +
            "}";

    @Test
    public void test_1() {
        System.out.println(JsonPath.read(JSON, "$.store.bicycle"));
    }

    @Test
    public void test_2() {
        System.out.println(JsonPath.read(JSON, "$.store.book[0]"));
    }

    @Test
    public void test_3() {
        System.out.println(JsonPath.read(JSON, "$.store.book[*].author"));
    }

    @Test
    public void test_4() {
        System.out.println(JsonPath.read(JSON, "$.store.book[-2:].author"));
    }

    @Test
    public void test_5() {
        System.out.println(JsonPath.read(JSON, "$.store.book[1:3].author"));
    }

    @Test
    public void test_6() {
        System.out.println(JsonPath.read(JSON, "$.store.book[0]['author', 'category']"));
    }

    @Test
    public void test_7() {
        System.out.println(JsonPath.read(JSON, "$..price"));
    }

}
