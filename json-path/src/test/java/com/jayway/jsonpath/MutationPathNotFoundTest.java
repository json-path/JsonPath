package com.jayway.jsonpath;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class MutationPathNotFoundTest {

    private static final String JSON = "{\"store\":{\"book\":[{\"category\":\"fiction\",\"price\":10},{\"category\":\"reference\",\"price\":20}]},\"empty\":[]}";
    private static final String UNMATCHED_FILTER_PATH = "$.store.book[?(@.price > 100)]";
    private static final String EMPTY_WILDCARD_PATH = "$.empty[*]";

    private String compiledPath(String path) {
        return JsonPath.compile(path).getPath();
    }

    private String expectedMessage(String path) {
        return "No results for path: " + compiledPath(path);
    }

    private Object freshDocument() {
        return JsonPath.parse(JSON).json();
    }

    @ParameterizedTest
    @ValueSource(strings = {UNMATCHED_FILTER_PATH, EMPTY_WILDCARD_PATH})
    public void set_throws_path_not_found_with_compiled_path(String path) {
        JsonPath jsonPath = JsonPath.compile(path);
        Object document = freshDocument();
        PathNotFoundException exception = assertThrows(PathNotFoundException.class,
                () -> jsonPath.set(document, "new", Configuration.defaultConfiguration()));
        assertThat(exception.getMessage()).isEqualTo(expectedMessage(path));
    }

    @ParameterizedTest
    @ValueSource(strings = {UNMATCHED_FILTER_PATH, EMPTY_WILDCARD_PATH})
    public void map_throws_path_not_found_with_compiled_path(String path) {
        JsonPath jsonPath = JsonPath.compile(path);
        Object document = freshDocument();
        PathNotFoundException exception = assertThrows(PathNotFoundException.class,
                () -> jsonPath.map(document, (currentValue, configuration) -> currentValue, Configuration.defaultConfiguration()));
        assertThat(exception.getMessage()).isEqualTo(expectedMessage(path));
    }

    @ParameterizedTest
    @ValueSource(strings = {UNMATCHED_FILTER_PATH, EMPTY_WILDCARD_PATH})
    public void delete_throws_path_not_found_with_compiled_path(String path) {
        JsonPath jsonPath = JsonPath.compile(path);
        Object document = freshDocument();
        PathNotFoundException exception = assertThrows(PathNotFoundException.class,
                () -> jsonPath.delete(document, Configuration.defaultConfiguration()));
        assertThat(exception.getMessage()).isEqualTo(expectedMessage(path));
    }

    @ParameterizedTest
    @ValueSource(strings = {UNMATCHED_FILTER_PATH, EMPTY_WILDCARD_PATH})
    public void add_throws_path_not_found_with_compiled_path(String path) {
        JsonPath jsonPath = JsonPath.compile(path);
        Object document = freshDocument();
        PathNotFoundException exception = assertThrows(PathNotFoundException.class,
                () -> jsonPath.add(document, "new", Configuration.defaultConfiguration()));
        assertThat(exception.getMessage()).isEqualTo(expectedMessage(path));
    }

    @ParameterizedTest
    @ValueSource(strings = {UNMATCHED_FILTER_PATH, EMPTY_WILDCARD_PATH})
    public void put_throws_path_not_found_with_compiled_path(String path) {
        JsonPath jsonPath = JsonPath.compile(path);
        Object document = freshDocument();
        PathNotFoundException exception = assertThrows(PathNotFoundException.class,
                () -> jsonPath.put(document, "key", "value", Configuration.defaultConfiguration()));
        assertThat(exception.getMessage()).isEqualTo(expectedMessage(path));
    }

    @Test
    public void set_with_suppress_exceptions_returns_empty_array_for_indefinite_path() {
        Configuration configuration = Configuration.defaultConfiguration().addOptions(Option.SUPPRESS_EXCEPTIONS);
        JsonPath jsonPath = JsonPath.compile(UNMATCHED_FILTER_PATH);
        Object document = freshDocument();
        Object result = jsonPath.set(document, "new", configuration);
        assertThat(result).isInstanceOf(List.class);
        assertThat((List<?>) result).isEmpty();
    }

    @Test
    public void set_with_suppress_exceptions_returns_null_for_definite_path_missing_ancestor() {
        Configuration configuration = Configuration.defaultConfiguration().addOptions(Option.SUPPRESS_EXCEPTIONS);
        JsonPath jsonPath = JsonPath.compile("$.missing.value");
        Object document = freshDocument();
        Object result = jsonPath.set(document, "new", configuration);
        assertThat(result).isNull();
    }

    @Test
    public void set_with_suppress_exceptions_and_as_path_list_returns_empty_array_for_indefinite_path() {
        Configuration configuration = Configuration.defaultConfiguration()
                .addOptions(Option.SUPPRESS_EXCEPTIONS, Option.AS_PATH_LIST);
        JsonPath jsonPath = JsonPath.compile(UNMATCHED_FILTER_PATH);
        Object document = freshDocument();
        Object result = jsonPath.set(document, "new", configuration);
        assertThat(result).isInstanceOf(List.class);
        assertThat((List<?>) result).isEmpty();
    }

    @Test
    public void set_with_suppress_exceptions_and_always_return_list_returns_empty_array_for_indefinite_path() {
        Configuration configuration = Configuration.defaultConfiguration()
                .addOptions(Option.SUPPRESS_EXCEPTIONS, Option.ALWAYS_RETURN_LIST);
        JsonPath jsonPath = JsonPath.compile(UNMATCHED_FILTER_PATH);
        Object document = freshDocument();
        Object result = jsonPath.set(document, "new", configuration);
        assertThat(result).isInstanceOf(List.class);
        assertThat((List<?>) result).isEmpty();
    }

    @Test
    public void set_with_suppress_exceptions_does_not_modify_document_for_indefinite_path() {
        Configuration configuration = Configuration.defaultConfiguration().addOptions(Option.SUPPRESS_EXCEPTIONS);
        JsonPath jsonPath = JsonPath.compile(UNMATCHED_FILTER_PATH);
        Object document = freshDocument();
        jsonPath.set(document, "new", configuration);
        Object expected = JsonPath.parse(JSON).json();
        assertThat(document).isEqualTo(expected);
    }

    @Test
    public void set_with_suppress_exceptions_does_not_modify_document_for_definite_path() {
        Configuration configuration = Configuration.defaultConfiguration().addOptions(Option.SUPPRESS_EXCEPTIONS);
        JsonPath jsonPath = JsonPath.compile("$.missing.value");
        Object document = freshDocument();
        jsonPath.set(document, "new", configuration);
        Object expected = JsonPath.parse(JSON).json();
        assertThat(document).isEqualTo(expected);
    }

    @Test
    public void set_with_suppress_exceptions_and_as_path_list_does_not_modify_document() {
        Configuration configuration = Configuration.defaultConfiguration()
                .addOptions(Option.SUPPRESS_EXCEPTIONS, Option.AS_PATH_LIST);
        JsonPath jsonPath = JsonPath.compile(UNMATCHED_FILTER_PATH);
        Object document = freshDocument();
        jsonPath.set(document, "new", configuration);
        Object expected = JsonPath.parse(JSON).json();
        assertThat(document).isEqualTo(expected);
    }

    @Test
    public void set_with_suppress_exceptions_and_always_return_list_does_not_modify_document() {
        Configuration configuration = Configuration.defaultConfiguration()
                .addOptions(Option.SUPPRESS_EXCEPTIONS, Option.ALWAYS_RETURN_LIST);
        JsonPath jsonPath = JsonPath.compile(UNMATCHED_FILTER_PATH);
        Object document = freshDocument();
        jsonPath.set(document, "new", configuration);
        Object expected = JsonPath.parse(JSON).json();
        assertThat(document).isEqualTo(expected);
    }
}
