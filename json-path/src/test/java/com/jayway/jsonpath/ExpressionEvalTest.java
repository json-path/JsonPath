package com.jayway.jsonpath;

import com.jayway.jsonpath.eval.Expression;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by IntelliJ IDEA.
 * User: kallestenflo
 * Date: 2/4/11
 * Time: 9:32 PM
 */
public class ExpressionEvalTest {


    @Test
    public void long_eval() throws Exception {

        assertTrue(Expression.eval(1L, "=", "1"));
        assertTrue(Expression.eval(2L, "!=", "1"));
        assertTrue(Expression.eval(2L, ">", "1"));
        assertTrue(Expression.eval(2L, ">=", "1"));
        assertTrue(Expression.eval(2L, ">=", "2"));
        assertTrue(Expression.eval(1L, "<", "2"));
        assertTrue(Expression.eval(2L, "<=", "2"));

        assertFalse(Expression.eval(1, ">", "2"));
        assertFalse(Expression.eval(1, ">=", "2"));
        assertFalse(Expression.eval(2, "<", "1"));
        assertFalse(Expression.eval(2, "<=", "1"));
        assertFalse(Expression.eval(1, "=", "2"));
        assertFalse(Expression.eval(1, "!=", "1"));
    }

    @Test
    public void double_eval() throws Exception {

        assertTrue(Expression.eval(1D, "=", "1"));
        assertTrue(Expression.eval(2D, "!=", "1"));
        assertTrue(Expression.eval(2D, ">", "1"));
        assertTrue(Expression.eval(2D, ">=", "1"));
        assertTrue(Expression.eval(2D, ">=", "2"));
        assertTrue(Expression.eval(1D, "<", "2"));
        assertTrue(Expression.eval(2D, "<=", "2"));

        assertFalse(Expression.eval(1D, ">", "2"));
        assertFalse(Expression.eval(1D, ">=", "2"));
        assertFalse(Expression.eval(2D, "<", "1"));
        assertFalse(Expression.eval(2D, "<=", "1"));
        assertFalse(Expression.eval(1D, "=", "2"));
        assertFalse(Expression.eval(1D, "!=", "1"));
    }

    @Test
    public void string_eval() throws Exception {

        assertTrue(Expression.eval("A", "=", "A"));
        assertTrue(Expression.eval("B", "!=", "A"));

    }


}
