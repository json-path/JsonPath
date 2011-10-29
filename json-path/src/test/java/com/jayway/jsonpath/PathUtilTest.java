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
public class PathUtilTest {


    @Test
    public void path_is_not_definite() throws Exception {
        assertFalse(PathUtil.isPathDefinite("$..book[0]"));
        assertFalse(PathUtil.isPathDefinite("$.books[*]"));
    }

    @Test
    public void path_is_definite() throws Exception {
        assertTrue(PathUtil.isPathDefinite("$.definite.this.is"));
        assertTrue(PathUtil.isPathDefinite("$.definite:this.is"));
        assertTrue(PathUtil.isPathDefinite("rows[0].id"));
    }


}
