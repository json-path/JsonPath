package com.jayway.jsonpath;

import org.hamcrest.Matcher;
import org.junit.Test;

import java.util.List;

import static org.hamcrest.Matchers.hasItems;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * Created by IntelliJ IDEA.
 * User: kallestenflo
 * Date: 2/2/11
 * Time: 1:22 PM
 */
public class SplitPathFragmentsTest {



    @Test
    public void valid_path_is_split_correctly() throws Exception {

        assertPath("$.store[*]", hasItems("$", "store", "[*]"));

        assertPath("$", hasItems("$"));

        assertPath("$..*", hasItems("$", "..", "*"));

        assertPath("$.store", hasItems("$", "store"));

        assertPath("$.store.*", hasItems("$", "store", "*"));

        assertPath("$.store[*].name", hasItems("$", "store", "[*]", "name"));

        assertPath("$..book[-1:].foo.bar", hasItems("$", "..", "book", "[-1:]", "foo", "bar"));

        assertPath("$..book[?(@.isbn)]", hasItems("$", "..", "book", "[?(@.isbn)]"));

        assertPath("['store'].['price']", hasItems("$", "store", "price"));

        assertPath("$.['store'].['price']", hasItems("$", "store", "price"));

        assertPath("$.['store']['price']", hasItems("$", "store", "price"));

        assertPath("$.['store'].price", hasItems("$", "store", "price"));

        assertPath("$.['store space']['price space']", hasItems("$", "store space", "price space"));

        assertPath("$.['store']['nice.price']", hasItems("$", "store", "nice.price"));

        assertPath("$..book[?(@.price<10)]", hasItems("$", "..", "book", "[?(@.price<10)]"));

        assertPath("$..book[?(@.price<10)]", hasItems("$", "..", "book", "[?(@.price<10)]"));

        assertPath("$.store.book[*].author", hasItems("$", "store", "book", "[*]", "author"));

        assertPath("$.store..price", hasItems("$", "store", "..", "price"));

    }

    @Test
    public void white_space_are_removed() throws Exception {

        assertPath("$.[ 'store' ]", hasItems("$", "store"));

        assertPath("$.[   'store' ]", hasItems("$", "store"));

        assertPath("$.['store bore']", hasItems("$", "store bore"));

        assertPath("$..book[  ?(@.price<10)  ]", hasItems("$", "..", "book", "[?(@.price<10)]"));

        assertPath("$..book[?(@.price<10  )]", hasItems("$", "..", "book", "[?(@.price<10)]"));

        assertPath("$..book[?(  @.price<10)]", hasItems("$", "..", "book", "[?(@.price<10)]"));

        assertPath("$..book[  ?(@.price<10)]", hasItems("$", "..", "book", "[?(@.price<10)]"));
    }

    @Test
    public void invalid_path_throws_exception() throws Exception {
        assertPathInvalid("$...*");
    }


    //----------------------------------------------------------------
    //
    // Helpers
    //
    //----------------------------------------------------------------

    private void assertPathInvalid(String path) {
        try {
            PathUtil.splitPath(path);
            assertTrue("Expected exception!", false);
        } catch (InvalidPathException expected) {}
    }

    private void assertPath(String path, Matcher<Iterable<String>> matcher) {
        System.out.println("PATH: " + path);

        List<String> fragments = PathUtil.splitPath(path);

        for (String fragment : fragments) {
            System.out.println(fragment);
        }

        assertThat(fragments, matcher);
        System.out.println("----------------------------------");
    }


}
