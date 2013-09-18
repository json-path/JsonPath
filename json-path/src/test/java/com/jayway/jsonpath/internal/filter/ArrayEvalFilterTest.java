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
        assertEquals(new ArrayEvalFilter.ConditionStatement("@", "==", "5"), ArrayEvalFilter.createConditionStatement("@==5"));
        assertEquals(new ArrayEvalFilter.ConditionStatement("@", "==", "5"), ArrayEvalFilter.createConditionStatement("@ == 5"));
        assertEquals(new ArrayEvalFilter.ConditionStatement("@", "==", "5"), ArrayEvalFilter.createConditionStatement(" @ == 5 "));
        assertEquals(new ArrayEvalFilter.ConditionStatement("@", "==", "5"), ArrayEvalFilter.createConditionStatement("@ ==5"));
        assertEquals(new ArrayEvalFilter.ConditionStatement("@", "==", "5"), ArrayEvalFilter.createConditionStatement("@== 5 "));

        //String array
        assertEquals(new ArrayEvalFilter.ConditionStatement("@", "==", "one"), ArrayEvalFilter.createConditionStatement("@=='one'"));
        assertEquals(new ArrayEvalFilter.ConditionStatement("@", "==", "one monkey"), ArrayEvalFilter.createConditionStatement("@ == 'one monkey' "));
        assertEquals(new ArrayEvalFilter.ConditionStatement("@", "==", "two"), ArrayEvalFilter.createConditionStatement("@  == 'two'"));

        //Sub item dot notation
        assertEquals(new ArrayEvalFilter.ConditionStatement("@.name", "==", "true"), ArrayEvalFilter.createConditionStatement("@.name == true"));

        //Sub item bracket notation
        assertEquals(new ArrayEvalFilter.ConditionStatement("@['name']", "==", "true"), ArrayEvalFilter.createConditionStatement("@['name'] == true"));
        assertEquals(new ArrayEvalFilter.ConditionStatement("@.['name']", "==", "true"), ArrayEvalFilter.createConditionStatement("@.['name'] == true"));

        //Sub path notation
        assertEquals(new ArrayEvalFilter.ConditionStatement("@['name']['age']", "!=", "true"), ArrayEvalFilter.createConditionStatement("@['name']['age'] != true"));
        assertEquals(new ArrayEvalFilter.ConditionStatement("@.['name'].age", ">", "true"), ArrayEvalFilter.createConditionStatement("@.['name'].age > true"));

    }

}
