package com.jayway.jsonpath;

import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.ReadContext;
import org.junit.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class RenameFilter {

    //https://github.com/json-path/JsonPath/issues/565
    @Test
    public void issue565_1() {
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
    public void issue565_2() {
        String json = "{\n" +
                "  \"oldName\": {\n" +
                "    \"oldName\": {\n" +
                "      \"oldName\": {\n" +
                "        \"oldName\": {\n" +
                "          \"oldName\": {\n" +
                "            \"oldName\": {\n" +
                "              \"oldName\": {\n" +
                "                \"otherName\": \"value\"\n" +
                "   }\n" +
                "     }\n" +
                "       }\n" +
                "         }\n" +
                "           }\n" +
                "             }\n" +
                "               }\n" +
                "}";
        String jsonPath = "$..[?(@.oldName)]";
        String oldKeyName = "oldName";
        String newKeyName = "newName";
        ReadContext context = JsonPath.parse(json).renameKey(jsonPath, oldKeyName, newKeyName);
        List<Object> result = context.read("$..[?(@.newName)]");

        assertEquals(7, result.size());

        assertNotNull(context.read("$.newName"));
        assertNotNull(context.read("$.newName.newName"));
        assertNotNull(context.read("$.newName.newName.newName"));
    }

    //https://github.com/json-path/JsonPath/issues/399
    @Test
    public void issue399_1() {
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

    @Test
    public void issue399_2() {
        String json = "{\n" +
                "    \"student\": {\n" +
                "        \"information\": [\n" +
                "            {\n" +
                "                \"class\": \"1701\",\n" +
                "                \"teacher\": \"Wang\",\n" +
                "                \"id\": \"1701202\",\n" +
                "                \"age\": 11\n" +
                "            },\n" +
                "            {\n" +
                "                \"class\": \"1702\",\n" +
                "                \"teacher\": \"Zhang\",\n" +
                "                \"id\": \"1702232\",\n" +
                "                \"age\": 12\n" +
                "            },\n" +
                "            {\n" +
                "                \"class\": \"1703\",\n" +
                "                \"teacher\": \"Zhu\",\n" +
                "                \"id\": \"1703656\",\n" +
                "                \"age\": 13\n" +
                "            }\n" +
                "        ]\n" +
                "   }\n" +
                "}";
        ReadContext ctx = JsonPath.parse(json);
        String output = ctx.read("$.concat($.student.information[?(@.id)].id, \" - \", $.student.information[?(@.teacher)].teacher)");
        String expected = ctx.read("$.concat($.student.information[*].id, \" - \", $.student.information[*].teacher)");
        assertThat(output).isEqualTo(expected);

        double minageViaFilter = ctx.read("$.min($.student.information[?(@.age)].age)");
        double minageWildcard = ctx.read("$.min($.student.information[*].age)");
        assertThat(minageViaFilter).isEqualTo(minageWildcard);
    }
}