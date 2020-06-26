package com.jayway.jsonpath.matchers;

import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.ReadContext;
import org.junit.Test;

import static com.jayway.jsonpath.JsonPath.compile;
import static com.jayway.jsonpath.matchers.JsonPathMatchers.withoutJsonPath;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

public class WithoutJsonPathTest {
    private static final String JSON_STRING = "{" +
            "\"name\": \"Jessie\"," +
            "\"flag\": false," +
            "\"empty_array\": []," +
            "\"empty_object\": {}," +
            "\"none\": null" +
            "}";
    private static final ReadContext JSON = JsonPath.parse(JSON_STRING);

    @Test
    public void shouldMatchNonExistingJsonPath() {
        assertThat(JSON, withoutJsonPath(compile("$.not_there")));
        assertThat(JSON, withoutJsonPath("$.not_there"));
    }

    @Test
    public void shouldNotMatchExistingJsonPath() {
        assertThat(JSON, not(withoutJsonPath(compile("$.name"))));
        assertThat(JSON, not(withoutJsonPath("$.name")));
        assertThat(JSON, not(withoutJsonPath("$.flag")));
        assertThat(JSON, not(withoutJsonPath("$.empty_array")));
        assertThat(JSON, not(withoutJsonPath("$.empty_object")));
        assertThat(JSON, not(withoutJsonPath("$.none")));
    }

    @Test
    public void shouldBeDescriptive() {
        assertThat(withoutJsonPath("$.name"),
                hasToString(equalTo("without json path \"$['name']\"")));
    }

}
