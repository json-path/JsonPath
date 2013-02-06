package com.jayway.jsonpath;

import org.junit.Test;

import java.util.List;

import static org.hamcrest.Matchers.hasItems;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

public class HelpTest {

    public static final String JSON = "{\n" +
            "   \"destination_addresses\" : [\n" +
            "      \"Rua do Dom António José Cordeiro 48, 3800-012 Aveiro, Portugal\",\n" +
            "      \"Rua do Dom António José Cordeiro 48, 3800-012 Aveiro, Portugal\"\n" +
            "   ],\n" +
            "   \"origin_addresses\" : [ \"N109, 3800, Portugal\" ],\n" +
            "   \"rows\" : [\n" +
            "      {\n" +
            "         \"elements\" : [\n" +
            "            {\n" +
            "               \"distance\" : {\n" +
            "                  \"text\" : \"0.4 km\",\n" +
            "                  \"value\" : 427\n" +
            "               },\n" +
            "               \"duration\" : {\n" +
            "                  \"text\" : \"1 min\",\n" +
            "                  \"value\" : 58\n" +
            "               },\n" +
            "               \"status\" : \"OK\"\n" +
            "            },\n" +
            "            {\n" +
            "               \"distance\" : {\n" +
            "                  \"text\" : \"5.0 km\",\n" +
            "                  \"value\" : 427\n" +
            "               },\n" +
            "               \"duration\" : {\n" +
            "                  \"text\" : \"1 min\",\n" +
            "                  \"value\" : 58\n" +
            "               },\n" +
            "               \"status\" : \"OK\"\n" +
            "            }\n" +
            "         ]\n" +
            "      }\n" +
            "   ],\n" +
            "   \"status\" : \"OK\"\n" +
            "}";

    private static final String JSON2 = "{\n" +
            "    \"error\": null,\n" +
            "    \"contents\": [\n" +
            "        {\n" +
            "            \"groupType\": \"series\",\n" +
            "             \"instanceId\": \"grp://15\",\n" +
            "             \"id\": \"prg://16\",\n" +
            "            \"type\": \"group\",\n" +
            "            \"media\": [\n" +
            "                {\n" +
            "                    \"classification\": \"urn:1.2.3\",\n" +
            "                    \"uri\": \"http://yahoo.com/1.png\",\n" +
            "                    \"mimeType\": \"application/octet-stream\"\n" +
            "                }\n" +
            "            ]\n" +
            "        }\n" +
            "    ],\n" +
            "    \"header\": {\n" +
            "        \"total\": 1\n" +
            "    }\n" +
            "}";


    @Test
    public void sample_one_x(){
        System.out.println(JsonPath.read(JSON2, "$.['error', 'header']"));
        System.out.println(JsonPath.read(JSON2, "$.contents[*].['groupType', 'type']"));
    }

    @Test
    public void sample_one(){
        List<String> addresses = JsonPath.read(JSON, "$.destination_addresses[*]");
        assertThat(addresses, hasItems("Rua do Dom António José Cordeiro 48, 3800-012 Aveiro, Portugal"));
    }

    @Test
    public void sample_two(){
        String text = JsonPath.read(JSON, "$.rows[0].elements[1].distance.text");
        assertEquals("5.0 km", text);
    }

    @Test
    public void sample_two_b(){
        String text = JsonPath.read(JSON, "$.rows[0].elements[1].distance.text");
        assertEquals("5.0 km", text);
    }

    @Test
    public void sample_three(){
        List<String> allDistanceTexts = JsonPath.read(JSON, "$.rows[0].elements[*].distance.text");
        assertThat(allDistanceTexts, hasItems("0.4 km", "5.0 km"));
    }
}
