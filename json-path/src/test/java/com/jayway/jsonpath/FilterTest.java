package com.jayway.jsonpath;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import static com.jayway.jsonpath.Criteria.where;
import static com.jayway.jsonpath.Filter.filter;
import static java.util.Arrays.asList;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

/**
 * Created by IntelliJ IDEA.
 * User: kallestenflo
 * Date: 3/5/12
 * Time: 12:27 PM
 */
public class FilterTest {

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
    public void a_single_is_filter_can_be_built_and_applied() throws Exception {
        Map<String, Object> check = JsonPath.read(DOCUMENT, "store.book[0]");
        Filter filter = Filter.filter(Criteria.where("category").is("reference"));
        assertTrue(filter.apply(check));
    }

    @Test
    public void multiple_filters_can_be_built_and_applied() throws Exception {
        Map<String, Object> check = JsonPath.read(DOCUMENT, "store.book[0]");

        Filter filter = Filter.filter(Criteria
                .where("category").is("reference")
                .and("author").is("Nigel Rees")
                .and("price").gt(8)
                .and("price").gte(8)
                .and("price").lt(10)
                .and("price").lte(10)
                .and("title").ne("is not")
                .and("title").in("is not", "Sayings of the Century")
                .and("title").nin("is not this", "is not that")
        );
        assertTrue(filter.apply(check));
    }

    @Test
    public void all_filters_evaluates() throws Exception {
        Map<String, Object> check = new HashMap<String, Object>();
        check.put("items", asList(1, 2, 3));


        assertTrue(filter(where("items").all(1, 2, 3)).apply(check));
        assertFalse(filter(where("items").all(1, 2, 3, 4)).apply(check));
    }

    @Test
    public void size_filters_evaluates() throws Exception {
        Map<String, Object> check = new HashMap<String, Object>();
        check.put("items", asList(1, 2, 3));


        assertTrue(filter(where("items").size(3)).apply(check));
        assertFalse(filter(where("items").size(2)).apply(check));
    }

    @Test
    public void exists_filters_evaluates() throws Exception {
        Map<String, Object> check = new HashMap<String, Object>();
        check.put("foo", "foo");


        assertTrue(filter(where("foo").exists(true)).apply(check));
        assertFalse(filter(where("foo").exists(false)).apply(check));

        assertTrue(filter(where("bar").exists(false)).apply(check));
        assertFalse(filter(where("bar").exists(true)).apply(check));
    }

    @Test
    public void type_filters_evaluates() throws Exception {
        Map<String, Object> check = new HashMap<String, Object>();
        check.put("string", "foo");
        check.put("int", 1);
        check.put("long", 1L);
        check.put("double", 1.12D);


        assertTrue(filter(where("string").type(String.class)).apply(check));
        assertFalse(filter(where("string").type(Number.class)).apply(check));

        assertTrue(filter(where("int").type(Integer.class)).apply(check));
        assertFalse(filter(where("int").type(Long.class)).apply(check));

        assertTrue(filter(where("long").type(Long.class)).apply(check));
        assertFalse(filter(where("long").type(Integer.class)).apply(check));

        assertTrue(filter(where("double").type(Double.class)).apply(check));
        assertFalse(filter(where("double").type(Integer.class)).apply(check));
    }

    @Test
    public void pattern_filters_evaluates() throws Exception {
        Map<String, Object> check = new HashMap<String, Object>();
        check.put("string", "kalle");


        assertTrue(filter(where("string").regex(Pattern.compile(".alle"))).apply(check));
        assertFalse(filter(where("string").regex(Pattern.compile("KALLE"))).apply(check));
        assertTrue(filter(where("string").regex(Pattern.compile("KALLE", Pattern.CASE_INSENSITIVE))).apply(check));

    }

    /*
    @Test
    public void or_filters_evaluates() throws Exception {
        Map<String, Object> check = new HashMap<String, Object>();
        check.put("string", "kalle");
        
       
        assertTrue(filter(where("string").is("x").orOperator(where("string").is("kalle"))).apply(check));
    }*/


}
