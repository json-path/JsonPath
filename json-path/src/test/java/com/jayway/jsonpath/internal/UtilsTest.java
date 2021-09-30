package com.jayway.jsonpath.internal;

import com.jayway.jsonpath.JsonPathException;
import java.util.ArrayList;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class UtilsTest {

    @Rule
    public final ExpectedException thrown = ExpectedException.none();

    @Test
    public void testJoin() {
        ArrayList<String> strings = new ArrayList<String>();
        strings.add("foo");
        strings.add("bar");
        strings.add("baz");

        Assert.assertEquals("foo,bar,baz", Utils.join(",", strings));
        Assert.assertEquals("", Utils.join(",", new ArrayList<String>()));
    }

    @Test
    public void testConcat() {
        Assert.assertEquals("", Utils.concat());
        Assert.assertEquals("", Utils.concat(""));
        Assert.assertEquals("", Utils.concat("", ""));
        Assert.assertEquals("a", Utils.concat("a"));
        Assert.assertEquals("a", Utils.concat("", "a", ""));
        Assert.assertEquals("abc", Utils.concat("a", "b", "c"));
    }

    @Test
    public void testEscape() {
        Assert.assertNull(Utils.escape(null, true));

        Assert.assertEquals("\\\\f\\'o\\\"o\\rb\\fa\\t\\nr\\bb\\/a",
                Utils.escape("\\f\'o\"o\rb\fa\t\nr\bb/a", true));
        Assert.assertEquals("\\uFFFF\\u0FFF\\u00FF\\u000F\\u0010",
                Utils.escape("\uffff\u0fff\u00ff\u000f\u0010", true));
    }

    @Test
    public void testUnescape() {
        Assert.assertNull(Utils.unescape(null));

        Assert.assertEquals("foo", Utils.unescape("foo"));
        Assert.assertEquals("\\", Utils.unescape("\\"));
        Assert.assertEquals("\\", Utils.unescape("\\\\"));
        Assert.assertEquals("\'", Utils.unescape("\\\'"));
        Assert.assertEquals("\"", Utils.unescape("\\\""));
        Assert.assertEquals("\r", Utils.unescape("\\r"));
        Assert.assertEquals("\f", Utils.unescape("\\f"));
        Assert.assertEquals("\t", Utils.unescape("\\t"));
        Assert.assertEquals("\n", Utils.unescape("\\n"));
        Assert.assertEquals("\b", Utils.unescape("\\b"));
        Assert.assertEquals("a", Utils.unescape("\\a"));
        Assert.assertEquals("\uffff", Utils.unescape("\\uffff"));
    }

    @Test
    public void testUnescapeThrow() {
        thrown.expect(JsonPathException.class);
        Utils.unescape("\\uuuuu");
    }

    @Test
    public void testHex() {
        Assert.assertEquals("61", Utils.hex('a'));
        Assert.assertEquals("24", Utils.hex('$'));
    }

    @Test
    public void testIsEmpty() {
        Assert.assertTrue(Utils.isEmpty(null));
        Assert.assertTrue(Utils.isEmpty(""));

        Assert.assertFalse(Utils.isEmpty("foo"));
    }

    @Test
    public void testIndexOf() {
        Assert.assertEquals(-1, Utils.indexOf("bar", "foo", 0));
        Assert.assertEquals(-1, Utils.indexOf("bar", "a", 2));
        Assert.assertEquals(1, Utils.indexOf("bar", "a", 0));
        Assert.assertEquals(1, Utils.indexOf("bar", "a", 1));
    }

    @Test
    public void testNotNull() {
        Assert.assertEquals("", Utils.notNull("", "bar", "a", "b", "c"));
        Assert.assertEquals("foo", Utils.notNull("foo", "bar", "a", "b", "c"));
    }

    @Test
    public void testNotNullThrow() {
        thrown.expect(IllegalArgumentException.class);
        Utils.notNull(null, "bar", "a", "b", "c");
    }

    @Test
    public void testCloseQuietly() throws IllegalArgumentException {
        thrown.expect(IllegalArgumentException.class);
        Utils.notNull(null, "bar", "a", "b", "c");
    }

    @Test
    public void testIsTrue() {
        Utils.isTrue(true, "foo");
    }

    @Test
    public void testIsTrueThrow() {
        thrown.expect(IllegalArgumentException.class);
        Utils.isTrue(false, "foo");
    }

    @Test
    public void testOnlyOneIsTrueThrow1() {
        thrown.expect(IllegalArgumentException.class);
        Utils.onlyOneIsTrue("foo", false, false);
    }

    @Test
    public void testOnlyOneIsTrueThrow2() {
        thrown.expect(IllegalArgumentException.class);
        Utils.onlyOneIsTrue("foo", true, true);
    }

    @Test
    public void testOnlyOneIsTrueNonThrow() {
        Assert.assertTrue(Utils.onlyOneIsTrueNonThrow(true));

        Assert.assertFalse(Utils.onlyOneIsTrueNonThrow(true, true, true));
        Assert.assertFalse(Utils.onlyOneIsTrueNonThrow(true, true, false));
        Assert.assertFalse(Utils.onlyOneIsTrueNonThrow(false, false, false));
    }

    @Test
    public void testNotEmpty() {
        Assert.assertEquals("bar", Utils.notEmpty("bar", "foo", 1, 2, 3));
        Assert.assertEquals("baz",
                Utils.notEmpty("baz", "bar", "b", "a", "r"));
    }

    @Test
    public void testNotEmptyThrowNull() {
        thrown.expect(IllegalArgumentException.class);
        Utils.notEmpty(null, "foo", 1, 2, 3);
    }

    @Test
    public void testNotEmptyThrowLength0() {
        thrown.expect(IllegalArgumentException.class);
        Utils.notEmpty("", "foo", 1, 2, 3);
    }

    @Test
    public void testToString() {
        Assert.assertNull(Utils.toString(null));

        Assert.assertEquals("", Utils.toString(""));
        Assert.assertEquals("foo", Utils.toString("foo"));
        Assert.assertEquals("123", Utils.toString(123));
    }
}
