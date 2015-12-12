package com.jayway.jsonpath.internal.function;

import com.jayway.jsonpath.Configuration;

import java.io.IOException;
import java.util.Scanner;

import static com.jayway.jsonpath.JsonPath.using;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by mattg on 6/27/15.
 */
public class BaseFunctionTest {
    protected static final String NUMBER_SERIES = "{\"empty\": [], \"numbers\" : [ 1, 2, 3, 4, 5, 6, 7, 8, 9, 10]}";
    protected static final String TEXT_SERIES = "{\"urls\": [\"http://api.worldbank.org/countries/all/?format=json\", \"http://api.worldbank.org/countries/all/?format=json\"], \"text\" : [ \"a\", \"b\", \"c\", \"d\", \"e\", \"f\" ]}";

    /**
     * Verify the function returns the correct result based on the input expectedValue
     *
     * @param pathExpr
     *      The path expression to execute
     *
     * @param json
     *      The json document (actual content) to parse
     *
     * @param expectedValue
     *      The expected value to be returned from the test
     */
    protected void verifyFunction(Configuration conf, String pathExpr, String json, Object expectedValue) {
        Object result = using(conf).parse(json).read(pathExpr);
        assertThat(conf.jsonProvider().unwrap(result)).isEqualTo(expectedValue);
    }

    protected void verifyMathFunction(Configuration conf, String pathExpr, Object expectedValue) {
        verifyFunction(conf, pathExpr, NUMBER_SERIES, expectedValue);
    }

    protected void verifyTextFunction(Configuration conf, String pathExpr, Object expectedValue) {
        verifyFunction(conf, pathExpr, TEXT_SERIES, expectedValue);
    }

    protected String getResourceAsText(String resourceName) throws IOException {
        return new Scanner(BaseFunctionTest.class.getResourceAsStream(resourceName), "UTF-8").useDelimiter("\\A").next();
    }
}
