package com.jayway.jsonpath;

import com.jayway.jsonpath.internal.filter.eval.ExpressionEvaluator;
import org.codehaus.jackson.node.BigIntegerNode;
import org.junit.Test;

import java.math.BigDecimal;
import java.math.BigInteger;

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

        assertTrue(ExpressionEvaluator.eval(1L, "==", "1"));
        assertTrue(ExpressionEvaluator.eval(2L, "!=", "1"));
        assertTrue(ExpressionEvaluator.eval(2L, ">", "1"));
        assertTrue(ExpressionEvaluator.eval(2L, ">=", "1"));
        assertTrue(ExpressionEvaluator.eval(2L, ">=", "2"));
        assertTrue(ExpressionEvaluator.eval(1L, "<", "2"));
        assertTrue(ExpressionEvaluator.eval(2L, "<=", "2"));

        assertFalse(ExpressionEvaluator.eval(1, ">", "2"));
        assertFalse(ExpressionEvaluator.eval(1, ">=", "2"));
        assertFalse(ExpressionEvaluator.eval(2, "<", "1"));
        assertFalse(ExpressionEvaluator.eval(2, "<=", "1"));
        assertFalse(ExpressionEvaluator.eval(1, "==", "2"));
        assertFalse(ExpressionEvaluator.eval(1, "!=", "1"));
    }

    @Test
    public void bigint_eval() throws Exception {

        assertTrue(ExpressionEvaluator.eval(new BigInteger("1"), "==", "1"));
        assertTrue(ExpressionEvaluator.eval(new BigInteger("2"), "!=", "1"));
        assertTrue(ExpressionEvaluator.eval(new BigInteger("2"), ">", "1"));
        assertTrue(ExpressionEvaluator.eval(new BigInteger("2"), ">=", "1"));
        assertTrue(ExpressionEvaluator.eval(new BigInteger("2"), ">=", "2"));
        assertTrue(ExpressionEvaluator.eval(new BigInteger("1"), "<", "2"));
        assertTrue(ExpressionEvaluator.eval(new BigInteger("2"), "<=", "2"));

        assertFalse(ExpressionEvaluator.eval(new BigInteger("1"), ">", "2"));
        assertFalse(ExpressionEvaluator.eval(new BigInteger("1"), ">=", "2"));
        assertFalse(ExpressionEvaluator.eval(new BigInteger("2"), "<", "1"));
        assertFalse(ExpressionEvaluator.eval(new BigInteger("2"), "<=", "1"));
        assertFalse(ExpressionEvaluator.eval(new BigInteger("1"), "==", "2"));
        assertFalse(ExpressionEvaluator.eval(new BigInteger("1"), "!=", "1"));
    }

    @Test
    public void bigdec_eval() throws Exception {

        assertTrue(ExpressionEvaluator.eval(new BigDecimal("1.1"), "==", "1.1"));
        assertTrue(ExpressionEvaluator.eval(new BigDecimal("2"), "!=", "1"));
        assertTrue(ExpressionEvaluator.eval(new BigDecimal("2"), ">", "1"));
        assertTrue(ExpressionEvaluator.eval(new BigDecimal("2"), ">=", "1"));
        assertTrue(ExpressionEvaluator.eval(new BigDecimal("2"), ">=", "2"));
        assertTrue(ExpressionEvaluator.eval(new BigDecimal("1"), "<", "2"));
        assertTrue(ExpressionEvaluator.eval(new BigDecimal("2"), "<=", "2"));

        assertFalse(ExpressionEvaluator.eval(new BigDecimal("1"), ">", "2"));
        assertFalse(ExpressionEvaluator.eval(new BigDecimal("1"), ">=", "2"));
        assertFalse(ExpressionEvaluator.eval(new BigDecimal("2"), "<", "1"));
        assertFalse(ExpressionEvaluator.eval(new BigDecimal("2"), "<=", "1"));
        assertFalse(ExpressionEvaluator.eval(new BigDecimal("1"), "==", "2"));
        assertFalse(ExpressionEvaluator.eval(new BigDecimal("1"), "!=", "1"));
    }


    @Test
    public void double_eval() throws Exception {

        assertTrue(ExpressionEvaluator.eval(1D, "==", "1"));
        assertTrue(ExpressionEvaluator.eval(2D, "!=", "1"));
        assertTrue(ExpressionEvaluator.eval(2D, ">", "1"));
        assertTrue(ExpressionEvaluator.eval(2D, ">=", "1"));
        assertTrue(ExpressionEvaluator.eval(2D, ">=", "2"));
        assertTrue(ExpressionEvaluator.eval(1D, "<", "2"));
        assertTrue(ExpressionEvaluator.eval(2D, "<=", "2"));

        assertFalse(ExpressionEvaluator.eval(1D, ">", "2"));
        assertFalse(ExpressionEvaluator.eval(1D, ">=", "2"));
        assertFalse(ExpressionEvaluator.eval(2D, "<", "1"));
        assertFalse(ExpressionEvaluator.eval(2D, "<=", "1"));
        assertFalse(ExpressionEvaluator.eval(1D, "==", "2"));
        assertFalse(ExpressionEvaluator.eval(1D, "!=", "1"));
    }

    @Test
    public void string_eval() throws Exception {

        assertTrue(ExpressionEvaluator.eval("A", "==", "A"));
        assertTrue(ExpressionEvaluator.eval("B", "!=", "A"));

    }

    @Test
    public void boolean_eval() throws Exception {

        assertTrue(ExpressionEvaluator.eval(true, "==", "true"));
        assertTrue(ExpressionEvaluator.eval(false, "==", "false"));
        assertTrue(ExpressionEvaluator.eval(true, "!=", "false"));
        assertTrue(ExpressionEvaluator.eval(true, "<>", "false"));
        assertTrue(ExpressionEvaluator.eval(false, "!=", "true"));
        assertTrue(ExpressionEvaluator.eval(false, "<>", "true"));

        assertFalse(ExpressionEvaluator.eval(true, "==", "false"));
        assertFalse(ExpressionEvaluator.eval(false, "==", "true"));
        assertFalse(ExpressionEvaluator.eval(true, "!=", "true"));
        assertFalse(ExpressionEvaluator.eval(true, "<>", "true"));
        assertFalse(ExpressionEvaluator.eval(false, "!=", "false"));
        assertFalse(ExpressionEvaluator.eval(false, "<>", "false"));

    }

}
