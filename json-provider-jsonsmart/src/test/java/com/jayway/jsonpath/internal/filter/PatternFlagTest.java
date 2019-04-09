package com.jayway.jsonpath.internal.filter;

import com.jayway.jsonpath.BaseTest;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;

@RunWith(Parameterized.class)
public class PatternFlagTest extends BaseTest {

    private final int flags;
    private final String expectedFlags;

    public PatternFlagTest(int flags, String expectedFlags) {
        this.flags = flags;
        this.expectedFlags = expectedFlags;
    }

    @Test
    public void testParseFlags() {
        Assert.assertEquals(expectedFlags, PatternFlag.parseFlags(flags));
    }

    @Parameterized.Parameters
    public static Iterable data() {
        return Arrays.asList(
            new Object[][]{
                { 1,   "d"       },
                { 2,   "i"       },
                { 4,   "x"       },
                { 8,   "m"       },
                { 32,  "s"       },
                { 64,  "u"       },
                { 256, "U"       },
                { 300, "xmsU"    },
                { 13,  "dxm"     },
                { 7,   "dix"     },
                { 100, "xsu"     },
                { 367, "dixmsuU" }
            }
        );
    }
}
