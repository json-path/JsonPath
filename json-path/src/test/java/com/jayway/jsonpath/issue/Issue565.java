package com.jayway.jsonpath.issue;

import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.ReadContext;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class Issue565 {

    @Test
    public void test_issue_565() {
        String json = "{\n" +
                "  \"oldName\": {\n" +
                "    \"oldName\": {\n" +
                "      \"oldName\": {\n" +
                "        \"otherName\": \"value\"\n" +
                "      }\n" +
                "    }\n" +
                "  }\n" +
                "}";
        String jsonPath = "$..[?(@.oldName)]";
        String oldKeyName = "oldName";
        String newKeyName = "newName";
        ReadContext context = JsonPath.parse(json).renameKey(jsonPath, oldKeyName, newKeyName);
        List<Object> result = context.read("$..[?(@.newName)]");

        assertEquals(3, result.size());

        assertNotNull(context.read("$.newName"));
        assertNotNull(context.read("$.newName.newName"));
        assertNotNull(context.read("$.newName.newName.newName"));
    }
}
