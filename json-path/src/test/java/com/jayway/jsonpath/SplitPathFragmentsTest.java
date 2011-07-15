package com.jayway.jsonpath;

import org.junit.Test;

import static org.hamcrest.Matchers.hasItemInArray;
import static org.hamcrest.Matchers.hasItems;
import static org.junit.Assert.assertThat;

/**
 * Created by IntelliJ IDEA.
 * User: kallestenflo
 * Date: 2/2/11
 * Time: 1:22 PM
 */
public class SplitPathFragmentsTest {

    /*
    1.  "$..book[-1:].foo.bar"
    2.  "$.store.book[*].author"
    3.  "$..author"
    4.  "$.store.*"
    5.  "$.store..price"
    6.  "$..book[(@.length-1)]"
    7.  "$..book[-1:]
    8.  "$..book[0,1]"
    9.  "$..book[:2]"
    10. "$..book[?(@.isbn)]"
    11. "$..book[?(@.price<10)]"
    12. "$..*"
    */


    @Test
    public void bracket_notation_can_be_split() throws Exception {
        assertThat(PathUtil.splitPath("$.['store'].['price']"), hasItems("$", "store", "price"));
        assertThat(PathUtil.splitPath("$['store']['price']"), hasItems("$", "store", "price"));
        assertThat(PathUtil.splitPath("['store']['price']"), hasItems("$", "store", "price"));
        assertThat(PathUtil.splitPath("['store'].price"), hasItems("$", "store", "price"));

        assertThat(PathUtil.splitPath("$.['store book'].['price list']"), hasItems("$", "store book", "price list"));

        assertThat(PathUtil.splitPath("$.['store.book'].['price.list']"), hasItems("$", "store.book", "price.list"));
    }

    @Test
    public void fragments_are_split_correctly() throws Exception {

        assertThat(PathUtil.splitPath("$..book[-1:].foo.bar"), hasItems("$", "..", "[-1:]", "foo", "bar"));

        assertThat(PathUtil.splitPath("$.store.book[*].author"), hasItems("$", "store", "book", "[*]", "author"));

        assertThat(PathUtil.splitPath("$..author"), hasItems("$", "..", "author"));

        assertThat(PathUtil.splitPath("$.store.*"), hasItems("$", "store", "*"));

        assertThat(PathUtil.splitPath("$.store..price"), hasItems("$", "store", "..", "price"));

        assertThat(PathUtil.splitPath("$..book[(@.length-1)]"), hasItems("$", "..", "book", "[(@.length-1)]"));

        assertThat(PathUtil.splitPath("$..book[-1:]"), hasItems("$", "..", "book", "[-1:]"));

        assertThat(PathUtil.splitPath("$..book[0,1]"), hasItems("$", "..", "book", "[0,1]"));

        assertThat(PathUtil.splitPath("$..book[:2]"), hasItems("$", "..", "book", "[:2]"));

        assertThat(PathUtil.splitPath("$..book[?(@.isbn)]"), hasItems("$", "..", "book", "[?(@.isbn)]"));

        assertThat(PathUtil.splitPath("$..book[?(@.price<10)]"), hasItems("$", "..", "book", "[?(@.price<10)]"));

        assertThat(PathUtil.splitPath("$..*"), hasItems("$", "..", "*"));

        assertThat(PathUtil.splitPath("$.[0][1].author"), hasItems("$", "[0]", "[1]", "author"));

        assertThat(PathUtil.splitPath("$.[0].[1].author"), hasItems("$", "[0]", "[1]", "author"));

        assertThat(PathUtil.splitPath("$.foo:bar.author"), hasItems("$", "foo:bar", "author"));

    }



}
