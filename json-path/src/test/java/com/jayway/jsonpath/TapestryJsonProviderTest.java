package com.jayway.jsonpath;

import static com.jayway.jsonpath.JsonPath.using;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Map;

import org.apache.tapestry5.json.JSONArray;
import org.apache.tapestry5.json.JSONObject;
import org.junit.Test;

public class TapestryJsonProviderTest extends BaseTest {

  @Test
  public void an_object_can_be_read() {

    JSONObject book = using(TAPESTRY_JSON_CONFIGURATION).parse(JSON_DOCUMENT).read("$.store.book[0]");

    assertThat(book.get("author").toString()).isEqualTo("Nigel Rees");
  }

  @Test
  public void a_property_can_be_read() {

    String category = using(TAPESTRY_JSON_CONFIGURATION).parse(JSON_DOCUMENT).read("$.store.book[0].category");

    assertThat(category).isEqualTo("reference");
  }

  @Test
  public void a_filter_can_be_applied() {

    JSONArray fictionBooks = using(TAPESTRY_JSON_CONFIGURATION).parse(JSON_DOCUMENT)
        .read("$.store.book[?(@.category == 'fiction')]");

    assertThat(fictionBooks.length()).isEqualTo(3);
  }

  @Test
  public void result_can_be_mapped_to_object() {

    List<Map<String, Object>> books = using(TAPESTRY_JSON_CONFIGURATION).parse(JSON_DOCUMENT).read("$.store.book",
        List.class);

    assertThat(books.size()).isEqualTo(4);
  }

}
