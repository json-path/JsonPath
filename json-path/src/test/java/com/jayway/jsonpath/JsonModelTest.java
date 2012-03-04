package com.jayway.jsonpath;

import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.net.URL;
import java.util.*;

import static java.util.Arrays.asList;
import static junit.framework.Assert.assertEquals;

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
    public void a_json_document_can_be_fetched_with_a_URL() throws Exception {
        URL url = new URL("http://maps.googleapis.com/maps/api/geocode/json");
        assertEquals("REQUEST_DENIED", JsonModel.create(url).get("status"));
    }

    @Test
    public void a_json_document_can_be_fetched_with_a_InputStream() throws Exception {
        ByteArrayInputStream bis = new ByteArrayInputStream(DOCUMENT.getBytes());
        assertEquals("Nigel Rees", JsonModel.create(bis).get("store.book[0].author"));
    }

    @Test
    public void test_a_sub_model_can_be_fetched_and_read() throws Exception {
        JsonModel model = JsonModel.create(DOCUMENT);
        assertEquals("Nigel Rees", model.getSubModel("$store.book[0]").get("author"));
        assertEquals("Nigel Rees", model.getSubModel(JsonPath.compile("$store.book[0]")).get("author"));
    }

    @Test
    public void maps_and_list_can_queried() throws Exception {
        Map<String, Object> doc = new HashMap<String, Object>();
        doc.put("items", asList(0, 1, 2));
        doc.put("child", Collections.singletonMap("key", "value"));

        JsonModel model = JsonModel.create(doc);

        assertEquals("value", model.get("$child.key"));
        assertEquals(1, model.get("$items[1]"));
        assertEquals("{\"child\":{\"key\":\"value\"},\"items\":[0,1,2]}", model.toJson());
    }

    @Test
    public void map_a_json_model_to_an_Class() throws Exception {

        JsonModel model = JsonModel.create(DOCUMENT);

        Book book = model.map("$.store.book[1]").to(Book.class);

        assertEquals("fiction", book.category);
        assertEquals("Evelyn Waugh", book.author);
        assertEquals("Sword of Honour", book.title);
        assertEquals(12.99D, book.price);
    }

    @Test
    public void map_a_json_model_to_a_List() throws Exception {
        JsonModel model = JsonModel.create(DOCUMENT);

        List<Book> booksList = model.map("$.store.book[0,1]").toListOf(Book.class);

        assertEquals("fiction", booksList.get(1).category);
        assertEquals("Evelyn Waugh", booksList.get(1).author);
        assertEquals("Sword of Honour", booksList.get(1).title);
        assertEquals(12.99D, booksList.get(1).price);

        booksList = model.map("$.store.book[*]").toListOf(Book.class);

        assertEquals("fiction", booksList.get(1).category);
        assertEquals("Evelyn Waugh", booksList.get(1).author);
        assertEquals("Sword of Honour", booksList.get(1).title);
        assertEquals(12.99D, booksList.get(1).price);

        booksList = model.map("$.store.book[*]").toList().of(Book.class);

        assertEquals("fiction", booksList.get(1).category);
        assertEquals("Evelyn Waugh", booksList.get(1).author);
        assertEquals("Sword of Honour", booksList.get(1).title);
        assertEquals(12.99D, booksList.get(1).price);
    }

    @Test
    public void map_a_json_model_to_a_Set() throws Exception {

        JsonModel model = JsonModel.create(DOCUMENT);

        Set<Book> bookSet = model.map("$.store.book[1]").toSetOf(Book.class);

        Book book = bookSet.iterator().next();

        assertEquals("fiction", book.category);
        assertEquals("Evelyn Waugh", book.author);
        assertEquals("Sword of Honour", book.title);
        assertEquals(12.99D, book.price);
    }


    public static class Book {
        public String category;
        public String author;
        public String title;
        public String isbn;
        public Double price;
    }


}
