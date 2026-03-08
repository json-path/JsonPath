package com.jayway.jsonpath;

import org.junit.Test;

import static org.junit.Assert.assertNull;

/**
 * refer to issue #628
 * example of how to use user configuration correctly
 * use config.jsonProvider() to parse document will not have a result that related to this configuration.
 */
public class ConfigurationTest {

    /**
     * Nonexistant property returns null when configured.
     */
    @Test
    public void nonexistant_property_returns_null_when_configured() {
        String document = "{}";
        Configuration config = Configuration.builder().options(Option.SUPPRESS_EXCEPTIONS).build();
        String nonExistentPath = "$.doesNotExist";

        assertNull(JsonPath.parse(document, config).read(nonExistentPath));
        assertNull(JsonPath.using(config).parse(document).read(nonExistentPath));
        //This way can not pass config to JsonPath.read
        //assertNull(JsonPath.read(config.jsonProvider().parse(document), nonExistentPath));
    }

    /**
     * Nonexistant property returns null when configured in real path.
     */
    @Test
    public void nonexistant_property_returns_null_when_configured_in_real_path() {
        String document = "{\"x\": [0,1,[0,1,2,3,null],null]}";
        Configuration config = Configuration.builder().options(Option.SUPPRESS_EXCEPTIONS).build();
        String nonExistentPath = "$.doesNotExist";

        assertNull(JsonPath.parse(document, config).read(nonExistentPath));
        assertNull(JsonPath.using(config).parse(document).read(nonExistentPath));
        //This way can not pass config to JsonPath.read
        //assertNull(JsonPath.read(config.jsonProvider().parse(document), nonExistentPath));
    }
}
