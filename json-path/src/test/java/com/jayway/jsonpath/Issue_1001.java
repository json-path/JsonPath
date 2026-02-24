package com.jayway.jsonpath;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class Issue_1001 {
    @Test
    public void testTrailingNewlineInPath() {
        String context = "{\n" +
                "  \"level_0\": {\n" +
                "    \"level_1\": {\n" +
                "      \"level_2\": \"At level 2\"\n" +
                "    }\n" +
                "  }\n" +
                "}";
        String result = JsonPath.read(context, "$.level_0.level_1.level_2\n");
        assertThat(result).isEqualTo("At level 2");
    }
}
