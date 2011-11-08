package com.jayway.jsonpath;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by IntelliJ IDEA.
 * User: kallestenflo
 * Date: 2/3/11
 * Time: 9:58 PM
 */
public class IsDefinitePathTest {


    @Test
    public void path_is_not_definite() throws Exception {
        assertFalse(JsonPath.compile("$..book[0]").isPathDefinite());
        assertFalse(JsonPath.compile("$.books[*]").isPathDefinite());
    }

    @Test
    public void path_is_definite() throws Exception {
        assertTrue(JsonPath.compile("$.definite.this.is").isPathDefinite());
        assertTrue(JsonPath.compile("rows[0].id").isPathDefinite());
    }


}
