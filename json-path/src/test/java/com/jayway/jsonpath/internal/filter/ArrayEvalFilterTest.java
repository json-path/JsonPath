package com.jayway.jsonpath.internal.filter;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * User: kalle
 * Date: 8/21/13
 * Time: 12:21 PM
 */
public class ArrayEvalFilterTest {

    @Test
    public void can_determine_condition_statement() {

       assertTrue(ArrayEvalFilter.isConditionStatement("[?(@.id == 5 && @.name == 'kalle')]"));
       assertTrue(ArrayEvalFilter.isConditionStatement("[?( @==5)]"));
       assertTrue(ArrayEvalFilter.isConditionStatement("[?(@.id == 5)]"));



    }

    @Test
    public void condition_statements_can_be_parsed() {

        //assertEquals(new ArrayEvalFilter.ConditionStatement("@.length", ">", "0"), ArrayEvalFilter.createConditionStatement("[?(@.length>0)]"));

        //int array
        assertEquals(new ArrayEvalFilter.OperatorExpression("@", "==", "5"), ArrayEvalFilter.createExpression("@==5"));
        assertEquals(new ArrayEvalFilter.OperatorExpression("@", "==", "5"), ArrayEvalFilter.createExpression("@ == 5"));
        assertEquals(new ArrayEvalFilter.OperatorExpression("@", "==", "5"), ArrayEvalFilter.createExpression(" @ == 5 "));
        assertEquals(new ArrayEvalFilter.OperatorExpression("@", "==", "5"), ArrayEvalFilter.createExpression("@ ==5"));
        assertEquals(new ArrayEvalFilter.OperatorExpression("@", "==", "5"), ArrayEvalFilter.createExpression("@== 5 "));

        //String array
        assertEquals(new ArrayEvalFilter.OperatorExpression("@", "==", "one"), ArrayEvalFilter.createExpression("@=='one'"));
        assertEquals(new ArrayEvalFilter.OperatorExpression("@", "==", "one monkey"), ArrayEvalFilter.createExpression("@ == 'one monkey' "));
        assertEquals(new ArrayEvalFilter.OperatorExpression("@", "==", "two"), ArrayEvalFilter.createExpression("@  == 'two'"));

        //Sub item dot notation
        assertEquals(new ArrayEvalFilter.OperatorExpression("@.name", "==", "true"), ArrayEvalFilter.createExpression("@.name == true"));

        //Sub item bracket notation
        assertEquals(new ArrayEvalFilter.OperatorExpression("@['name']", "==", "true"), ArrayEvalFilter.createExpression("@['name'] == true"));
        assertEquals(new ArrayEvalFilter.OperatorExpression("@.['name']", "==", "true"), ArrayEvalFilter.createExpression("@.['name'] == true"));

        //Sub path notation
        assertEquals(new ArrayEvalFilter.OperatorExpression("@['name']['age']", "!=", "true"), ArrayEvalFilter.createExpression("@['name']['age'] != true"));
        assertEquals(new ArrayEvalFilter.OperatorExpression("@.['name'].age", ">", "true"), ArrayEvalFilter.createExpression("@.['name'].age > true"));

    }

}
