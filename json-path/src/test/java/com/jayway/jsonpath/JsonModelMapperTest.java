package com.jayway.jsonpath;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.junit.Test;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;

/**
 * Created by IntelliJ IDEA.
 * User: kallestenflo
 * Date: 3/2/12
 * Time: 10:50 AM
 */
public class JsonModelMapperTest {
    
    public final static String DOCUMENT =
                "{ \"store\": {\n" +
                        "    \"book\": [ \n" +
                        "      { \"category\": \"reference\",\n" +
                        "        \"author\": \"Nigel Rees\",\n" +
                        "        \"title\": \"Sayings of the Century\",\n" +
                        "        \"displayPrice\": 8.95\n" +
                        "      },\n" +
                        "      { \"category\": \"fiction\",\n" +
                        "        \"author\": \"Evelyn Waugh\",\n" +
                        "        \"title\": \"Sword of Honour\",\n" +
                        "        \"displayPrice\": 12.99\n" +
                        "      },\n" +
                        "      { \"category\": \"fiction\",\n" +
                        "        \"author\": \"Herman Melville\",\n" +
                        "        \"title\": \"Moby Dick\",\n" +
                        "        \"isbn\": \"0-553-21311-3\",\n" +
                        "        \"displayPrice\": 8.99\n" +
                        "      },\n" +
                        "      { \"category\": \"fiction\",\n" +
                        "        \"author\": \"J. R. R. Tolkien\",\n" +
                        "        \"title\": \"The Lord of the Rings\",\n" +
                        "        \"isbn\": \"0-395-19395-8\",\n" +
                        "        \"displayPrice\": 22.99\n" +
                        "      }\n" +
                        "    ],\n" +
                        "    \"bicycle\": {\n" +
                        "      \"color\": \"red\",\n" +
                        "      \"displayPrice\": 19.95,\n" +
                        "      \"foo:bar\": \"fooBar\",\n" +
                        "      \"dot.notation\": \"new\",\n" +
    	                "      \"dash-notation\": \"dashes\"\n" +
                        "    }\n" +
                        "  }\n" +
                        "}";


    @Test
    public void map_a_json_model() throws Exception {

        JsonModel model = JsonModel.create(DOCUMENT);

        List<Book> booksList = model.map("$.store.book[0,1]").toListOf(Book.class);

        Set<Book> bookSet = model.map("$.store.book[0,1]").toSetOf(Book.class);

        Book book = model.map("$.store.book[1]").to(Book.class);

        System.out.println("test");

    }

    @Test
    public void a_book_can_be_mapped() throws Exception {


        //JsonPath.convert(DOCUMENT, "$.store.book[0,1]", List.class).to()

        //List books = JsonPath.read(DOCUMENT, "$.store.book[0,1]", List.class);

        ObjectMapper objectMapper = new ObjectMapper();


        /*


//Standard
List<Map> res = JsonPath.read(DOCUMENT, "$.store.book[0,1]");
//or
List<Map> res = JsonPath.read(DOCUMENT, "$.store.book[0,1]", List.class);


//POJO Mapping med jackson ObjectMapper
List<Book> res =  JsonPath.asList().of(Book.class).read(DOCUMENT, "$.store.book[0,1]");

List<Book> res =  JsonPath.asListOf(Book.class).read(DOCUMENT, "$.store.book[0,1]");

Book res = JsonPath.as(Book.class).read(DOCUMENT, "$.store.book[0]");

         */
    }
    
    public static class Book {
        private String category;
        private String author;
        private String title;
        private Double displayPrice;

        public Book() {
        }

        public String getCategory() {
            return category;
        }

        public void setCategory(String category) {
            this.category = category;
        }

        public String getAuthor() {
            return author;
        }

        public void setAuthor(String author) {
            this.author = author;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public Double getDisplayPrice() {
            return displayPrice;
        }

        public void setDisplayPrice(Double displayPrice) {
            this.displayPrice = displayPrice;
        }
    }
    
}
    


