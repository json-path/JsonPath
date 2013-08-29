package com.jayway.jsonpath;

import org.junit.Ignore;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static java.util.Arrays.asList;
import static junit.framework.Assert.*;

/**
 * Created by IntelliJ IDEA.
 * User: kallestenflo
 * Date: 11/8/11
 * Time: 7:52 PM
 */
public class JsonModelTest {

    public final static String DOCUMENT =
            "{ \"store\": {\n" +
                    "    \"book\": [ \n" +
                    "      { \"category\": \"reference\",\n" +
                    "        \"author\": \"Nigel Rees\",\n" +
                    "        \"title\": \"Sayings of the Century\",\n" +
                    "        \"price\": 8.95\n" +
                    "      },\n" +
                    "      { \"category\": \"fiction\",\n" +
                    "        \"author\": \"Evelyn Waugh\",\n" +
                    "        \"title\": \"Sword of Honour\",\n" +
                    "        \"price\": 12.99\n" +
                    "      },\n" +
                    "      { \"category\": \"fiction\",\n" +
                    "        \"author\": \"Herman Melville\",\n" +
                    "        \"title\": \"Moby Dick\",\n" +
                    "        \"isbn\": \"0-553-21311-3\",\n" +
                    "        \"price\": 8.99\n" +
                    "      },\n" +
                    "      { \"category\": \"fiction\",\n" +
                    "        \"author\": \"J. R. R. Tolkien\",\n" +
                    "        \"title\": \"The Lord of the Rings\",\n" +
                    "        \"isbn\": \"0-395-19395-8\",\n" +
                    "        \"price\": 22.99\n" +
                    "      }\n" +
                    "    ],\n" +
                    "    \"bicycle\": {\n" +
                    "      \"color\": \"red\",\n" +
                    "      \"price\": 19.95,\n" +
                    "      \"foo:bar\": \"fooBar\",\n" +
                    "      \"dot.notation\": \"new\"\n" +
                    "    }\n" +
                    "  }\n" +
                    "}";

    public final static String INVALID_DOCUMENT = "{?\\?\\?!!?~q`}}}}}\"\" \"store\": {\n";

    @Test(expected = InvalidJsonException.class)
    public void invalid_json_throws() throws Exception {
        JsonModel.model(INVALID_DOCUMENT).get("store.id");
    }

    @Test(expected = InvalidPathException.class)
    public void invalid_path_throws() throws Exception {
        JsonModel.model(DOCUMENT).get("a(");
    }


    @Test
    public void a_model_can_be_pretty_printed() throws Exception {
        JsonModel model = JsonModel.model(DOCUMENT);

        model.print();
    }

    @Test
    @Ignore //TODO: finalize behaviour
    public void has_path_validates() throws Exception {
        assertFalse(JsonModel.model(DOCUMENT).hasPath("store.invalid"));
        assertFalse(JsonModel.model(DOCUMENT).hasPath("store.book[0].foo"));

        assertTrue(JsonModel.model(DOCUMENT).hasPath("store.book"));
        assertTrue(JsonModel.model(DOCUMENT).hasPath("store.book[0].title"));
    }

    @Test
    public void a_json_document_can_be_fetched_with_a_URL() throws Exception {
        URL url = new URL("http://maps.googleapis.com/maps/api/geocode/json");
        assertEquals("REQUEST_DENIED", JsonModel.model(url).get("status"));
    }

    @Test
    public void a_json_document_can_be_fetched_with_a_InputStream() throws Exception {
        ByteArrayInputStream bis = new ByteArrayInputStream(DOCUMENT.getBytes());
        assertEquals("Nigel Rees", JsonModel.model(bis).get("store.book[0].author"));
    }


    @Test
    public void maps_and_list_can_queried() throws Exception {
        Map<String, Object> doc = new HashMap<String, Object>();
        doc.put("items", asList(0, 1, 2));
        doc.put("child", Collections.singletonMap("key", "value"));

        JsonModel model = JsonModel.model(doc);

        assertEquals("value", model.get("$child.key"));
        assertEquals(1, model.get("$items[1]"));
        assertEquals("{\"child\":{\"key\":\"value\"},\"items\":[0,1,2]}", model.toJson());
    }



    @Test
    public void query_for_null_property_returns_null() {
        String documentWithNull =
                "{ \"store\": {\n" +
                        "    \"book\": { \n" +
                        "      \"color\": null\n" +
                        "    }\n" +
                        "  }\n" +
                        "}";

        Object color = JsonModel.model(documentWithNull).get("store.book.color");

        assertNull(color);
    }

    @Test(expected = InvalidPathException.class)
    public void query_for_property_on_array_throws() throws Exception {
        JsonModel.model(DOCUMENT).get("store.book.color");
    }

}
