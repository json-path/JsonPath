package com.jayway.jsonpath;

import org.assertj.core.api.Assertions;
import org.junit.Test;

import static com.jayway.jsonpath.JsonPath.using;

public class ReadContextTest extends BaseTest {

    @Test
    public void json_can_be_fetched_as_string() {

        String expected = "{\"category\":\"reference\",\"author\":\"Nigel Rees\",\"title\":\"Sayings of the Century\",\"display-price\":8.95}";

        String jsonString1 = using(JSON_SMART_CONFIGURATION).parse(JSON_BOOK_DOCUMENT).jsonString();
        String jsonString2 = using(JACKSON_CONFIGURATION).parse(JSON_BOOK_DOCUMENT).jsonString();
        String jsonString3 = using(JACKSON_JSON_NODE_CONFIGURATION).parse(JSON_BOOK_DOCUMENT).jsonString();
        String jsonString4 = using(GSON_CONFIGURATION).parse(JSON_BOOK_DOCUMENT).jsonString();
        
        Assertions.assertThat(jsonString1).isEqualTo(expected);
        Assertions.assertThat(jsonString2).isEqualTo(expected);
        Assertions.assertThat(jsonString3).isEqualTo(expected);
        Assertions.assertThat(jsonString4).isEqualTo(expected);
    }

}
