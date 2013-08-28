package com.jayway.jsonpath;

import com.jayway.jsonpath.internal.filter.eval.ExpressionEvaluator;
import org.codehaus.jackson.node.BigIntegerNode;
import org.junit.Test;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by IntelliJ IDEA.
 * User: kallestenflo
 * Date: 2/4/11
 * Time: 9:32 PM
 */
public class ExpressionEvalTest {

    public static final String DOCUMENT= "{\n" +
            "    \"characters\": [\n" +
            "        {\n" +
            "            \"aliases\": [], \n" +
            "            \"name\": \"Kleeg Lars\", \n" +
            "            \"occupation\": \"Moisture farmer\", \n" +
            "            \"offspring\": []\n" +
            "        }, \n" +
            "        {\n" +
            "            \"aliases\": [], \n" +
            "            \"name\": \"Shmi Skywalker\", \n" +
            "            \"occupation\": \"Virgin mother\", \n" +
            "            \"offspring\": [\n" +
            "                \"AnakinSkywalker\"\n" +
            "            ]\n" +
            "        }, \n" +
            "        {\n" +
            "            \"aliases\": [\n" +
            "                \"Darth Vader\"\n" +
            "            ], \n" +
            "            \"name\": \"Annakin Skywalker\", \n" +
            "            \"occupation\": \"Hand of the Emperor, Lord of the Sith\", \n" +
            "            \"offspring\": [\n" +
            "                \"Luke Skywalker\", \n" +
            "                \"LeiaOrgana\"\n" +
            "            ]\n" +
            "        }, \n" +
            "        {\n" +
            "            \"aliases\": [\n" +
            "                \"Nerf herder\"\n" +
            "            ], \n" +
            "            \"name\": \"Luke Skywalker\", \n" +
            "            \"occupation\": \"Farm boy\", \n" +
            "            \"offspring\": null\n" +
            "        }, \n" +
            "        {\n" +
            "            \"aliases\": [\n" +
            "                \"Your Highness\"\n" +
            "            ], \n" +
            "            \"name\": \"Leia Organa\", \n" +
            "            \"occupation\": \"Senator\", \n" +
            "        }\n" +
            "    ]\n" +
            "}\n";

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
        assertFalse(ExpressionEvaluator.eval(false, "<>", "false"));
        assertFalse(ExpressionEvaluator.eval(false, "!=", "false"));

    }

    @Test
    public void null_eval() throws Exception {
        assertTrue(ExpressionEvaluator.eval(new Integer(10), "!=", "null"));

        assertTrue(ExpressionEvaluator.eval(null, "==", "null"));

        assertTrue(ExpressionEvaluator.eval(null, "<>", "FOO"));
        assertTrue(ExpressionEvaluator.eval("FOO", "<>", "null"));


        assertTrue(ExpressionEvaluator.eval(null, "!=", "FOO"));
        assertTrue(ExpressionEvaluator.eval("FOO", "<>", "null"));

        assertTrue(ExpressionEvaluator.eval(null, "!=", "10"));
    }


    @Test
    public void nulls_filter() {

        List<Map<String, Object>> result = JsonPath.read(DOCUMENT, "$.characters[?(@.offspring == null)]");
        assertEquals(1, result.size());

        result = JsonPath.read(DOCUMENT, "$.characters[?(@.offspring != null)]");
        assertEquals(3, result.size());

        result = JsonPath.read(DOCUMENT, "$.characters[?(@.offspring)]");
        assertEquals(4, result.size());
    }


}
