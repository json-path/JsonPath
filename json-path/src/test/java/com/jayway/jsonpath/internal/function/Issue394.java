package com.jayway.jsonpath.internal.function;

import net.minidev.json.JSONArray;
import com.jayway.jsonpath.JsonPath;
import org.junit.Test;
import java.io.InputStream;
import java.util.LinkedHashMap;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class Issue394 {

    @Test
    public void testGetParentNodeByChildNodeFromStream(){
        InputStream stream = ClassLoader.getSystemResourceAsStream("issue_394.json");

        JSONArray arr = JsonPath.parse(stream).read("$.street.store.book[?(@.authors[?(@.lastName == 'Waugh')])]");

        assertEquals(arr.size(),1);
    }

    @Test
    public void testGetParentNodeByChildNodeFromJson(){
        String json = "{\n" +
                "  \"parent\": {\n" +
                "    \"children\": [\n{\n" +
                "        \"index\": \"0\",\n" +
                "        \"name\": \"A\"\n},\n{\n" +
                "        \"index\": \"1\",\n" +
                "        \"name\": \"B\"\n},\n{\n" +
                "        \"index\": \"2\",\n" +
                "        \"name\": \"C\"\n}\n]\n}\n}";

        JSONArray arr = JsonPath.read(json,"$.parent[?(@.children[?(@.index == '2')])]");

        assertNotNull(((LinkedHashMap) arr.get(0)).get("children"));
    }
}
