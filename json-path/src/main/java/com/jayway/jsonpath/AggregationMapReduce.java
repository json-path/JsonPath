package com.jayway.jsonpath;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Defines a pattern for taking a collection of input streams and executing the same path operation across all files
 * map / reducing the results as they come in to provide an aggregation function on top of the
 *
 * Created by matt@mjgreenwood.net on 6/26/15.
 */
public class AggregationMapReduce {

    public static void main(String args[]) throws IOException {
        ReadContext ctx = JsonPath.parse(new File("/home/mattg/dev/JsonPath/json-path-assert/src/test/resources/lotto.json"));
        List<String> numbers = ctx.read("$.lotto.winners..numbers.%sum()");

        Object value = ctx.read("$.lotto.winners.[?(@.winnerId > $.lotto.winners.%length())].numbers.%avg()");
        System.out.println(numbers);
        System.out.println(value);
    }
}
