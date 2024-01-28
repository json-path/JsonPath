package com.jayway.jsonpath.internal;

import com.jayway.jsonpath.JsonPathException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

public class UtilsTest {


    @Test
    public void testJoin() {
        ArrayList<String> strings = new ArrayList<String>();
        strings.add("foo");
        strings.add("bar");
        strings.add("baz");

        Assertions.assertEquals("foo,bar,baz", Utils.join(",", strings));
        Assertions.assertEquals("", Utils.join(",", new ArrayList<String>()));
    }

    @Test
    public void testConcat() {
        Assertions.assertEquals("", Utils.concat());
        Assertions.assertEquals("", Utils.concat(""));
        Assertions.assertEquals("", Utils.concat("", ""));
        Assertions.assertEquals("a", Utils.concat("a"));
        Assertions.assertEquals("a", Utils.concat("", "a", ""));
        Assertions.assertEquals("abc", Utils.concat("a", "b", "c"));
    }

    @Test
    public void testEscape() {
        Assertions.assertNull(Utils.escape(null, true));

        Assertions.assertEquals("\\\\f\\'o\\\"o\\rb\\fa\\t\\nr\\bb\\/a", Utils.escape("\\f\'o\"o\rb\fa\t\nr\bb/a", true));
        Assertions.assertEquals("\\uFFFF\\u0FFF\\u00FF\\u000F\\u0010", Utils.escape("\uffff\u0fff\u00ff\u000f\u0010", true));
    }

    @Test
    public void testUnescape() {
        Assertions.assertNull(Utils.unescape(null));

        Assertions.assertEquals("foo", Utils.unescape("foo"));
        Assertions.assertEquals("\\", Utils.unescape("\\"));
        Assertions.assertEquals("\\", Utils.unescape("\\\\"));
        Assertions.assertEquals("\'", Utils.unescape("\\\'"));
        Assertions.assertEquals("\"", Utils.unescape("\\\""));
        Assertions.assertEquals("\r", Utils.unescape("\\r"));
        Assertions.assertEquals("\f", Utils.unescape("\\f"));
        Assertions.assertEquals("\t", Utils.unescape("\\t"));
        Assertions.assertEquals("\n", Utils.unescape("\\n"));
        Assertions.assertEquals("\b", Utils.unescape("\\b"));
        Assertions.assertEquals("a", Utils.unescape("\\a"));
        Assertions.assertEquals("\uffff", Utils.unescape("\\uffff"));
    }

    @Test
    public void testUnescapeThrow() {
        Assertions.assertThrows(JsonPathException.class, () -> Utils.unescape("\\uuuuu"));
    }

    @Test
    public void testHex() {
        Assertions.assertEquals("61", Utils.hex('a'));
        Assertions.assertEquals("24", Utils.hex('$'));
    }

    @Test
    public void testIsEmpty() {
        Assertions.assertTrue(Utils.isEmpty(null));
        Assertions.assertTrue(Utils.isEmpty(""));

        Assertions.assertFalse(Utils.isEmpty("foo"));
    }

    @Test
    public void testIndexOf() {
        Assertions.assertEquals(-1, Utils.indexOf("bar", "foo", 0));
        Assertions.assertEquals(-1, Utils.indexOf("bar", "a", 2));
        Assertions.assertEquals(1, Utils.indexOf("bar", "a", 0));
        Assertions.assertEquals(1, Utils.indexOf("bar", "a", 1));
    }

    @Test
    public void testNotNull() {
        Assertions.assertEquals("", Utils.notNull("", "bar", "a", "b", "c"));
        Assertions.assertEquals("foo", Utils.notNull("foo", "bar", "a", "b", "c"));
    }

    @Test
    public void testNotNullThrow() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> Utils.notNull(null, "bar", "a", "b", "c"));
    }

    @Test
    public void testCloseQuietly() throws IllegalArgumentException {
        Assertions.assertThrows(IllegalArgumentException.class, () -> Utils.notNull(null, "bar", "a", "b", "c"));
    }

    @Test
    public void testIsTrue() {
        Utils.isTrue(true, "foo");
    }

    @Test
    public void testIsTrueThrow() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> Utils.isTrue(false, "foo"));
    }

    @Test
    public void testOnlyOneIsTrueThrow1() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> Utils.onlyOneIsTrue("foo", false, false));
    }

    @Test
    public void testOnlyOneIsTrueThrow2() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> Utils.onlyOneIsTrue("foo", true, true));
    }

    @Test
    public void testOnlyOneIsTrueNonThrow() {
        Assertions.assertTrue(Utils.onlyOneIsTrueNonThrow(true));

        Assertions.assertFalse(Utils.onlyOneIsTrueNonThrow(true, true, true));
        Assertions.assertFalse(Utils.onlyOneIsTrueNonThrow(true, true, false));
        Assertions.assertFalse(Utils.onlyOneIsTrueNonThrow(false, false, false));
    }

    @Test
    public void testNotEmpty() {
        Assertions.assertEquals("bar", Utils.notEmpty("bar", "foo", 1, 2, 3));
        Assertions.assertEquals("baz", Utils.notEmpty("baz", "bar", "b", "a", "r"));
    }

    @Test
    public void testNotEmptyThrowNull() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> Utils.notEmpty(null, "foo", 1, 2, 3));
    }

    @Test
    public void testNotEmptyThrowLength0() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> Utils.notEmpty("", "foo", 1, 2, 3));
    }

    @Test
    public void testToString() {
        Assertions.assertNull(Utils.toString(null));

        Assertions.assertEquals("", Utils.toString(""));
        Assertions.assertEquals("foo", Utils.toString("foo"));
        Assertions.assertEquals("123", Utils.toString(123));
    }
}
