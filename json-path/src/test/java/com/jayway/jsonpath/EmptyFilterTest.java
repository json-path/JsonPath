package com.jayway.jsonpath;

import org.junit.Assert;
import org.junit.Test;

/**
 * test for issue 900
 */
public class EmptyFilterTest {

    @Test
    public void test() {
        String json = "{\n" +
                "  \"data1\": {\n" +
                "    \"data\": [\n" +
                "      {\n" +
                "        \"attribute1\": \"string1\",\n" +
                "        \"attribute2\": \"string2\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"attribute1\": \"string3\",\n" +
                "        \"attribute2\": \"string4\"\n" +
                "      }\n" +
                "    ]\n" +
                "  },\n" +
                "  \"data2\": {\n" +
                "    \"data\": [\n" +
                "      {}\n" +
                "    ]\n" +
                "  },\n" +
                "  \"data3\": {\n" +
                "    \"data\": [\n" +
                "      {\n" +
                "        \"attribute1\": \"string5\",\n" +
                "        \"attribute2\": \"string6\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"attribute1\": \"string7\",\n" +
                "        \"attribute2\": \"string8\"\n" +
                "      }\n" +
                "    ]\n" +
                "  }\n" +
                "}";
        JsonPath jsonPath = JsonPath.compile("$..data.*[?(@ empty false)]");
        Object jsonPathValue = jsonPath.read(json);
        Assert.assertNotNull(jsonPathValue);
    }
}
