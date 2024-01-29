package com.jayway.jsonpath;

import net.minidev.json.JSONStyle;
import net.minidev.json.JSONValue;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class EscapeTest extends BaseTest {

    private static JSONStyle style;

    @BeforeAll
    public static void before(){
        style = JSONValue.COMPRESSION;
        JSONValue.COMPRESSION = JSONStyle.LT_COMPRESS;
    }

    @AfterAll
    public static void after(){
        JSONValue.COMPRESSION = style;
    }

    @Test
    public void urls_are_not_escaped() {

        JSONStyle orig = JSONValue.COMPRESSION;



        String json = "[" +
                "\"https://a/b/1\"," +
                "\"https://a/b/2\"," +
                "\"https://a/b/3\"" +
                "]";

        String resAsString = JsonPath.using(JSON_SMART_CONFIGURATION).parse(json).read("$").toString();

        assertThat(resAsString).contains("https://a/b/1");

    }
}
