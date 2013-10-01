package com.jayway.jsonpath.internal2;

public class ArrayPathFragmentTest {
/*
    private static final Logger logger = LoggerFactory.getLogger(ArrayPathFragmentTest.class);

    private JsonProvider jsonProvider = JsonProviderFactory.createProvider();

    private String SIMPLE_ARRAY = "[" +
            "{\n" +
            "   \"foo\" : \"foo-val-0\"\n" +
            "}," +
            "{\n" +
            "   \"foo\" : \"foo-val-1\"\n" +
            "}," +
            "{\n" +
            "   \"foo\" : \"foo-val-2\"\n" +
            "}," +
            "{\n" +
            "   \"foo\" : \"foo-val-3\"\n" +
            "}," +
            "{\n" +
            "   \"foo\" : \"foo-val-4\"\n" +
            "}," +
            "{\n" +
            "   \"foo\" : \"foo-val-5\"\n" +
            "}," +
            "{\n" +
            "   \"foo\" : \"foo-val-6\"\n" +
            "}" +
            "]";

    @Test
    public void array_can_select_single_index_by_context_length() {

        PathEvaluationResult result = PathEvaluator.evaluate("$[(@.length-1)]", SIMPLE_ARRAY, jsonProvider, Collections.EMPTY_SET);

        assertEquals("{\"foo\":\"foo-val-6\"}", result.getJson());
    }

    @Test
    public void array_can_select_multiple_indexes() {

        PathEvaluationResult result = PathEvaluator.evaluate("$[0,1]", SIMPLE_ARRAY, jsonProvider, Collections.EMPTY_SET);

        assertEquals("[{\"foo\":\"foo-val-0\"},{\"foo\":\"foo-val-1\"}]", result.getJson());
    }

    @Test
    public void array_can_be_sliced_to_2() {

        PathEvaluationResult result = PathEvaluator.evaluate("$[:2]", SIMPLE_ARRAY, jsonProvider, Collections.EMPTY_SET);

        assertEquals("[{\"foo\":\"foo-val-0\"},{\"foo\":\"foo-val-1\"}]", result.getJson());

        System.out.println(result.getPathList().toString());

        logger.debug(result.toString());
    }

    @Test
    public void array_can_be_sliced_to_2_from_tail() {

        PathEvaluationResult result = PathEvaluator.evaluate("$[:-5]", SIMPLE_ARRAY, jsonProvider, Collections.EMPTY_SET);

        assertEquals("[{\"foo\":\"foo-val-0\"},{\"foo\":\"foo-val-1\"}]", result.getJson());
    }

    @Test
    public void array_can_be_sliced_from_2() {

        PathEvaluationResult result = PathEvaluator.evaluate("$[5:]", SIMPLE_ARRAY, jsonProvider, Collections.EMPTY_SET);

        assertEquals("[{\"foo\":\"foo-val-5\"},{\"foo\":\"foo-val-6\"}]", result.getJson());
    }

    @Test
    public void array_can_be_sliced_from_2_from_tail() {

        PathEvaluationResult result = PathEvaluator.evaluate("$[-2:]", SIMPLE_ARRAY, jsonProvider, Collections.EMPTY_SET);

        assertEquals("[{\"foo\":\"foo-val-5\"},{\"foo\":\"foo-val-6\"}]", result.getJson());
    }

    @Test
    public void array_can_be_sliced_between() {

        PathEvaluationResult result = PathEvaluator.evaluate("$[2:4]", SIMPLE_ARRAY, jsonProvider, Collections.EMPTY_SET);

        assertEquals("[{\"foo\":\"foo-val-2\"},{\"foo\":\"foo-val-3\"}]", result.getJson());
    }
    */
}
