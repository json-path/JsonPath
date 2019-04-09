package com.jayway.jsonpath.matchers;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.ReadContext;
import com.jayway.jsonpath.matchers.helpers.StrictParsingConfiguration;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.util.List;
import java.util.Map;

import static com.jayway.jsonpath.matchers.JsonPathMatchers.*;
import static com.jayway.jsonpath.matchers.helpers.ResourceHelpers.resource;
import static com.jayway.jsonpath.matchers.helpers.ResourceHelpers.resourceAsFile;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

public class JsonPathMatchersTest {
    private static final String BOOKS_JSON = resource("books.json");
    private static final String INVALID_JSON = "{ invalid-json }";
    private static final File BOOKS_JSON_FILE = resourceAsFile("books.json");

    @BeforeClass
    public static void setupStrictJsonParsing() {
        // NOTE: Evaluation depends on the default configuration of JsonPath
        Configuration.setDefaults(new StrictParsingConfiguration());
    }

    @AfterClass
    public static void setupDefaultJsonParsing() {
        Configuration.setDefaults(null);
    }

    @Test
    public void shouldMatchJsonPathToStringValue() {
        final String json = "{\"name\": \"Jessie\"}";

        assertThat(json, hasJsonPath("$.name"));
        assertThat(json, isJson(withJsonPath("$.name")));
        assertThat(json, hasJsonPath("$.name", equalTo("Jessie")));
        assertThat(json, isJson(withJsonPath("$.name", equalTo("Jessie"))));

        assertThat(json, not(hasJsonPath("$.name", equalTo("John"))));
        assertThat(json, not(isJson(withJsonPath("$.name", equalTo("John")))));
    }

    @Test
    public void shouldMatchJsonPathToIntegerValue() {
        final String json = "{\"number\": 10}";

        assertThat(json, hasJsonPath("$.number"));
        assertThat(json, isJson(withJsonPath("$.number")));
        assertThat(json, hasJsonPath("$.number", equalTo(10)));
        assertThat(json, isJson(withJsonPath("$.number", equalTo(10))));

        assertThat(json, not(hasJsonPath("$.number", equalTo(3))));
        assertThat(json, not(isJson(withJsonPath("$.number", equalTo(3)))));
    }

    @Test
    public void shouldMatchJsonPathToDoubleValue() {
        final String json = "{\"price\": 19.95}";

        assertThat(json, hasJsonPath("$.price"));
        assertThat(json, isJson(withJsonPath("$.price")));
        assertThat(json, hasJsonPath("$.price", equalTo(19.95)));
        assertThat(json, isJson(withJsonPath("$.price", equalTo(19.95))));

        assertThat(json, not(hasJsonPath("$.price", equalTo(3.3))));
        assertThat(json, not(isJson(withJsonPath("$.price", equalTo(42)))));
    }

    @Test
    public void shouldMatchJsonPathToBooleanValue() {
        final String json = "{\"flag\": false}";

        assertThat(json, hasJsonPath("$.flag"));
        assertThat(json, isJson(withJsonPath("$.flag")));
        assertThat(json, hasJsonPath("$.flag", equalTo(false)));
        assertThat(json, isJson(withJsonPath("$.flag", equalTo(false))));

        assertThat(json, not(hasJsonPath("$.flag", equalTo(true))));
        assertThat(json, not(isJson(withJsonPath("$.flag", equalTo(true)))));
    }

    @Test
    public void shouldMatchJsonPathToJsonObject() {
        final String json = "{\"object\": { \"name\":\"Oscar\"}}";

        assertThat(json, hasJsonPath("$.object"));
        assertThat(json, isJson(withJsonPath("$.object")));
        assertThat(json, hasJsonPath("$.object", instanceOf(Map.class)));
        assertThat(json, isJson(withJsonPath("$.object", instanceOf(Map.class))));

        assertThat(json, not(hasJsonPath("$.object", instanceOf(List.class))));
        assertThat(json, not(isJson(withJsonPath("$.object", instanceOf(List.class)))));
    }

    @Test
    public void shouldMatchJsonPathToEmptyJsonObject() {
        final String json = "{\"empty_object\": {}}";

        assertThat(json, hasJsonPath("$.empty_object"));
        assertThat(json, isJson(withJsonPath("$.empty_object")));
        assertThat(json, hasJsonPath("$.empty_object", instanceOf(Map.class)));
        assertThat(json, isJson(withJsonPath("$.empty_object", instanceOf(Map.class))));

        assertThat(json, not(hasJsonPath("$.empty_object", instanceOf(List.class))));
        assertThat(json, not(isJson(withJsonPath("$.empty_object", instanceOf(List.class)))));
    }

    @Test
    public void shouldMatchJsonPathToJsonArray() {
        final String json = "{\"list\": [ \"one\",\"two\",\"three\"]}";

        assertThat(json, hasJsonPath("$.list"));
        assertThat(json, hasJsonPath("$.list[*]"));
        assertThat(json, isJson(withJsonPath("$.list")));
        assertThat(json, isJson(withJsonPath("$.list[*]")));
        assertThat(json, hasJsonPath("$.list", contains("one", "two", "three")));
        assertThat(json, isJson(withJsonPath("$.list", hasItem("two"))));

        assertThat(json, not(hasJsonPath("$.list", hasSize(2))));
        assertThat(json, not(isJson(withJsonPath("$.list", contains("four")))));
    }

    @Test
    public void shouldMatchJsonPathToEmptyJsonArray() {
        final String json = "{\"empty_list\": []}";

        assertThat(json, hasJsonPath("$.empty_list"));
        assertThat(json, hasJsonPath("$.empty_list[*]"));
        assertThat(json, isJson(withJsonPath("$.empty_list")));
        assertThat(json, isJson(withJsonPath("$.empty_list[*]")));
        assertThat(json, hasJsonPath("$.empty_list", empty()));
        assertThat(json, isJson(withJsonPath("$.empty_list", hasSize(0))));

        assertThat(json, not(hasJsonPath("$.empty_list", hasSize(2))));
        assertThat(json, not(isJson(withJsonPath("$.empty_list", contains("four")))));
    }

    @Test
    public void willMatchIndefiniteJsonPathsEvaluatedToEmptyLists() {
        // This is just a test to demonstrate that indefinite paths
        // will always match, regardless of result. This is because
        // the evaluation of these expressions will return lists,
        // even though they may be empty.
        String json = "{\"items\": []}";
        assertThat(json, hasJsonPath("$.items[*].name"));
        assertThat(json, hasJsonPath("$.items[*]"));
        assertThat(json, hasJsonPath("$.items[*]", hasSize(0)));
    }

    @Test
    public void shouldMatchJsonPathToNullValue() {
        final String json = "{\"none\": null}";

        assertThat(json, hasJsonPath("$.none"));
        assertThat(json, isJson(withJsonPath("$.none")));
        assertThat(json, hasJsonPath("$.none", nullValue()));
        assertThat(json, isJson(withJsonPath("$.none", nullValue())));

        assertThat(json, not(hasJsonPath("$.none", equalTo("something"))));
        assertThat(json, not(isJson(withJsonPath("$.none", empty()))));
    }

    @Test
    public void shouldNotMatchNonExistingJsonPath() {
        final String json = "{}";

        assertThat(json, not(hasJsonPath("$.not_there")));
        assertThat(json, not(hasJsonPath("$.not_there", anything())));
        assertThat(json, not(hasJsonPath("$.not_there[*]")));
        assertThat(json, not(hasJsonPath("$.not_there[*]", anything())));
        assertThat(json, not(isJson(withJsonPath("$.not_there"))));
        assertThat(json, not(isJson(withJsonPath("$.not_there", anything()))));
        assertThat(json, not(isJson(withJsonPath("$.not_there[*]"))));
        assertThat(json, not(isJson(withJsonPath("$.not_there[*]", anything()))));
    }

    @Test
    public void shouldNotMatchInvalidJsonWithPath() {
        assertThat(INVALID_JSON, not(hasJsonPath("$.path")));
        assertThat(new Object(), not(hasJsonPath("$.path")));
        assertThat(null, not(hasJsonPath("$.path")));
    }

    @Test
    public void shouldNotMatchInvalidJsonWithPathAndValue() {
        assertThat(INVALID_JSON, not(hasJsonPath("$.path", anything())));
        assertThat(new Object(), not(hasJsonPath("$.path", anything())));
        assertThat(null, not(hasJsonPath("$.message", anything())));
    }

    @Test
    public void shouldMatchJsonPathOnFile() {
        assertThat(BOOKS_JSON_FILE, hasJsonPath("$.store.name", equalTo("Little Shop")));
    }

    @Test
    public void shouldNotMatchJsonPathOnNonExistingFile() {
        File nonExistingFile = new File("missing-file");
        assertThat(nonExistingFile, not(hasJsonPath("$..*", anything())));
    }

    @Test
    public void shouldMatchJsonPathOnParsedJsonObject() {
        Object json = Configuration.defaultConfiguration().jsonProvider().parse(BOOKS_JSON);
        assertThat(json, hasJsonPath("$.store.name", equalTo("Little Shop")));
    }

    @Test
    public void shouldMatchJsonPathOnReadContext() {
        String test = "{\"foo\":\"bar\"}";
        ReadContext context = JsonPath.parse(test);
        assertThat(context, hasJsonPath("$.foo"));
        assertThat(context, hasJsonPath("$.foo", equalTo("bar")));
        assertThat(context, hasNoJsonPath("$.zoo"));
    }
}
