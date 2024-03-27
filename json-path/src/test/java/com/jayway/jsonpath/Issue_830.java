package com.jayway.jsonpath;

import net.minidev.json.JSONArray;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class Issue_830 {

    private final String json = "{\n" +
            "    \"some\": {\n" +
            "        \"nested\": {\n" +
            "            \"path\": [\n" +
            "                {\n" +
            "                    \"id\": 1,\n" +
            "                    \"name\": \"one\",\n" +
            "                    \"data\": {\n" +
            "                        \"field\": \"value\"\n" +
            "                    }\n" +
            "                },\n" +
            "                {\n" +
            "                    \"id\": 2,\n" +
            "                    \"name\": \"two\",\n" +
            "                    \"data\": {\n" +
            "                        \"needlessly\": {\n" +
            "                            \"nested\": {\n" +
            "                                \"field\": \"value\"\n" +
            "                            }\n" +
            "                        }\n" +
            "                    }\n" +
            "                }\n" +
            "            ]\n" +
            "        }\n" +
            "    }\n" +
            "}";

    @Test
    public void testFeature_1() {
        InputStream inputStream = new ByteArrayInputStream(json.getBytes());
        DocumentContext documentContext = JsonPath.parse(inputStream);
        JSONArray items = documentContext.read("$['some']['nested']['path'][*]");
        List<DocumentContext> contextList = JsonPath.parse(items);
        List<String> ans = new ArrayList<>();
        for (DocumentContext context: contextList) {
            ans.add(context.read("$['data']").toString());
        }
        assertThat(ans.toString()).isEqualTo("[{field=value}, {needlessly={nested={field=value}}}]");
    }

    @Test
    public void testFeature_2() {
        InputStream inputStream = new ByteArrayInputStream(json.getBytes());
        DocumentContext documentContext = JsonPath.parse(inputStream);
        JSONArray items = documentContext.read("$['some']['nested']['path'][*]");
        List<DocumentContext> contextList = JsonPath.parse(items);
        List<String> ans = new ArrayList<>();
        for (DocumentContext context: contextList) {
            ans.add(context.read("$['name']").toString());
        }
        assertThat(ans.toString()).isEqualTo("[one, two]");
    }
}
