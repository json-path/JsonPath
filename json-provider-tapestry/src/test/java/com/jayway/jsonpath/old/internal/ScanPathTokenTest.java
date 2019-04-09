package com.jayway.jsonpath.old.internal;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.internal.path.PathCompiler;
import org.junit.Test;

import java.util.List;

import static com.jayway.jsonpath.JsonPath.read;
import static org.assertj.core.api.Assertions.assertThat;

public class ScanPathTokenTest {


    public final static Object DOCUMENT = Configuration.defaultConfiguration().jsonProvider().parse(
            "{\n" +
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
                    "}"
    );

    public final static Object DOCUMENT2 = Configuration.defaultConfiguration().jsonProvider().parse(
            "{\n" +
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
                    " }"
    );

    @Test
    public void a_document_can_be_scanned_for_property() {

        List<String> result = read(DOCUMENT, "$..author");

        assertThat(result).containsOnly("Nigel Rees", "Evelyn Waugh", "J. R. R. Tolkien");
    }

    @Test
    public void a_document_can_be_scanned_for_property_path() {

        List<String> result = read(DOCUMENT, "$..address.street");

        assertThat(result).containsOnly("fleet street", "Baker street", "Svea gatan", "Söder gatan");
    }

    @Test
    public void a_document_can_be_scanned_for_wildcard() {

        List<String> result = PathCompiler.compile("$..[*]").evaluate(DOCUMENT, DOCUMENT, Configuration.defaultConfiguration()).getPathList();

        assertThat(result).containsOnly(
                "$['store']",
                "$['store']['bicycle']",
                "$['store']['book']",
                "$['store']['bicycle']['address']",
                "$['store']['bicycle']['color']",
                "$['store']['bicycle']['price']",
                "$['store']['bicycle']['items']",
                "$['store']['bicycle']['address']['city']",
                "$['store']['bicycle']['address']['street']",
                "$['store']['bicycle']['items'][0]",
                "$['store']['bicycle']['items'][1]",
                "$['store']['bicycle']['items'][2]",
                "$['store']['bicycle']['items'][3]",
                "$['store']['bicycle']['items'][4]",
                "$['store']['bicycle']['items'][5]",
                "$['store']['bicycle']['items'][0][0]",
                "$['store']['bicycle']['items'][0][1]",
                "$['store']['bicycle']['items'][0][2]",
                "$['store']['book'][0]",
                "$['store']['book'][1]",
                "$['store']['book'][2]",
                "$['store']['book'][0]['address']",
                "$['store']['book'][0]['author']",
                "$['store']['book'][0]['price']",
                "$['store']['book'][0]['category']",
                "$['store']['book'][0]['title']",
                "$['store']['book'][0]['address']['city']",
                "$['store']['book'][0]['address']['street']",
                "$['store']['book'][1]['address']",
                "$['store']['book'][1]['author']",
                "$['store']['book'][1]['price']",
                "$['store']['book'][1]['category']",
                "$['store']['book'][1]['title']",
                "$['store']['book'][1]['address']['city']",
                "$['store']['book'][1]['address']['street']",
                "$['store']['book'][2]['address']",
                "$['store']['book'][2]['author']",
                "$['store']['book'][2]['price']",
                "$['store']['book'][2]['isbn']",
                "$['store']['book'][2]['category']",
                "$['store']['book'][2]['title']",
                "$['store']['book'][2]['address']['city']",
                "$['store']['book'][2]['address']['street']");
    }

    @Test
    public void a_document_can_be_scanned_for_wildcard2() {

        List<String> result = PathCompiler.compile("$.store.book[0]..*").evaluate(DOCUMENT, DOCUMENT, Configuration.defaultConfiguration()).getPathList();

        assertThat(result).containsOnly(
                "$['store']['book'][0]['address']",
                "$['store']['book'][0]['author']",
                "$['store']['book'][0]['price']",
                "$['store']['book'][0]['category']",
                "$['store']['book'][0]['title']",
                "$['store']['book'][0]['address']['city']",
                "$['store']['book'][0]['address']['street']");

    }

    @Test
    public void a_document_can_be_scanned_for_wildcard3() {

        List<String> result = PathCompiler.compile("$.phoneNumbers[0]..*").evaluate(DOCUMENT2, DOCUMENT, Configuration.defaultConfiguration()).getPathList();

        assertThat(result).containsOnly(
                "$['phoneNumbers'][0]['number']",
                "$['phoneNumbers'][0]['type']");

    }

    @Test
    public void a_document_can_be_scanned_for_predicate_match() {

        List<String> result = PathCompiler.compile("$..[?(@.address.city == 'Stockholm')]").evaluate(DOCUMENT, DOCUMENT, Configuration.defaultConfiguration()).getPathList();

        assertThat(result).containsOnly(
                "$['store']['bicycle']",
                "$['store']['book'][2]");

    }

    @Test
    public void a_document_can_be_scanned_for_existence() {

        List<String> result = PathCompiler.compile("$..[?(@.isbn)]").evaluate(DOCUMENT, DOCUMENT, Configuration.defaultConfiguration()).getPathList();

        assertThat(result).containsOnly(
                "$['store']['book'][2]");
    }


}
