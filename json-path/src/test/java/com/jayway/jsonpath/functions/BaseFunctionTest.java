package com.jayway.jsonpath.functions;

import com.jayway.jsonpath.Configuration;

import static com.jayway.jsonpath.JsonPath.using;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by mattg on 6/27/15.
 */
public class BaseFunctionTest {
    protected static final String NUMBER_SERIES = "{\"numbers\" : [ 1, 2, 3, 4, 5, 6, 7, 8, 9, 10]}";
    protected static final String TEXT_SERIES = "{\"text\" : [ \"a\", \"b\", \"c\", \"d\", \"e\", \"f\" ]}";


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
    protected void verifyFunction(String pathExpr, String json, Object expectedValue) {
        Configuration conf = Configuration.defaultConfiguration();
        assertThat(using(conf).parse(json).read(pathExpr)).isEqualTo(expectedValue);
    }

    protected void verifyMathFunction(String pathExpr, Object expectedValue) {
        verifyFunction(pathExpr, NUMBER_SERIES, expectedValue);
    }
}
