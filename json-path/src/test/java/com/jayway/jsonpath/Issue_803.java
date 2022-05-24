package com.jayway.jsonpath;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

public class Issue_803 extends BaseTest {
    @Test
    public void testLinkedHashMap() {
        // Replace all whitespaces
        assertEquals(JSON_BOOK_STORE_DOCUMENT.replaceAll("\\s+",""),
                JsonPath.parse(JSON_BOOK_STORE_DOCUMENT).read("$", String.class).replaceAll("\\s+",""));
        // ReadContext.read escape HTML, so we replace "\\u002A" with "*" in JSON_DOCUMENT
        assertEquals(JSON_DOCUMENT.replaceAll("\\s+","").replace("\\u002A", "*"),
                JsonPath.parse(JSON_DOCUMENT).read("$", String.class).replaceAll("\\s+",""));
    }
}
