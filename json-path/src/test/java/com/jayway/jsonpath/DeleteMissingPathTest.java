package com.jayway.jsonpath;

import com.jayway.jsonpath.spi.json.*;
import org.junit.jupiter.api.Test;

public class DeleteMissingPathTest {

    private DocumentContext getDocumentContextFromProvider(JsonProvider jsonProvider) {

        Configuration configuration = Configuration.builder()
                .jsonProvider(jsonProvider)
                .options(Option.SUPPRESS_EXCEPTIONS)
                .build();

        return JsonPath.parse("{}", configuration);
    }

    @Test
    public void test_delete_missing_path_with_suppress_exceptions_does_not_throw_gson() {
        getDocumentContextFromProvider(new GsonJsonProvider())
                .delete("$..this..path..is..missing");
    }

    @Test
    public void test_delete_missing_path_with_suppress_exceptions_does_not_throw_jackson_json_node() {
        getDocumentContextFromProvider(new JacksonJsonNodeJsonProvider())
                .delete("$..this..path..is..missing");
    }

    @Test
    public void test_delete_missing_path_with_suppress_exceptions_does_not_throw_jackson() {
        getDocumentContextFromProvider(new JacksonJsonProvider())
                .delete("$..this..path..is..missing");
    }

    @Test
    public void test_delete_missing_path_with_suppress_exceptions_does_not_throw_jakarta() {
        getDocumentContextFromProvider(new JakartaJsonProvider())
                .delete("$..this..path..is..missing");
    }

    @Test
    public void test_delete_missing_path_with_suppress_exceptions_does_not_throw_jettison() {
        getDocumentContextFromProvider(new JettisonProvider())
                .delete("$..this..path..is..missing");
    }

    @Test
    public void test_delete_missing_path_with_suppress_exceptions_does_not_throw_json_org() {
        getDocumentContextFromProvider(new JsonOrgJsonProvider())
                .delete("$..this..path..is..missing");
    }

    @Test
    public void test_delete_missing_path_with_suppress_exceptions_does_not_throw_json_smart() {
        getDocumentContextFromProvider(new JsonSmartJsonProvider())
                .delete("$..this..path..is..missing");
    }

    @Test
    public void test_delete_missing_path_with_suppress_exceptions_does_not_throw_tapestry() {
        getDocumentContextFromProvider(new TapestryJsonProvider())
                .delete("$..this..path..is..missing");
    }
}
