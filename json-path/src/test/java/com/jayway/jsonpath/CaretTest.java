package com.jayway.jsonpath;

import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static com.jayway.jsonpath.JsonPath.parse;
import static com.jayway.jsonpath.JsonPath.read;
import static java.util.Collections.emptyMap;
import static org.assertj.core.api.Assertions.assertThat;

public class CaretTest extends BaseTest {

    private static final Map<String, Object> EMPTY_MAP = emptyMap();


    @Test
    public void get_parent_node() {

        try(InputStream is = this.getClass().getResourceAsStream("/issue_caret.json")) {
            Object result = read(is, "$..ExtendedResult[?(@.:Type =='ER' && @.:Code == 'FX')].Extension[?(@.:Code == 'PENALTY' && @.:Value)]^^^^^^[':SortOrder']");
            System.out.println(result.toString());
        } catch ( IOException ioex) {
            ioex.printStackTrace();
        }

    }


    // Helper converter implementation for test cases.
    private class ToStringMapFunction implements MapFunction {

        @Override
        public Object map(Object currentValue, Configuration configuration) {
            return currentValue.toString()+"converted";
        }
    }
}