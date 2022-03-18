package com.jayway.jsonpath;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * test for issue 786
 */
public class Issue_786 extends BaseTest{

    @Test
    public void test(){
        assertThat(bookLength()).describedAs("First run").isEqualTo(4);
        assertThat(bookLength()).describedAs("Second run").isEqualTo(4);
        assertThat(bookLength()).describedAs("Third run").isEqualTo(4);
    }

    private int bookLength() {
        return JsonPath.read(JSON_DOCUMENT, "$..book.length()");
    }

}
