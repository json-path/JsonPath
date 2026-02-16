// CS304 (manually written)
// Issue link: https://github.com/json-path/JsonPath/issues/356
package com.jayway.jsonpath;

import org.junit.Test;

import java.math.BigDecimal;
import java.util.LinkedHashMap;

import static com.jayway.jsonpath.Criteria.where;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test for issue 356
 */
public class Issue_356 { //NOPMD - suppressed AtLeastOneConstructor //NOPMD - suppressed ClassNamingConventions
    /**
     * The json data for testing
     */
    private static final String JSON = "{\n"
            + "  \"store\": {\n"
            + "    \"book\": [\n"
            + "      {\n"
            + "        \"title\": \"Sayings of the Century\",\n"
            + "        \"price\": {\n"
            + "          \"value\": 8.95,\n"
            + "          \"currency\": \"usd\"\n"
            + "       }\n"
            + "      },\n"
            + "      {\n"
            + "        \"title\": \"Sword of Honour\",\n"
            + "        \"price\": {\n"
            + "          \"value\": 12.99,\n"
            + "          \"currency\": \"usd\"\n"
            + "        }\n"
            + "      },\n"
            + "      {\n"
            + "        \"title\": \"Moby Dick\",\n"
            + "        \"price\": {\n"
            + "          \"value\": 8.99,\n"
            + "          \"currency\": \"usd\"\n"
            + "        }\n"
            + "      }\n"
            + "    ]\n"
            + "  }\n"
            + "}";
    /**
     * The constraint for testing
     */
    private static final double CONSTRAINT = 9;

    /**
     * test1 for "...price.value" PredicateContext,
     * it will return the book with the price value less than 9
     */
    @Test
    public void test1() {
        final Object ans = JsonPath.parse(JSON).read("$..book[?]", Filter.filter(where("price.value").matches(new Predicate() { //NOPMD - suppressed DataflowAnomalyAnalysis
            @Override
            public boolean apply(final PredicateContext ctx) {
                // some custom logic with expecting value number in context
                return ((BigDecimal) ctx.item()).doubleValue() < CONSTRAINT;
            }
        })));
        assertThat(ans.toString().equals("[{\"title\":\"Sayings of the Century\"," //NOPMD - suppressed LawOfDemeter
                + "\"price\":{\"value\":8.95,\"currency\":\"usd\"}},"
                + "{\"title\":\"Moby Dick\","
                + "\"price\":{\"value\":8.99,\"currency\":\"usd\"}}]")).isTrue();
    }


    /**
     * test2 for "...price" PredicateContext,
     * it will return the book with the price value less than 9
     */
    @Test
    public void test2() {
        final Object ans = JsonPath.parse(JSON).read("$..book[?]", Filter.filter(where("price").matches(new Predicate() { //NOPMD - suppressed DataflowAnomalyAnalysis
            @Override
            public boolean apply(final PredicateContext ctx) { //NOPMD - suppressed CommentRequired
                // some custom logic with expecting value number in context
                return ((LinkedHashMap<String, Double>) ctx.item()).get("value") < CONSTRAINT;
            }
        })));
        assertThat(ans.toString().equals("[{\"title\":\"Sayings of the Century\"," //NOPMD - suppressed LawOfDemeter
                + "\"price\":{\"value\":8.95,\"currency\":\"usd\"}},"
                + "{\"title\":\"Moby Dick\","
                + "\"price\":{\"value\":8.99,\"currency\":\"usd\"}}]")).isTrue();
    }

}
