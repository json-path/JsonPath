package com.jayway.jsonpath;

import org.junit.jupiter.api.Test;


public class Issue_1016
{
    public static final Configuration jsonConf = Configuration.defaultConfiguration();

    @Test
    public void test_read_with_empty_array()
    {
        // Before fix:
        // assertEvaluationThrows("[{\"id\":1,\"array\":[\"a\",{\"b\":\"c\"}]}]", "$.*[?(\"a\" in @.array)]", JsonPathException.class);

        DocumentContext emptyArray = JsonPath.using(jsonConf).parse("[{\"id\":1,\"array\":[\"a\",[]]}]");
        Object read = emptyArray.read("$.*[?(\"a\" in @.array)]");
        assert(read.toString().equals("[{\"id\":1,\"array\":[\"a\",[]]}]"));
    }

    @Test
    public void test_read_with_filled_array()
    {
        DocumentContext filledArray = JsonPath.using(jsonConf).parse("[{\"id\":1,\"array\":[\"a\",[\"b\", \"c\"]]}]");
        Object read = filledArray.read("$.*[?(\"a\" in @.array)]");
        assert(read.toString().equals("[{\"id\":1,\"array\":[\"a\",[\"b\",\"c\"]]}]"));
    }

    @Test
    public void test_read_with_empty_object()
    {
        DocumentContext emptyObj = JsonPath.using(jsonConf).parse("[{\"id\":1,\"array\":[\"a\",{}]}]");
        Object read = emptyObj.read("$.*[?(\"a\" in @.array)]");
        assert(read.toString().equals("[{\"id\":1,\"array\":[\"a\",{}]}]"));
    }

    @Test
    public void test_read_with_filled_object()
    {
        DocumentContext filledObj = JsonPath.using(jsonConf).parse("[{\"id\":1,\"array\":[\"a\",{\"b\":\"c\"}]}]");
        Object read = filledObj.read("$.*[?(\"a\" in @.array)]");
        assert(read.toString().equals("[{\"id\":1,\"array\":[\"a\",{\"b\":\"c\"}]}]"));
    }

    @Test
    public void test_read_with_combined_elements()
    {
        DocumentContext combined = JsonPath.using(jsonConf).parse("[{\"id\":1,\"array\":[\"a\",[\"b\", {\"c\" :  \"d\"}]]}]");
        Object read = combined.read("$.*[?(\"a\" in @.array)]");
        assert(read.toString().equals("[{\"id\":1,\"array\":[\"a\",[\"b\",{\"c\":\"d\"}]]}]"));
    }
}