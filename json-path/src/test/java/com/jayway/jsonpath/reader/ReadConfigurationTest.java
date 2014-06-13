package com.jayway.jsonpath.reader;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;
import com.jayway.jsonpath.spi.json.JsonProvider;
import com.jayway.jsonpath.spi.json.JsonProviderFactory;
import org.junit.Test;

public class ReadConfigurationTest {

    private static JsonProvider provider = JsonProviderFactory.createProvider();


    @Test
    public void fluent() {

        Configuration configuration = Configuration.defaultConfiguration();

        Configuration configuration2 = Configuration.builder()
                .jsonProvider(JsonProviderFactory.createProvider())
                .options(Option.THROW_ON_MISSING_PROPERTY).build();

        JsonProvider jsonProvider = JsonProviderFactory.createProvider();

        JsonPath.using(configuration).parse("{}").read("$");
        JsonPath.using(jsonProvider).parse("{}").read("$");

        JsonPath.parse("{}").read("$");
        JsonPath.parse("{}", configuration).read("$");

        JsonPath.using(configuration).parse("{}").read("$");
           /*
        Object updatedJsonModel = JsonPath.parse("{...}")
                          .write("$['store'][1]['name']", "new name")
                          .write("$.store[1].age", 43)
                          .getValue();
        */

    }
}
