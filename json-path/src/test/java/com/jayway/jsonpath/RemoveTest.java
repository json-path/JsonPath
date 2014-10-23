package com.jayway.jsonpath;

import static com.jayway.jsonpath.JsonPath.using;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

public class RemoveTest extends BaseTest {

    @Test
    public void fromJsonString() {
    	String result = JsonPath.remove("{\"val\":1,\"val2\":2}", "$.val");
    	assertThat(result).isEqualTo("{\"val2\":2}");
    }
    
    @SuppressWarnings("unchecked")
	@Test
    public void fromMap() {
        Map<String, Object> model = new HashMap<String, Object>(){{
            put("a", "a-val");
            put("b", "b-val");
            put("c", "c-val");
        }};

        Configuration conf = Configuration.defaultConfiguration();

        assertThat(using(conf).parse(model).remove("$.b", Map.class))
                .containsEntry("a", "a-val")
                .containsEntry("c", "c-val").doesNotContainKey("b");
    }
    
    @Test
    public void fromMapToString() {
        Map<String, Object> model = new HashMap<String, Object>(){{
            put("a", "a-val");
            put("b", "b-val");
            put("c", "c-val");
        }};

        Configuration conf = Configuration.defaultConfiguration();

        assertThat(using(conf).parse(model).remove("$.b", String.class))
		        .contains("\"a\":\"a-val\"")
		        .contains("\"c\":\"c-val\"")
		        .doesNotContain("\"b\":\"b-val\"");
    }
    
    @SuppressWarnings("unchecked")
	@Test
    public void fromStringToMap() {
        Configuration conf = Configuration.defaultConfiguration();

        assertThat(using(conf).parse("{\"val\":1,\"val2\":2}").remove("$.val2", Map.class))
		        .containsEntry("val", 1).doesNotContainKey("val2");
    }
}