package com.jayway.jsonpath;

import org.junit.Test;

import java.util.List;
import java.util.Set;

import static com.jayway.jsonpath.Criteria.where;
import static com.jayway.jsonpath.Filter.filter;
import static com.jayway.jsonpath.JsonModel.model;
import static junit.framework.Assert.assertEquals;

/**
 * Created by IntelliJ IDEA.
 * User: kallestenflo
 * Date: 3/4/12
 * Time: 8:36 PM
 */
public class JsonModelMappingTest {

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
    public void map_and_filter_can_be_combined() throws Exception {
     
        
        JsonModel model = JsonModel.model(DOCUMENT);
        
        Filter filter = Filter.filter(Criteria.where("category").is("fiction").and("price").gt(10D));

        List<Book> books = model.map("$.store.book[?]", filter).toList().of(Book.class);


        JsonPath jsonPath = JsonPath.compile("$.store.book[?]", filter(where("category").is("fiction").and("price").gt(10D)));

        Object read = jsonPath.read(DOCUMENT);

        List<Book> books1 = JsonModel.model(DOCUMENT).map("$.store.book[?]", filter).toListOf(Book.class);

        model(DOCUMENT);


        System.out.println("");
        
    }

    @Test
    public void map_a_json_model_to_an_Class() throws Exception {

        JsonModel model = JsonModel.model(DOCUMENT);

        Book book = model.map("$.store.book[1]").to(Book.class);

        assertEquals("fiction", book.category);
        assertEquals("Evelyn Waugh", book.author);
        assertEquals("Sword of Honour", book.title);
        assertEquals(12.99D, book.price);
    }

    @Test
    public void map_a_json_model_to_a_List() throws Exception {
        JsonModel model = JsonModel.model(DOCUMENT);

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

        JsonModel model = JsonModel.model(DOCUMENT);

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
