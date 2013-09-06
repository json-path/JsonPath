package com.jayway.jsonpath;

import org.junit.Test;

import java.util.List;
import java.util.Map;

import static junit.framework.Assert.assertEquals;

/**
 * Created by IntelliJ IDEA.
 * User: kallestenflo
 * Date: 3/14/12
 * Time: 7:30 AM
 */
public class JsonModelChainedCallsTest {
    
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
                    "      \"number\": 12,\n" +
                    "      \"dot.notation\": \"new\"\n" +
                    "    }\n" +
                    "  }\n" +
                    "}";
    
    
    @Test
    public void convert_and_map() throws Exception {
     
        JsonModel model = JsonModel.model(DOCUMENT);
        
        Transformer<Map<String, Object>> transformer = new Transformer<Map<String, Object>>() {
            public Object transform(Map<String, Object> map, Configuration configuration) {
                map.remove("isbn");
                map.put("author", "kalle");
                return map;
            }
        };

        Book book = model.opsForObject("store.book[0]").transform(transformer).to(Book.class);

        assertEquals(book.author, "kalle");
    }
    
        
    @Test
    public void convert_each_and_map() throws Exception {
     
        JsonModel model = JsonModel.model(DOCUMENT);
        
        Transformer<Object> transformer = new Transformer<Object>() {
            public Object transform(Object obj, Configuration configuration) {
                Map<String, Object> map = (Map<String, Object>) obj;
                map.remove("isbn");
                map.put("author", "kalle");
                return map;
            }
        };

        List<Book> books = model.opsForArray("store.book").each(transformer).toList().of(Book.class);

        System.out.println();
    }
    
    
    public static class Book {
        public String category;
        public String author;
        public String title;
        public Double price;
    }
    
}
