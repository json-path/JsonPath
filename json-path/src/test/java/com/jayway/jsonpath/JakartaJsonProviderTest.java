package com.jayway.jsonpath;

import org.junit.Test;

import jakarta.json.JsonArray;
import jakarta.json.JsonObject;

import static com.jayway.jsonpath.JsonPath.using;
import static org.assertj.core.api.Assertions.assertThat;

public class JakartaJsonProviderTest extends BaseTest {


    @Test
    public void an_object_can_be_read() {

        JsonObject book = using(JSON_ORG_CONFIGURATION).parse(JSON_DOCUMENT).read("$.store.book[0]");

        assertThat(book.get("author").toString()).isEqualTo("Nigel Rees");
    }

    @Test
    public void a_property_can_be_read() {

        String category = using(JSON_ORG_CONFIGURATION).parse(JSON_DOCUMENT).read("$.store.book[0].category");

        assertThat(category).isEqualTo("reference");
    }

    @Test
    public void a_filter_can_be_applied() {

    	JsonArray fictionBooks = using(JSON_ORG_CONFIGURATION).parse(JSON_DOCUMENT).read("$.store.book[?(@.category == 'fiction')]");

        assertThat(fictionBooks.size()).isEqualTo(3);
    }

    @Test
    public void read_books_with_isbn() {

    	JsonArray books = using(JSON_ORG_CONFIGURATION).parse(JSON_DOCUMENT).read("$..book[?(@.isbn)]");

        assertThat(books.size()).isEqualTo(2);
    }

    /**
     * Functions take parameters, the length parameter for example takes an entire document which we anticipate
     * will compute to a document that is an array of elements which can determine its length.
     *
     * Since we translate this query from $..books.length() to length($..books) verify that this particular translation
     * works as anticipated.
     */
    @Test
    public void read_book_length_using_translated_query() {
        Integer result = using(Configuration.defaultConfiguration())
                .parse(JSON_BOOK_STORE_DOCUMENT)
                .read("$..book.length()");
        assertThat(result).isEqualTo(4);
    }

    @Test
    public void read_book_length() {
        Object result = using(Configuration.defaultConfiguration())
                .parse(JSON_BOOK_STORE_DOCUMENT)
                .read("$.length($..book)");
        assertThat(result).isEqualTo(4);
    }

}
