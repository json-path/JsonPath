package com.jayway.jsonpath.old.internal;

import com.jayway.jsonpath.Configuration;

/**
 *
 */
public class TestBase {


    public final static Object ARRAY = Configuration.defaultConfiguration().jsonProvider().parse("[" +
            "{\n" +
            "   \"foo\" : \"foo-val-0\"\n" +
            "}," +
            "{\n" +
            "   \"foo\" : \"foo-val-1\"\n" +
            "}," +
            "{\n" +
            "   \"foo\" : \"foo-val-2\"\n" +
            "}," +
            "{\n" +
            "   \"foo\" : \"foo-val-3\"\n" +
            "}," +
            "{\n" +
            "   \"foo\" : \"foo-val-4\"\n" +
            "}," +
            "{\n" +
            "   \"foo\" : \"foo-val-5\"\n" +
            "}," +
            "{\n" +
            "   \"foo\" : \"foo-val-6\"\n" +
            "}" +
            "]");

    public final static Object DOC = Configuration.defaultConfiguration().jsonProvider().parse(
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
                    "}");



    public final static Configuration CONF = Configuration.defaultConfiguration();

}
