package com.jayway.jsonpath;

import org.junit.Test;

import static org.junit.Assert.assertNull;

/**
 * Check when the Option.SUPPRESS_EXCEPTIONS is set the fail operation will not return.
 */
public class ConfigurationTest {

    @Test
    public void nonexistant_property_returns_null_when_configured() {
        String document = "{}";
        Configuration config = Configuration.builder().options(Option.SUPPRESS_EXCEPTIONS).build();
        String nonExistentPath = "$.doesNotExist";

        assertNull(JsonPath.parse(document, config).read(nonExistentPath));
        assertNull(JsonPath.using(config).parse(document).read(nonExistentPath));
        assertNull(JsonPath.read(config.jsonProvider().parse(document), nonExistentPath));
    }

    @Test
    public void nonexistant_property_returns_null_when_configured_in_real_path() {
        String document = "{\"x\": [0,1,[0,1,2,3,null],null]}";
        Configuration config = Configuration.builder().options(Option.SUPPRESS_EXCEPTIONS).build();
        String nonExistentPath = "$.doesNotExist";

        assertNull(JsonPath.parse(document, config).read(nonExistentPath));
        assertNull(JsonPath.using(config).parse(document).read(nonExistentPath));
        assertNull(JsonPath.read(config.jsonProvider().parse(document), nonExistentPath));
    }


}
