package com.jayway.jsonpath.internal.filter;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Locale;

public class RelationalOperatorTest {

    Locale locale;

    @Before
    public void saveDefaultLocale() {
        locale = Locale.getDefault();
    }

    @After
    public void restoreDefaultLocale() {
        Locale.setDefault(locale);
    }

    @Test
    public void testFromStringWithEnglishLocale() {
        Locale.setDefault(Locale.ENGLISH);
        assertEquals(RelationalOperator.IN, RelationalOperator.fromString("in"));
        assertEquals(RelationalOperator.IN, RelationalOperator.fromString("IN"));
    }

    @Test
    public void testFromStringWithTurkishLocale() {
        Locale.setDefault(new Locale("tr", "TR"));
        assertEquals(RelationalOperator.IN, RelationalOperator.fromString("in"));
        assertEquals(RelationalOperator.IN, RelationalOperator.fromString("IN"));
    }

}