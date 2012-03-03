package com.jayway.jsonpath;

import com.jayway.jsonpath.util.ScriptEngineJsonPath;
import org.junit.Test;

import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItems;
import static org.junit.Assert.*;
import static org.junit.Assert.assertThat;

/**
 * Created by IntelliJ IDEA.
 * User: kallestenflo
 * Date: 2/2/11
 * Time: 3:07 PM
 */
public class JsonPathTest {

    public final static String ARRAY = "[{\"value\": 1},{\"value\": 2}, {\"value\": 3},{\"value\": 4}]";

    public final static String DOCUMENT =
            "{ \"store\": {\n" +
                    "    \"book\": [ \n" +
                    "      { \"category\": \"reference\",\n" +
                    "        \"author\": \"Nigel Rees\",\n" +
                    "        \"title\": \"Sayings of the Century\",\n" +
                    "        \"display-price\": 8.95\n" +
                    "      },\n" +
                    "      { \"category\": \"fiction\",\n" +
                    "        \"author\": \"Evelyn Waugh\",\n" +
                    "        \"title\": \"Sword of Honour\",\n" +
                    "        \"display-price\": 12.99\n" +
                    "      },\n" +
                    "      { \"category\": \"fiction\",\n" +
                    "        \"author\": \"Herman Melville\",\n" +
                    "        \"title\": \"Moby Dick\",\n" +
                    "        \"isbn\": \"0-553-21311-3\",\n" +
                    "        \"display-price\": 8.99\n" +
                    "      },\n" +
                    "      { \"category\": \"fiction\",\n" +
                    "        \"author\": \"J. R. R. Tolkien\",\n" +
                    "        \"title\": \"The Lord of the Rings\",\n" +
                    "        \"isbn\": \"0-395-19395-8\",\n" +
                    "        \"display-price\": 22.99\n" +
                    "      }\n" +
                    "    ],\n" +
                    "    \"bicycle\": {\n" +
                    "      \"color\": \"red\",\n" +
                    "      \"display-price\": 19.95,\n" +
                    "      \"foo:bar\": \"fooBar\",\n" +
                    "      \"dot.notation\": \"new\",\n" +
	                "      \"dash-notation\": \"dashes\"\n" +
                    "    }\n" +
                    "  }\n" +
                    "}";


    private final static String PRODUCT_JSON = "{\n" +
            "\t\"product\": [ {\n" +
            "\t    \"version\": \"A\", \n" +
            "\t    \"codename\": \"Seattle\", \n" +
            "\t    \"attr.with.dot\": \"A\"\n" +
            "\t},\n" +
            "\t{\n" +
            "\t    \"version\": \"4.0\", \n" +
            "\t    \"codename\": \"Montreal\", \n" +
            "\t    \"attr.with.dot\": \"B\"\n" +
            "\t}]\n" +
            "}";

    private final static String ARRAY_EXPAND = "[{\"parent\": \"ONE\", \"child\": {\"name\": \"NAME_ONE\"}}, [{\"parent\": \"TWO\", \"child\": {\"name\": \"NAME_TWO\"}}]]";


    @Test
    public void array_start_expands() throws Exception {
        //assertThat(JsonPath.<List<String>>read(ARRAY_EXPAND, "$[?(@.parent = 'ONE')].child.name"), hasItems("NAME_ONE"));
        assertThat(JsonPath.<List<String>>read(ARRAY_EXPAND, "$[?(@['parent'] = 'ONE')].child.name"), hasItems("NAME_ONE"));
    }

    @Test
    public void bracket_notation_can_be_used_in_path() throws Exception {

        //System.out.println(ScriptEngineJsonPath.eval(DOCUMENT, "$.['store'].['bicycle'].['dot.notation']"));
        System.out.println(ScriptEngineJsonPath.eval(DOCUMENT, "$.store.bicycle.['dot.notation']"));


        assertEquals("new", JsonPath.read(DOCUMENT, "$.['store'].bicycle.['dot.notation']"));
        assertEquals("new", JsonPath.read(DOCUMENT, "$['store']['bicycle']['dot.notation']"));
        assertEquals("new", JsonPath.read(DOCUMENT, "$.['store']['bicycle']['dot.notation']"));
        assertEquals("new", JsonPath.read(DOCUMENT, "$.['store'].['bicycle'].['dot.notation']"));

	    System.out.println(ScriptEngineJsonPath.eval(DOCUMENT, "$.store.bicycle.['dash-notation']"));

	    assertEquals("dashes", JsonPath.read(DOCUMENT, "$.['store'].bicycle.['dash-notation']"));
	    assertEquals("dashes", JsonPath.read(DOCUMENT, "$['store']['bicycle']['dash-notation']"));
	    assertEquals("dashes", JsonPath.read(DOCUMENT, "$.['store']['bicycle']['dash-notation']"));
	    assertEquals("dashes", JsonPath.read(DOCUMENT, "$.['store'].['bicycle'].['dash-notation']"));
    }

    @Test
    public void filter_an_array() throws Exception {
        List<Object> matches = JsonPath.read(ARRAY, "$.[?(@.value = 1)]");

        assertEquals(1, matches.size());
        System.out.println(matches);
    }

    @Test
    public void filter_an_array_on_index() throws Exception {
        Integer matches = JsonPath.read(ARRAY, "$.[1].value");

        assertEquals(new Integer(2), matches);
        System.out.println(matches);
    }

    @Test
    public void read_path_with_colon() throws Exception {

        assertEquals(JsonPath.read(DOCUMENT, "$.store.bicycle.foo:bar"), "fooBar");
        assertEquals(JsonPath.read(DOCUMENT, "$['store']['bicycle']['foo:bar']"), "fooBar");
    }

    @Test
    public void read_document_from_root() throws Exception {

        Map result = JsonPath.read(DOCUMENT, "$.store");

        assertEquals(2, result.values().size());
    }

    @Test
    public void read_store_book_1() throws Exception {

        JsonPath path = JsonPath.compile("$.store.book[1]");

        Map map = path.read(DOCUMENT);

        assertEquals("Evelyn Waugh", map.get("author"));
    }

    @Test
    public void read_store_book_wildcard() throws Exception {
        JsonPath path = JsonPath.compile("$.store.book[*]");

        List<Object> list = path.read(DOCUMENT);

    }

    @Test
    public void read_store_book_author() throws Exception {
        assertThat(JsonPath.<List<String>>read(DOCUMENT, "$.store.book[0,1].author"), hasItems("Nigel Rees", "Evelyn Waugh"));
        assertThat(JsonPath.<List<String>>read(DOCUMENT, "$.store.book[*].author"), hasItems("Nigel Rees", "Evelyn Waugh", "Herman Melville", "J. R. R. Tolkien"));
        assertThat(JsonPath.<List<String>>read(DOCUMENT, "$.['store'].['book'][*].['author']"), hasItems("Nigel Rees", "Evelyn Waugh", "Herman Melville", "J. R. R. Tolkien"));
        assertThat(JsonPath.<List<String>>read(DOCUMENT, "$['store']['book'][*]['author']"), hasItems("Nigel Rees", "Evelyn Waugh", "Herman Melville", "J. R. R. Tolkien"));
        assertThat(JsonPath.<List<String>>read(DOCUMENT, "$['store'].book[*]['author']"), hasItems("Nigel Rees", "Evelyn Waugh", "Herman Melville", "J. R. R. Tolkien"));
    }

    @Test
    public void all_authors() throws Exception {
        assertThat(JsonPath.<List<String>>read(DOCUMENT, "$..author"), hasItems("Nigel Rees", "Evelyn Waugh", "Herman Melville", "J. R. R. Tolkien"));
    }


    @Test
    public void all_store_properties() throws Exception {
        List<Object> itemsInStore = JsonPath.read(DOCUMENT, "$.store.*");

        assertEquals(JsonPath.read(itemsInStore, "$.[0].[0].author"), "Nigel Rees");
        assertEquals(JsonPath.read(itemsInStore, "$.[0][0].author"), "Nigel Rees");
    }

    @Test
    public void all_prices_in_store() throws Exception {

        assertThat(JsonPath.<List<Double>>read(DOCUMENT, "$.store..['display-price']"), hasItems(8.95D, 12.99D, 8.99D, 19.95D));

    }

    @Test
    public void access_array_by_index_from_tail() throws Exception {

        assertThat(JsonPath.<String>read(DOCUMENT, "$..book[(@.length-1)].author"), equalTo("J. R. R. Tolkien"));
        assertThat(JsonPath.<String>read(DOCUMENT, "$..book[-1:].author"), equalTo("J. R. R. Tolkien"));
    }

    @Test
    public void read_store_book_index_0_and_1() throws Exception {

        assertThat(JsonPath.<List<String>>read(DOCUMENT, "$.store.book[0,1].author"), hasItems("Nigel Rees", "Evelyn Waugh"));
        assertTrue(JsonPath.<List>read(DOCUMENT, "$.store.book[0,1].author").size() == 2);
    }

    @Test
    public void read_store_book_pull_first_2() throws Exception {

        assertThat(JsonPath.<List<String>>read(DOCUMENT, "$.store.book[:2].author"), hasItems("Nigel Rees", "Evelyn Waugh"));
        assertTrue(JsonPath.<List>read(DOCUMENT, "$.store.book[:2].author").size() == 2);
    }


    @Test
    public void read_store_book_filter_by_isbn() throws Exception {

        assertThat(JsonPath.<List<String>>read(DOCUMENT, "$.store.book[?(@.isbn)].isbn"), hasItems("0-553-21311-3", "0-395-19395-8"));
        assertTrue(JsonPath.<List>read(DOCUMENT, "$.store.book[?(@.isbn)].isbn").size() == 2);
	    assertTrue(JsonPath.<List>read(DOCUMENT, "$.store.book[?(@['isbn'])].isbn").size() == 2);
    }

    @Test
    public void all_books_cheaper_than_10() throws Exception {


        assertThat(JsonPath.<List<String>>read(DOCUMENT, "$..book[?(@['display-price'] < 10)].title"), hasItems("Sayings of the Century", "Moby Dick"));
        assertThat(JsonPath.<List<String>>read(DOCUMENT, "$..book[?(@.display-price < 10)].title"), hasItems("Sayings of the Century", "Moby Dick"));

    }

        @Test
    public void all_books() throws Exception {

            //List<String> books = JsonPath.<List<String>>read(DOCUMENT, "$..book");
            Object books = JsonPath.<List<String>>read(DOCUMENT, "$..book");

            System.out.println("test");

        }

    @Test
    public void dot_in_predicate_works() throws Exception {

        assertThat(JsonPath.<List<String>>read(PRODUCT_JSON, "$.product[?(@.version='4.0')].codename"), hasItems("Montreal"));

    }

    @Test
    public void dots_in_predicate_works() throws Exception {

        assertThat(JsonPath.<List<String>>read(PRODUCT_JSON, "$.product[?(@.attr.with.dot='A')].codename"), hasItems("Seattle"));

    }

    @Test
    public void all_books_with_category_reference() throws Exception {

        assertThat(JsonPath.<List<String>>read(DOCUMENT, "$..book[?(@.category='reference')].title"), hasItems("Sayings of the Century"));
        assertThat(JsonPath.<List<String>>read(DOCUMENT, "$.store.book[?(@.category='reference')].title"), hasItems("Sayings of the Century"));

    }

    @Test
    public void all_members_of_all_documents() throws Exception {
        List<String> all = JsonPath.read(DOCUMENT, "$..*");
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void access_index_out_of_bounds_does_not_throw_exception() throws Exception {

        Object res = JsonPath.read(DOCUMENT, "$.store.book[100].author");

    }



}
