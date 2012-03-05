package com.jayway.jsonpath;

import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static junit.framework.Assert.assertEquals;

/**
 * Created by IntelliJ IDEA.
 * User: kallestenflo
 * Date: 3/4/12
 * Time: 4:55 PM
 */
public class JsonModelOpsTest {

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
    public void object_ops_can_update() throws Exception {

        JsonModel model = JsonModel.create(DOCUMENT);

        model.opsForObject("store.book[0]")
                .put("author", "Kalle")
                .put("price", 12.30D);

        assertEquals("Kalle", model.get("store.book[0].author"));
        assertEquals(12.30D, model.get("store.book[0].price"));
    }
    
    
    @Test
    public void array_ops_can_add_element() throws Exception {
        JsonModel model = JsonModel.create(DOCUMENT);
        
        Map<String, Object> newBook = new HashMap<String, Object>();
        newBook.put("category", "reference");
        newBook.put("author", "Kalle");
        newBook.put("title", "JSONPath book");
        newBook.put("isbn", "0-553-21311-34");
        newBook.put("price", 12.10D);

        model.opsForArray("store.book").add(newBook);

        JsonModel subModel = model.getModel("store.book[4]");

        assertEquals("reference", subModel.get("category"));
        assertEquals("Kalle", subModel.get("author"));
        assertEquals("JSONPath book", subModel.get("title"));
        assertEquals("0-553-21311-34", subModel.get("isbn"));
        assertEquals(12.10D, subModel.get("price"));
    }

    @Test
    public void arrays_can_be_mapped() throws Exception {
        JsonModel model = JsonModel.create(DOCUMENT);

        List<Book> books1 = model.opsForArray("store.book").toList().of(Book.class);
        List<Book> books2 = model.opsForArray("store.book").toListOf(Book.class);
        Set<Book> books3 = model.opsForArray("store.book").toSetOf(Book.class);

        assertEquals(4, books1.size());
        assertEquals(4, books2.size());
        assertEquals(4, books3.size());
    }

    @Test
    public void objects_can_be_mapped() throws Exception {
        JsonModel model = JsonModel.create(DOCUMENT);

        Book book = model.opsForObject("store.book[1]").to(Book.class);

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
