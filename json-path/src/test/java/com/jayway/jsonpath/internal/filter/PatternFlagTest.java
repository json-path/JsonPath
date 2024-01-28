package com.jayway.jsonpath.internal.filter;

import com.jayway.jsonpath.BaseTest;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PatternFlagTest extends BaseTest {


    @ParameterizedTest
    @MethodSource("testData")
    public void testParseFlags(int flags, String expectedFlags) {
        assertEquals(expectedFlags, PatternFlag.parseFlags(flags));
    }

    public static Stream<Arguments> testData() {
        return Stream.of(
                Arguments.arguments(1, "d"),
                Arguments.arguments(2, "i"),
                Arguments.arguments(4, "x"),
                Arguments.arguments(8, "m"),
                Arguments.arguments(32, "s"),
                Arguments.arguments(64, "u"),
                Arguments.arguments(256, "U"),
                Arguments.arguments(300, "xmsU"),
                Arguments.arguments(13, "dxm"),
                Arguments.arguments(7, "dix"),
                Arguments.arguments(100, "xsu"),
                Arguments.arguments(367, "dixmsuU")
        );
    }
}
