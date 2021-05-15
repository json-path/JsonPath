package com.jayway.jsonpath;

import net.minidev.json.JSONStyle;
import net.minidev.json.JSONValue;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class EscapeTest extends BaseTest {

    private static JSONStyle style;

    @BeforeClass
    public static void before(){
        style = JSONValue.COMPRESSION;
        JSONValue.COMPRESSION = JSONStyle.LT_COMPRESS;
    }

    @AfterClass
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
    
    @Test
    public void Test_Escape_character1() {
        String JSON="{\"\\data\": \"a \\ b\"}";
        assertThat((String)(((JsonPath.parse(JSON)
                .read("$.\\data"))))).isEqualTo("a \\ b");

    }

    @Test
    public void Test_Escape_character2() {
        String JSON="{\"a\": [{\"data\"  : \"a \\ b\"}]}";
        assertThat((String)(((net.minidev.json.JSONArray)(JsonPath.parse(JSON)
                .read("$.a[*].data"))).get(0))).isEqualTo("a \\ b");
    }
}
