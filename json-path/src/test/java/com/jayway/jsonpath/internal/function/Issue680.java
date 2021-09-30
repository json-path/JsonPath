package com.jayway.jsonpath.internal.function;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.spi.json.JsonSmartJsonProvider;
import com.jayway.jsonpath.spi.mapper.JsonSmartMappingProvider;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class Issue680 {

    @Test
    public void testIssue680concat() {
        String json = "{ \"key\": \"first\"}";
        Object value = JsonPath.read(json, "concat(\"/\", $.key)");
        assertThat(value).isEqualTo("/first");
        json = "{ \"key\": \"second\"}";
        value = JsonPath.read(json, "concat(\"/\", $.key)");
        assertThat(value).isEqualTo("/second");
    }

    @Test
    public void testIssue680min() {
        String json = "{ \"key\": 1}";
        double value = JsonPath.read(json, "min($.key)");
        assertThat(value).isEqualTo(1d);
        json = "{ \"key\": 2}";
        value = JsonPath.read(json, "min($.key)");
        assertThat(value).isEqualTo(2d);
    }

    @Test
    public void testIssue680concat_2() {
        Map<String, String> context = new HashMap<String, String>();
        context.put("key", "first");
        Object value = JsonPath.read(context, "concat(\"/\", $.key)");
        assertThat(value).isEqualTo("/first");
        Map<String, String> context2 = new HashMap<String, String>();
        context2.put("key", "second");
        value = JsonPath.read(context2, "concat(\"/\", $.key)");
        assertThat(value).isEqualTo("/second");
    }
}
