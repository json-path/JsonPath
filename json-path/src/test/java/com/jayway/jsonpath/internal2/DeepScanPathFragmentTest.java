package com.jayway.jsonpath.internal2;

import com.jayway.jsonpath.spi.json.JsonProvider;
import com.jayway.jsonpath.spi.json.JsonProviderFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DeepScanPathFragmentTest {



    private static final Logger logger = LoggerFactory.getLogger(DeepScanPathFragmentTest.class);

    private JsonProvider jsonProvider = JsonProviderFactory.createProvider();

    private static final String DOCUMENT = "{\n" +
            " \"store\":{\n" +
            "  \"book\":[\n" +
            "   {\n" +
            "    \"category\":\"reference\",\n" +
            "    \"author\":\"Nigel Rees\",\n" +
            "    \"title\":\"Sayings of the Century\",\n" +
            "    \"price\":8.95,\n" +
            "    \"address\":{ " +
            "        \"street\":\"fleet street\",\n" +
            "        \"city\":\"London\"\n" +
            "      }\n" +
            "   },\n" +
            "   {\n" +
            "    \"category\":\"fiction\",\n" +
            "    \"author\":\"Evelyn Waugh\",\n" +
            "    \"title\":\"Sword of Honour\",\n" +
            "    \"price\":12.9,\n" +
            "    \"address\":{ \n" +
            "        \"street\":\"Baker street\",\n" +
            "        \"city\":\"London\"\n" +
            "      }\n" +
            "   },\n" +
            "   {\n" +
            "    \"category\":\"fiction\",\n" +
            "    \"author\":\"J. R. R. Tolkien\",\n" +
            "    \"title\":\"The Lord of the Rings\",\n" +
            "    \"isbn\":\"0-395-19395-8\",\n" +
            "    \"price\":22.99," +
            "    \"address\":{ " +
            "        \"street\":\"Svea gatan\",\n" +
            "        \"city\":\"Stockholm\"\n" +
            "      }\n" +
            "   }\n" +
            "  ],\n" +
            "  \"bicycle\":{\n" +
            "   \"color\":\"red\",\n" +
            "   \"price\":19.95," +
            "   \"address\":{ " +
            "        \"street\":\"Söder gatan\",\n" +
            "        \"city\":\"Stockholm\"\n" +
            "      },\n" +
            "   \"items\": [[\"A\",\"B\",\"C\"],1,2,3,4,5]\n" +
            "  }\n" +
            " }\n" +
            "}";

    private static final String DOCUMENT2 = "{\n" +
            "     \"firstName\": \"John\",\n" +
            "     \"lastName\" : \"doe\",\n" +
            "     \"age\"      : 26,\n" +
            "     \"address\"  :\n" +
            "     {\n" +
            "         \"streetAddress\": \"naist street\",\n" +
            "         \"city\"         : \"Nara\",\n" +
            "         \"postalCode\"   : \"630-0192\"\n" +
            "     },\n" +
            "     \"phoneNumbers\":\n" +
            "     [\n" +
            "         {\n" +
            "           \"type\"  : \"iPhone\",\n" +
            "           \"number\": \"0123-4567-8888\"\n" +
            "         },\n" +
            "         {\n" +
            "           \"type\"  : \"home\",\n" +
            "           \"number\": \"0123-4567-8910\"\n" +
            "         }\n" +
            "     ]\n" +
            " }\n" +
            "       ";

    /*
    @Test
    public void a_document_can_be_scanned_for_property() {

        // $..['author']       - PropertyPathComponent
        // $..[*]              - PropertyWildcardComponent
        // $..[1] [1,2,3]      - ArrayPathComponent
        // $..[?(@.name)]      - FilterPredicatePathComponent


        PathEvaluationResult result = PathEvaluator.evaluate("$..author", DOCUMENT, jsonProvider, Collections.EMPTY_SET);

        assertEquals("[\"Nigel Rees\",\"Evelyn Waugh\",\"J. R. R. Tolkien\"]", result.getJson());

    }

    @Test
    public void a_document_can_be_scanned_for_property_path() {

        PathEvaluationResult result = PathEvaluator.evaluate("$..address.street", DOCUMENT, jsonProvider, Collections.EMPTY_SET);

        assertEquals("[\"fleet street\",\"Baker street\",\"Svea gatan\",\"Söder gatan\"]", result.getJson());

    }

    @Test
    public void a_document_can_be_scanned_for_wildcard() {

        PathEvaluationResult result1 = PathEvaluator.evaluate("$..[*]", DOCUMENT, jsonProvider, Collections.EMPTY_SET);
        PathEvaluationResult result2 = PathEvaluator.evaluate("$..*", DOCUMENT, jsonProvider, Collections.EMPTY_SET);

        assertTrue(result1.getPathList().equals(result2.getPathList()));

        logger.debug(result1.toString());
    }

    @Test
    public void a_document_can_be_scanned_for_wildcard2() {
        //ISSUE
        PathEvaluationResult result = PathEvaluator.evaluate("$.store.book[0]..*", DOCUMENT, jsonProvider, Collections.EMPTY_SET);

        logger.debug(result.toString());
    }

    @Test
    public void a_document_can_be_scanned_for_wildcard3() {
        //ISSUE
        PathEvaluationResult result = PathEvaluator.evaluate("$.phoneNumbers[0]..*", DOCUMENT2, jsonProvider, Collections.EMPTY_SET);

        logger.debug(result.toString());
    }

    @Test
    public void a_document_can_be_scanned_for_predicate_match() {

        PathEvaluationResult result = PathEvaluator.evaluate("$..[?(@.address.city == 'Stockholm')]", DOCUMENT, jsonProvider, Collections.EMPTY_SET);

        logger.debug(result.toString());
    }

    @Test
    public void a_document_can_be_scanned_for_existence() {

        PathEvaluationResult result = PathEvaluator.evaluate("$..[?(@.isbn)]", DOCUMENT, jsonProvider, Collections.EMPTY_SET);

        logger.debug(result.toString());
    }

    @Test
    public void a_document_can_be_scanned_for_array_indexes() {

        PathEvaluationResult result = PathEvaluator.evaluate("$..[(@.length - 1)]", DOCUMENT, jsonProvider, Collections.EMPTY_SET);

        logger.debug(result.toString());
        logger.debug(result.getPathList().toString());
    }
    */
}
