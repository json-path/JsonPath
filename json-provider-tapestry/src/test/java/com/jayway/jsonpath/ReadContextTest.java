package com.jayway.jsonpath;

import org.assertj.core.api.Assertions;
import org.junit.Test;

import static com.jayway.jsonpath.JsonPath.using;

public class ReadContextTest extends BaseTest {

    public static final String JSON_BOOK_DOCUMENT =
            "[ " +
            "   \"category\" , \"reference\",\n" +
            "   \"author\" , \"Nigel Rees\",\n" +
            "   \"title\" , \"Sayings of the Century\",\n" +
            "   \"display-price\" , 8.95\n" +
            "]";
    
    @Test
    public void json_can_be_fetched_as_string() {
        String expected = "[\"category\",\"reference\",\"author\",\"NigelRees\",\"title\",\"SayingsoftheCentury\",\"display-price\",8.95]";
        String jsonString = using(CONFIGURATION).parse(JSON_BOOK_DOCUMENT).jsonString();        
        Assertions.assertThat(jsonString.replace("\n", "").replace(" ", "")).isEqualTo(expected);
    }

}
