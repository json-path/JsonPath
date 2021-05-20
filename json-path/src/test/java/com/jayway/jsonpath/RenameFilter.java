package com.jayway.jsonpath;

import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.ReadContext;
import org.junit.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class RenameFilter {

    @Test
    //https://github.com/json-path/JsonPath/issues/565
    public void issue565() {
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

    @Test
    //https://github.com/json-path/JsonPath/issues/399
    public void issue399() {
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
                "                \"price\": 8.99\n" +
                "            }\n" +
                "        ]\n" +
                "   }\n" +
                "}";

        ReadContext ctx = JsonPath.parse(json);
        String output = ctx.read("$.concat($.store.book[?(@.title)].title, \" - \", $.store.book[?(@.author)].author)");
        String expected = ctx.read("$.concat($.store.book[*].title, \" - \", $.store.book[*].author)");
        assertThat(output).isEqualTo(expected);

        double minPriceViaFilter = ctx.read("$.min($.store.book[?(@.price)].price)");
        double minPriceWildcard = ctx.read("$.min($.store.book[*].price)");
        assertThat(minPriceViaFilter).isEqualTo(minPriceWildcard);
    }
}