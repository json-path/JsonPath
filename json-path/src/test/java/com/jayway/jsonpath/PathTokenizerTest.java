package com.jayway.jsonpath;

import com.jayway.jsonpath.reader.PathToken;
import com.jayway.jsonpath.reader.PathTokenizer;
import com.jayway.jsonpath.spi.JsonProvider;
import com.jayway.jsonpath.spi.impl.DefaultJsonProvider;
import org.junit.Test;

import java.util.List;

import static junit.framework.Assert.assertEquals;
import static org.hamcrest.Matchers.hasItems;
import static org.junit.Assert.assertThat;

/**
 * Created by IntelliJ IDEA.
 * User: kallestenflo
 * Date: 11/4/11
 * Time: 10:44 PM
 */
public class PathTokenizerTest {

    private JsonProvider jsonProvider = new DefaultJsonProvider();

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
                    "        \"custom\": \"onely this\",\n" +
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
    public void path_tokens_can_be_read() throws Exception {
        Object result = jsonProvider.parse(DOCUMENT);

        for (PathToken pathToken : new PathTokenizer("$.store.bicycle.color", jsonProvider)) {
            result = pathToken.filter(result, jsonProvider);
        }

        assertEquals("red", result);
    }

    @Test
    public void read_an_array_without_filters() throws Exception {
        Object result = jsonProvider.parse(DOCUMENT);

        for (PathToken pathToken : new PathTokenizer("$.store.book", jsonProvider)) {
            result = pathToken.filter(result, jsonProvider);
        }

        assertEquals(4, toList(result).size());
    }

    @Test
    public void read_a_literal_property_from_object_in_array() throws Exception {
        Object result = jsonProvider.parse(DOCUMENT);

        for (PathToken pathToken : new PathTokenizer("$.store.book[*].title", jsonProvider)) {
            result = pathToken.filter(result, jsonProvider);
        }

        assertEquals(4, toList(result).size());
    }

    @Test
    public void read_a_literal_property_from_position_in_array() throws Exception {
        Object result = jsonProvider.parse(DOCUMENT);

        for (PathToken pathToken : new PathTokenizer("$.store.book[0].title", jsonProvider)) {
            result = pathToken.filter(result, jsonProvider);
        }

        assertEquals("Sayings of the Century", result);
    }

    @Test
    public void read_a_literal_property_from_two_positions_in_array() throws Exception {
        Object result = jsonProvider.parse(DOCUMENT);

        for (PathToken pathToken : new PathTokenizer("$.store.book[0, 1].author", jsonProvider)) {
            result = pathToken.filter(result, jsonProvider);
        }

        assertThat(this.<String>toList(result), hasItems("Nigel Rees", "Evelyn Waugh"));
    }

    @Test
    public void read_a_literal_property_from_head_in_array() throws Exception {
        Object result = jsonProvider.parse(DOCUMENT);

        for (PathToken pathToken : new PathTokenizer("$.store.book[:2].author", jsonProvider)) {
            result = pathToken.filter(result, jsonProvider);
        }

        assertThat(this.<String>toList(result), hasItems("Nigel Rees", "Evelyn Waugh"));
    }

    @Test
    public void read_a_literal_property_from_tail_in_array() throws Exception {
        Object result = jsonProvider.parse(DOCUMENT);

        for (PathToken pathToken : new PathTokenizer("$.store.book[-1:].author", jsonProvider)) {
            result = pathToken.filter(result, jsonProvider);
        }
        assertEquals("J. R. R. Tolkien", result);
    }

    @Test
    public void field_defined_in_array_object() throws Exception {
        Object result = jsonProvider.parse(DOCUMENT);

        for (PathToken pathToken : new PathTokenizer("$.store.book[?(@.custom)].author", jsonProvider)) {
            result = pathToken.filter(result, jsonProvider);
        }
        assertThat(this.<String>toList(result), hasItems("J. R. R. Tolkien"));
    }

    @Test
    public void property_value_in_array_object() throws Exception {
        Object result = jsonProvider.parse(DOCUMENT);

        for (PathToken pathToken : new PathTokenizer("$.store.book[?(@.custom = 'onely this')].author", jsonProvider)) {
            result = pathToken.filter(result, jsonProvider);
        }
        assertThat(this.<String>toList(result), hasItems("J. R. R. Tolkien"));
    }

    @Test
    public void deep_scan() throws Exception {
               Object result = jsonProvider.parse(DOCUMENT);

        for (PathToken pathToken : new PathTokenizer("$..author", jsonProvider)) {
            result = pathToken.filter(result, jsonProvider);
        }
        assertThat(this.<String>toList(result), hasItems("Nigel Rees","Evelyn Waugh", "J. R. R. Tolkien"));
    }

    private <T> List<T> toList(Object obj) {
        return (List<T>) obj;
    }

}
