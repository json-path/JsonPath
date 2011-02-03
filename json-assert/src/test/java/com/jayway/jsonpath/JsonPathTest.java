package com.jayway.jsonpath;

import org.apache.commons.lang.StringUtils;
import org.junit.Test;

import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.hasItems;
import static org.junit.Assert.*;

/**
 * Created by IntelliJ IDEA.
 * User: kallestenflo
 * Date: 2/2/11
 * Time: 3:07 PM
 */
public class JsonPathTest {

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
    public void read_document_from_root() throws Exception {

        List<Object> list = JsonPath.read(DOCUMENT, "$.store");

        assertEquals(2, ((Map)list.get(0)).values().size());


    }

    @Test
    public void read_store_book_1() throws Exception {


        JsonPath path = JsonPath.compile("$.store.book[1]");

        List<Object> list = path.read(DOCUMENT);

        System.out.println(list.toString());

    }

    @Test
    public void read_store_book_wildcard() throws Exception {
        JsonPath path = JsonPath.compile("$.store.book[*]");

        List<Object> list = path.read(DOCUMENT);

        System.out.println(list.toString());
    }

    @Test
    public void read_store_book_author() throws Exception {
        assertThat(JsonPath.<String>read(DOCUMENT, "$.store.book[*].author"), hasItems("Nigel Rees", "Evelyn Waugh", "Herman Melville", "J. R. R. Tolkien"));
    }


    @Test
    public void all_authors() throws Exception {
        assertThat(JsonPath.<String>read(DOCUMENT, "$..author"), hasItems("Nigel Rees", "Evelyn Waugh", "Herman Melville", "J. R. R. Tolkien"));
    }


    @Test
    public void all_store_properties() throws Exception {
        List<Object> itemsInStore = JsonPath.read(DOCUMENT, "$.store.*");

        assertEquals(JsonPath.readOne(itemsInStore, "$.[0].[0].author"), "Nigel Rees");
        assertEquals(JsonPath.readOne(itemsInStore, "$.[0][0].author"), "Nigel Rees");
    }

    @Test
    public void all_prices_in_store() throws Exception {

        assertThat(JsonPath.<Double>read(DOCUMENT, "$.store..price"), hasItems(8.95D, 12.99D, 8.99D, 19.95D));

    }

    @Test
    public void access_array_by_index_from_tail() throws Exception {

        assertThat(JsonPath.<String>readOne(DOCUMENT, "$..book[(@.length-1)].author"), equalTo("J. R. R. Tolkien"));
        assertThat(JsonPath.<String>readOne(DOCUMENT, "$..book[-1:].author"), equalTo("J. R. R. Tolkien"));
    }

    @Test
    public void read_store_book_index_0_and_1() throws Exception {

        assertThat(JsonPath.<String>read(DOCUMENT, "$.store.book[0,1].author"), hasItems("Nigel Rees", "Evelyn Waugh"));
        assertTrue(JsonPath.<String>read(DOCUMENT, "$.store.book[0,1].author").size() == 2);
    }

    @Test
    public void read_store_book_pull_first_2() throws Exception {

        assertThat(JsonPath.<String>read(DOCUMENT, "$.store.book[:2].author"), hasItems("Nigel Rees", "Evelyn Waugh"));
        assertTrue(JsonPath.<String>read(DOCUMENT, "$.store.book[:2].author").size() == 2);
    }

    @Test
    public void read_store_book_filter_by_isbn() throws Exception {

        assertThat(JsonPath.<String>read(DOCUMENT, "$.store.book[?(@.isbn)].isbn"), hasItems("0-553-21311-3", "0-395-19395-8"));
        assertTrue(JsonPath.<String>read(DOCUMENT, "$.store.book[?(@.isbn)].isbn").size() == 2);
    }

    @Test
    public void all_members_of_all_documents() throws Exception {

        List<String> all = JsonPath.read(DOCUMENT, "$..*");

        System.out.println(StringUtils.join(all, "\n"));
        System.out.println(all.toString());

    }
}
