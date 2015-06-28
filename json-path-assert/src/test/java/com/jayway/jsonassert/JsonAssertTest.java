package com.jayway.jsonassert;

import org.junit.Test;

import java.io.InputStream;

import static com.jayway.jsonassert.JsonAssert.*;
import static org.hamcrest.Matchers.*;

public class JsonAssertTest {

    public final static String JSON =
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
                    "      \"price\": 19.95\n," +
                    "      \"gears\": [23, 50]\n," +
                    "      \"extra\": {\"x\": 0}\n," +
                    "      \"escape\" : \"Esc\\b\\f\\n\\r\\t\\u002A\",\n" +
                    "      \"nullValue\": null\n" +
                    "    }\n" +
                    "  }\n" +
                    "}";

    @Test
    public void invalid_path() throws Exception {
        with(JSON).assertThat("$.store.book[*].fooBar", emptyCollection());
    }


    @Test(expected = AssertionError.class)
    public void has_path() throws Exception {

        with(JSON).assertNotDefined("$.store.bicycle[?(@.color == 'red' )]");
    }

    @Test
    public void assert_gears() throws Exception {
        with(JSON).assertThat("$.store.bicycle[?(@.gears == [23, 50])]", is(collectionWithSize(equalTo(1))));
        with(JSON).assertThat("$.store.bicycle[?(@.gears == [23, 77])]", is(collectionWithSize(equalTo(0))));
        with(JSON).assertThat("$.store.bicycle[?(@.extra == {\"x\":0})]", is(collectionWithSize(equalTo(1))));
        with(JSON).assertThat("$.store.bicycle[?(@.escape == 'Esc\\b\\f\\n\\r\\t\\u002A')]", is(collectionWithSize(equalTo(1))));
    }

    @Test(expected = AssertionError.class)
    public void failed_error_message() throws Exception {

        with(JSON).assertThat("$.store.book[0].category", endsWith("foobar"));
    }

    @Test
    public void links_document() throws Exception {

        with(getResourceAsStream("links.json")).assertEquals("count", 2)
                .assertThat("links['gc:this']href", endsWith("?pageNumber=1&pageSize=2"))
                .assertNotDefined("links['gc:prev']")
                .assertNotDefined("links['gc:next']")
                .assertThat("rows", collectionWithSize(equalTo(2)));

    }

    @Test
    public void a_document_can_be_expected_not_to_contain_a_path() throws Exception {
        with(JSON).assertNotDefined("$.store.bicycle.cool");
    }

    @Test
    public void a_value_can_asserted_to_be_null() throws Exception {
        with(JSON).assertNull("$.store.bicycle.nullValue");
    }

    @Test
    public void ends_with_evalueates() throws Exception {
        with(JSON).assertThat("$.store.book[0].category", endsWith("nce"));
    }

    @Test
    public void a_path_can_be_asserted_with_matcher() throws Exception {

        with(JSON).assertThat("$.store.bicycle.color", equalTo("red"))
                .assertThat("$.store.bicycle.price", equalTo(19.95D));
    }

    @Test
    public void list_content_can_be_asserted_with_matcher() throws Exception {

        with(JSON).assertThat("$..book[*].author", hasItems("Nigel Rees", "Evelyn Waugh", "Herman Melville", "J. R. R. Tolkien"));

        with(JSON).assertThat("$..author", hasItems("Nigel Rees", "Evelyn Waugh", "Herman Melville", "J. R. R. Tolkien"))
                .assertThat("$..author", is(collectionWithSize(equalTo(4))));
    }

    @Test
    public void list_content_can_be_asserted_with_nested_matcher() throws Exception {
        with(JSON).assertThat("$..book[*]", hasItems(hasEntry("author", "Nigel Rees"), hasEntry("author", "Evelyn Waugh")));
    }

    @Test
    public void map_content_can_be_asserted_with_matcher() throws Exception {

        with(JSON).assertThat("$.store.book[0]", hasEntry("category", "reference"))
                .assertThat("$.store.book[0]", hasEntry("title", "Sayings of the Century"))
                .and()
                .assertThat("$..book[0]", is(collectionWithSize(equalTo(1))))
                .and()
                .assertThat("$.store.book[0]", mapContainingKey(equalTo("category")))
                .and()
                .assertThat("$.store.book[0]", mapContainingValue(equalTo("reference")));

        with(JSON).assertThat("$.['store'].['book'][0]", hasEntry("category", "reference"))
                .assertThat("$.['store'].['book'][0]", hasEntry("title", "Sayings of the Century"))
                .and()
                .assertThat("$..['book'][0]", is(collectionWithSize(equalTo(1))))
                .and()
                .assertThat("$.['store'].['book'][0]", mapContainingKey(equalTo("category")))
                .and()
                .assertThat("$.['store'].['book'][0]", mapContainingValue(equalTo("reference")));
    }

    @Test
    public void an_empty_collection() throws Exception {
        with(JSON).assertThat("$.store.book[?(@.category == 'x')]", emptyCollection());
    }

    @Test
    public void a_path_can_be_asserted_equal_to() throws Exception {

        with(JSON).assertEquals("$.store.book[0].title", "Sayings of the Century")
                .assertThat("$.store.book[0].title", equalTo("Sayings of the Century"));

        with(JSON).assertEquals("$['store']['book'][0].['title']", "Sayings of the Century")
                .assertThat("$['store'].book[0].title", equalTo("Sayings of the Century"));
    }

    @Test
    public void path_including_wildcard_path_followed_by_another_path_concatenates_results_to_list() throws Exception {
        with(getResourceAsStream("lotto.json")).assertThat("lotto.winners[*].winnerId", hasItems(23, 54));
    }

    @Test
    public void testNotDefined() throws Exception {
        JsonAsserter asserter = JsonAssert.with("{\"foo\":\"bar\"}");
        asserter.assertNotDefined("$.xxx");
    }


    @Test(expected = AssertionError.class)
    public void assert_that_invalid_path_is_thrown() {

        JsonAsserter asserter = JsonAssert.with("{\"foo\":\"bar\"}");
        asserter.assertEquals("$foo", "bar");
    }
    @Test
    public void testAssertEqualsInteger() throws Exception {
        with(getResourceAsStream("lotto.json")).assertEquals("lotto.winners[0].winnerId", 23);
    }

    @Test(expected = AssertionError.class)
    public void testAssertEqualsIntegerInvalidExpected() throws Exception {
        with(getResourceAsStream("lotto.json")).assertEquals("lotto.winners[0].winnerId", 24);
    }

    @Test(expected = AssertionError.class)
    public void testAssertEqualsIntegerInvalidField() throws Exception {
        with(getResourceAsStream("lotto.json")).assertEquals("lotto.winners[0].winnerId1", 24);
    }

    private InputStream getResourceAsStream(String resourceName) {
        return getClass().getClassLoader().getResourceAsStream(resourceName);
    }

}
