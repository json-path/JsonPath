package com.jayway.jsonpath.internal.function;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.Configurations;
import net.minidev.json.JSONArray;
import org.junit.Test;

/**
 * Verifies methods that are helper implementations of functions for manipulating JSON entities, i.e.
 * length, etc.
 *
 * Created by mattg on 6/27/15.
 */
public class JSONEntityPathFunctionTest extends BaseFunctionTest {


    private static final String BATCH_JSON = "{\n" +
            "  \"batches\": {\n" +
            "    \"minBatchSize\": 10,\n" +
            "    \"results\": [\n" +
            "      {\n" +
            "        \"productId\": 23,\n" +
            "        \"values\": [\n" +
            "          2,\n" +
            "          45,\n" +
            "          34,\n" +
            "          23,\n" +
            "          3,\n" +
            "          5,\n" +
            "          4,\n" +
            "          3,\n" +
            "          2,\n" +
            "          1,\n" +
            "        ]\n" +
            "      },\n" +
            "      {\n" +
            "        \"productId\": 23,\n" +
            "        \"values\": [\n" +
            "          52,\n" +
            "          3,\n" +
            "          12,\n" +
            "          11,\n" +
            "          18,\n" +
            "          22,\n" +
            "          1\n" +
            "        ]\n" +
            "      }\n" +
            "    ]\n" +
            "  }\n" +
            "}";




    private Configuration conf = Configurations.JSON_SMART_CONFIGURATION;

    @Test
    public void testLengthOfTextArray() {
        // The length of JSONArray is an integer
        verifyFunction(conf, "$['text'].length()", TEXT_SERIES, 6);
        verifyFunction(conf, "$['text'].size()", TEXT_SERIES, 6);
    }
    @Test
    public void testLengthOfNumberArray() {
        // The length of JSONArray is an integer
        verifyFunction(conf, "$.numbers.length()", NUMBER_SERIES, 10);
        verifyFunction(conf, "$.numbers.size()", NUMBER_SERIES, 10);
    }


    @Test
    public void testLengthOfStructure() {
        verifyFunction(conf, "$.batches.length()", BATCH_JSON, 2);
    }

    /**
     * The fictitious use-case/story - is we have a collection of batches with values indicating some quality metric.
     * We want to determine the average of the values for only the batch's values where the number of items in the batch
     * is greater than the min batch size which is encoded in the JSON document.
     *
     * We use the length function in the predicate to determine the number of values in each batch and then for those
     * batches where the count is greater than min we calculate the average batch value.
     *
     * Its completely contrived example, however, this test exercises functions within predicates.
     */
    @Test
    public void testPredicateWithFunctionCallSingleMatch() {
        String path = "$.batches.results[?(@.values.length() >= $.batches.minBatchSize)].values.avg()";

        // Its an array because in some use-cases the min size might match more than one batch and thus we'll get
        // the average out for each collection
        JSONArray values = new JSONArray();
        values.add(12.2d);
        verifyFunction(conf, path, BATCH_JSON, values);
    }

    @Test
    public void testPredicateWithFunctionCallTwoMatches() {
        String path = "$.batches.results[?(@.values.length() >= 3)].values.avg()";

        // Its an array because in some use-cases the min size might match more than one batch and thus we'll get
        // the average out for each collection
        JSONArray values = new JSONArray();
        values.add(12.2d);
        values.add(17d);
        verifyFunction(conf, path, BATCH_JSON, values);
    }

}
