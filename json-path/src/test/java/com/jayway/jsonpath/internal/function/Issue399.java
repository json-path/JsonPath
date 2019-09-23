package com.jayway.jsonpath.internal.function;

import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.ReadContext;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test for Issue #399
 *
 * Issue#399 states that, if there is a predicate in function call statement, for example $.concat($.store.book[?(@.title)].title,
 * parsing operation fails due to the unhandled case of filter expressions when parsing parameters of function.
 * Test case verifies that this issue has been solved.
 */
public class Issue399 {

    private String json = "{\n" +
            "    \"store\": {\n" +
            "        \"book\": [\n" +
            "            {\n" +
            "                \"category\": \"reference\",\n" +
            "                \"author\": \"Nigel Rees\",\n" +
            "                \"title\": \"Sayings of the Century\",\n" +
            "                \"price\": 8.95\n" +
            "            },\n" +
            "            {\n" +
            "                \"category\": \"fiction\",\n" +
            "                \"author\": \"Evelyn Waugh\",\n" +
            "                \"title\": \"Sword of Honour\",\n" +
            "                \"price\": 12.99\n" +
            "            },\n" +
            "            {\n" +
            "                \"category\": \"fiction\",\n" +
            "                \"author\": \"Herman Melville\",\n" +
            "                \"title\": \"Moby Dick\",\n" +
            "                \"price\": 8.99\n" +
            "            }\n" +
            "        ]\n" +
            "   }\n" +
            "}";

    @Test
    public void testIssue399() {
        ReadContext ctx = JsonPath.parse(json);
        String output = ctx.read("$.concat($.store.book[?(@.title)].title, \" - \", $.store.book[?(@.author)].author)");
        String expected = ctx.read("$.concat($.store.book[*].title, \" - \", $.store.book[*].author)");
        assertThat(output).isEqualTo(expected);

        double minPriceViaFilter = ctx.read("$.min($.store.book[?(@.price)].price)");
        double minPriceWildcard = ctx.read("$.min($.store.book[*].price)");
        assertThat(minPriceViaFilter).isEqualTo(minPriceWildcard);
    }
}
