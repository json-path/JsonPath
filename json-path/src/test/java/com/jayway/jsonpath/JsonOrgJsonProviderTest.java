package com.jayway.jsonpath;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;

import java.util.List;
import java.util.Map;

import static com.jayway.jsonpath.JsonPath.using;
import static org.assertj.core.api.Assertions.assertThat;

public class JsonOrgJsonProviderTest extends BaseTest {


    @Test
    public void an_object_can_be_read() {

        JSONObject book = using(JSON_ORG_CONFIGURATION).parse(JSON_DOCUMENT).read("$.store.book[0]");

        assertThat(book.get("author").toString()).isEqualTo("Nigel Rees");
    }

    @Test
    public void a_property_can_be_read() {

        String category = using(JSON_ORG_CONFIGURATION).parse(JSON_DOCUMENT).read("$.store.book[0].category");

        assertThat(category).isEqualTo("reference");
    }

    @Test
    public void a_filter_can_be_applied() {

        JSONArray fictionBooks = using(JSON_ORG_CONFIGURATION).parse(JSON_DOCUMENT).read("$.store.book[?(@.category == 'fiction')]");

        assertThat(fictionBooks.length()).isEqualTo(3);
    }

    @Test
    public void result_can_be_mapped_to_object() {

        List<Map<String, Object>> books = using(JSON_ORG_CONFIGURATION).parse(JSON_DOCUMENT).read("$.store.book", List.class);

        assertThat(books.size()).isEqualTo(4);
    }
}
