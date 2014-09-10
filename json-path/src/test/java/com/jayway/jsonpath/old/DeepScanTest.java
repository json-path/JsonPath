package com.jayway.jsonpath.old;

import com.jayway.jsonpath.Option;
import com.jayway.jsonpath.spi.json.JsonProvider;
import com.jayway.jsonpath.spi.json.JsonProviderFactory;

import java.util.Collections;
import java.util.Set;

public class DeepScanTest {

    private static final String DOCUMENT = "{\n" +
            " \"store\":{\n" +
            "  \"book\":[\n" +
            "   {\n" +
            "    \"category\":\"reference\",\n" +
            "    \"author\":\"Nigel Rees\",\n" +
            "    \"title\":\"Sayings of the Century\",\n" +
            "    \"price\":8.95\n" +
            "   },\n" +
            "   {\n" +
            "    \"category\":\"fiction\",\n" +
            "    \"author\":\"Evelyn Waugh\",\n" +
            "    \"title\":\"Sword of Honour\",\n" +
            "    \"price\":12.99\n" +
            "   },\n" +
            "   {\n" +
            "    \"category\":\"fiction\",\n" +
            "    \"author\":\"J. R. R. Tolkien\",\n" +
            "    \"title\":\"The Lord of the Rings\",\n" +
            "    \"isbn\":\"0-395-19395-8\",\n" +
            "    \"price\":22.99\n" +
            "   }\n" +
            "  ],\n" +
            "  \"bicycle\":{\n" +
            "   \"color\":\"red\",\n" +
            "   \"price\":19.95\n" +
            "  }\n" +
            " }\n" +
            "}";

    private static final JsonProvider prov = JsonProviderFactory.createProvider();
    private static final Set<Option> opts = Collections.emptySet();
/*
    @Test
    public void correct_path() {

        System.out.println(DOCUMENT);

        System.out.println(PathEvaluator.evaluate("$.store..", DOCUMENT, prov, opts));
    }

    @Test
    public void a_string_property_can_be_scanned_for() {

        PathEvaluationResult result = PathEvaluator.evaluate("$.store..category", DOCUMENT, prov, opts);

        assertThat(result.getPathList(), hasItems("$['store']['book'][0]['category']", "$['store']['book'][1]['category']", "$['store']['book'][2]['category']"));

        assertThat(result.getResultList(), hasItems(
                Matchers.<Object>is("reference"),
                Matchers.<Object>is("fiction"),
                Matchers.<Object>is("fiction")));

        System.out.println(result.toString());
    }

    @Test
    public void a_path_can_end_with_deep_scan() {

        String json = "{\"items\":[1, 3, 5, 7, 8, 13, 20]}";

        //System.out.println( JsonPath.read(json, "$.."));
        System.out.println(PathEvaluator.evaluate("$..", json, prov, opts).toString());

    }
    */
}
