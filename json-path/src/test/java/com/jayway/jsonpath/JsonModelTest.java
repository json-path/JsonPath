package com.jayway.jsonpath;

import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

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


    @Test
    public void a_model_can_be_pretty_printed() throws Exception {
        JsonModel model = JsonModel.model(DOCUMENT);

        model.print();
    }

    @Test
    public void has_path_validates() throws Exception {
        assertFalse(JsonModel.model(DOCUMENT).hasPath("store.invalid"));
        assertFalse(JsonModel.model(DOCUMENT).hasPath("store.book[0].foo"));

        assertTrue(JsonModel.model(DOCUMENT).hasPath("store.book"));
        assertTrue(JsonModel.model(DOCUMENT).hasPath("store.book[0].title"));
    }

    @Test
    public void a_json_document_can_be_fetched_with_a_URL() throws Exception {
        URL url = new URL("http://maps.googleapis.com/maps/api/geocode/json");
        assertThat("REQUEST_DENIED", equalTo(JsonModel.model(url).get("status")));
    }

    @Test
    public void a_json_document_can_be_fetched_with_a_InputStream() throws Exception {
        ByteArrayInputStream bis = new ByteArrayInputStream(DOCUMENT.getBytes());
        assertThat("Nigel Rees", equalTo(JsonModel.model(bis).get("store.book[0].author")));
    }


    @Test
    public void maps_and_list_can_queried() throws Exception {
        Map<String, Object> doc = new HashMap<String, Object>();
        doc.put("items", asList(0, 1, 2));
        doc.put("child", Collections.singletonMap("key", "value"));

        JsonModel model = JsonModel.model(doc);

        assertThat("value", equalTo(model.get("$child.key")));
        assertThat(1, equalTo(model.get("$items[1]")));
        assertThat("{\"child\":{\"key\":\"value\"},\"items\":[0,1,2]}", equalTo(model.toJson()));
    }


    @Test(expected = InvalidPathException.class)
    public void invalid_path_throws() throws Exception {
        JsonModel.model(DOCUMENT).get("store.invalid");
    }


}
