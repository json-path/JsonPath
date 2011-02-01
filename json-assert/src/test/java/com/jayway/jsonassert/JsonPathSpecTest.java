package com.jayway.jsonassert;

import com.jayway.jsonassert.impl.JsonPathImpl;
import org.junit.Test;

import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.hasItems;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

/**
 * Created by IntelliJ IDEA.
 * User: kallestenflo
 * Date: 2/1/11
 * Time: 8:25 PM
 */
public class JsonPathSpecTest {

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
                    "      \"price\": 19.95\n" +
                    "    }\n" +
                    "  }\n" +
                    "}";

    @Test
    public void the_authors_of_all_books_in_the_store() throws Exception {
        JsonPath reader = JsonPathImpl.parse(DOCUMENT);

        assertThat(reader.<String>getList("store.book[*].author"), hasItems("Nigel Rees", "Evelyn Waugh", "Herman Melville", "J. R. R. Tolkien"));
    }

    @Test
    public void all_authors() throws Exception {

        JsonPath reader = JsonPathImpl.parse(DOCUMENT);

        assertThat(reader.<String>getList("..author"), hasItems("Nigel Rees", "Evelyn Waugh", "Herman Melville", "J. R. R. Tolkien"));

    }

    @Test
    public void all_items_in_store() throws Exception {
        JsonPath reader = JsonPathImpl.parse(DOCUMENT);

        List<Object> itemsInStore = reader.<Object>getList("store.*");


        assertEquals(JsonPathImpl.jsonPath(itemsInStore, "[0][0].author"), "Nigel Rees");

        System.out.println(itemsInStore.toString());
    }

    @Test
    public void the_price_of_everything_in_the_store() throws Exception {
        JsonPath reader = JsonPathImpl.parse(DOCUMENT);

        assertThat(reader.<Double>getList("store..price"), hasItems(8.95D, 12.99D, 8.99D, 19.95D));
    }

    @Test
    public void the_third_book() throws Exception {
        JsonPath reader = JsonPathImpl.parse(DOCUMENT);

        Map<String, String> book = reader.getReader("..book[2]").get("[0]");

        assertThat(book, hasEntry("author", "Herman Melville"));


        book = reader.getMap("store.book[2]");

        assertThat(book, hasEntry("author", "Herman Melville"));
    }


}
