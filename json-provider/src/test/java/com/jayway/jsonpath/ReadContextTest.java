package com.jayway.jsonpath;

import org.assertj.core.api.Assertions;
import org.junit.Test;

import static com.jayway.jsonpath.JsonPath.using;

public class ReadContextTest extends BaseTestConfiguration {

    @Test
    public void json_can_be_fetched_as_string() {

        String expected = "{\"category\":\"reference\",\"author\":\"Nigel Rees\",\"title\":\"Sayings of the Century\",\"display-price\":8.95}";

        String jsonString1 = using(JSON_SMART_CONFIGURATION).parse(BaseTestJson.JSON_BOOK_DOCUMENT, false).jsonString();
        String jsonString2 = using(JACKSON_CONFIGURATION).parse(BaseTestJson.JSON_BOOK_DOCUMENT, false).jsonString();
        String jsonString3 = using(JACKSON_JSON_NODE_CONFIGURATION).parse(BaseTestJson.JSON_BOOK_DOCUMENT, false).jsonString();
        String jsonString4 = using(GSON_CONFIGURATION).parse(BaseTestJson.JSON_BOOK_DOCUMENT, false).jsonString();
        
        Assertions.assertThat(jsonString1).isEqualTo(expected);
        Assertions.assertThat(jsonString2).isEqualTo(expected);
        Assertions.assertThat(jsonString3).isEqualTo(expected);
        Assertions.assertThat(jsonString4).isEqualTo(expected);
    }

}
