package com.jayway.jsonpath.internal2;

import com.jayway.jsonpath.spi.json.JsonProvider;
import com.jayway.jsonpath.spi.json.JsonProviderFactory;

public class PathWalkerTest {

    private JsonProvider jsonProvider = JsonProviderFactory.createProvider();

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
/*

    @Test
    public void a_definite_path_can_be_evaluated() {

        Object model = jsonProvider.parse(DOCUMENT);

        Path path = PathCompiler.compile("$['store']['bicycle']");

        PathWalker walker = new PathWalker(jsonProvider, Collections.EMPTY_SET);

        walker.walk(path, model);
    }

    @Test
    public void a_path_can_be_evaluated_with_array_wildcard() {

        Object model = jsonProvider.parse(DOCUMENT);

        Path path = PathCompiler.compile("$['store']['book'][*]");

        PathWalker walker = new PathWalker(jsonProvider, Collections.EMPTY_SET);

        walker.walk(path, model);
    }

    @Test
    public void a_path_can_be_evaluated_with_array_sequence() {

        Object model = jsonProvider.parse(DOCUMENT);

        Path path = PathCompiler.compile("$['store']['book'][0,2]");

        PathWalker walker = new PathWalker(jsonProvider, Collections.EMPTY_SET);

        walker.walk(path, model);
    }

    @Test
    public void a_path_can_be_evaluated_with_slice_from() {

        Object model = jsonProvider.parse(DOCUMENT);

        Path path = PathCompiler.compile("$['store']['book'][2:]");

        PathWalker walker = new PathWalker(jsonProvider, Collections.EMPTY_SET);

        walker.walk(path, model);
    }

    @Test
    public void a_path_can_be_evaluated_with_filter() {

        Object model = jsonProvider.parse(DOCUMENT);

        Path path = PathCompiler.compile("$['store']['book'][?(@['category'] == 'fiction')]");

        PathWalker walker = new PathWalker(jsonProvider, Collections.EMPTY_SET);

        walker.walk(path, model);
    }

    @Test
    public void a_path_can_be_evaluated_property_wildcard_on_object() {

        Object model = jsonProvider.parse(DOCUMENT);

        Path path = PathCompiler.compile("$['store']['book'][1].*");

        PathWalker walker = new PathWalker(jsonProvider, Collections.EMPTY_SET);

        walker.walk(path, model);
    }
    */
}
