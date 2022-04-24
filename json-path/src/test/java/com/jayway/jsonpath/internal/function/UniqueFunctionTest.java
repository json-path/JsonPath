package com.jayway.jsonpath.internal.function;

import com.jayway.jsonpath.JsonPath;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * test for unique function
 */
public class UniqueFunctionTest {

    private static String testJson = "{\n" +
            "    \"items\": [\n" +
            "    {\n" +
            "      \"Id\": 1\n" +
            "    },\n" +
            "    {\n" +
            "      \"Name\": 2,\n" +
            "      \"Id\": 1\n" +
            "    },\n" +
            "    {\n" +
            "      \"Id\": 1,\n" +
            "      \"Name\": 2\n" +
            "    },\n" +
            "    {\n" +
            "      \"Id\": 2\n" +
            "    }\n" +
            "]\n" +
            "}";

    @Test
    public void testWithoutUnique(){
        assertThat(JsonPath.read(testJson, "$..items.[*].Id").toString()).isEqualTo("[1,1,1,2]");
        assertThat(JsonPath.read(testJson, "$..items.[*]").toString()).isEqualTo("[{\"Id\":1},{\"Name\":2,\"Id\":1},{\"Id\":1,\"Name\":2},{\"Id\":2}]");

    }

    @Test
    public void testWithUnique(){
        assertThat(JsonPath.read(testJson, "$..items.[*].Id.unique()").toString()).isEqualTo("[1,2]");
        assertThat(JsonPath.read(testJson, "$..items.[*].unique()").toString()).isEqualTo("[{\"Id\":1},{\"Name\":2,\"Id\":1},{\"Id\":2}]");

    }


}
