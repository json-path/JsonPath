package com.jayway.jsonpath;

import org.junit.Before;
import org.junit.Test;

import static com.jayway.jsonpath.JsonPath.parse;
import static org.assertj.core.api.Assertions.assertThat;

public class CreateJsonFromPathTest {

    private String inputObject =  "{ }";
    Configuration pathConfiguration;

    @Before
    public void setup() {
        pathConfiguration = Configuration.builder()
                .options(Option.CREATE_MISSING_PROPERTIES_ON_DEFINITE_PATH).build();

    }

    @Test
    public void an_empty_root_can_be_updated() {

        String path = "$.id";
        JsonPath compiledPath = JsonPath.compile(path);
        Object output = compiledPath.set(pathConfiguration.jsonProvider().parse(inputObject),
                "12345",pathConfiguration);
        String result = parse(output).read(path);
        assertThat(result).matches("12345");

    }

    //String path2 = "$.shipment.extensionFields.carrier";
    @Test
    public void a_hierarchical_non_existent_path_can_be_created_and_value_set() {

        String path = "$.shipment.extensionFields.carrier";
        JsonPath compiledPath = JsonPath.compile(path);
        Object output = compiledPath.set(pathConfiguration.jsonProvider().parse(inputObject),
                "Alpha Transports",pathConfiguration);
        String result = parse(output).read(path);
        assertThat(result).matches("Alpha Transports");

    }

    @Test
    public void an_array_path_can_be_created_and_value_set() {

        String path = "$.shipment.extensionFields.shippers[1]";
        JsonPath compiledPath = JsonPath.compile(path);
        Object output = compiledPath.set(pathConfiguration.jsonProvider().parse(inputObject),
                "JohnDoe",pathConfiguration);
        String result = parse(output).read(path);
        /*
        DocumentContext jsonContext = JsonPath.parse(output);
        System.out.println("Document Created by JsonPaths:" + jsonContext.jsonString());
        */
        assertThat(result).matches("JohnDoe");

    }

    @Test
    public void an_array_path_followed_by_object_created_and_value_set() {

        String path = "$.shipment.extensionFields.drivers[0].officePhone";
        JsonPath compiledPath = JsonPath.compile(path);
        Object output = compiledPath.set(pathConfiguration.jsonProvider().parse(inputObject),
                123456,pathConfiguration);
        Integer result = parse(output).read(path);
        assertThat(result).isEqualTo(123456);
    }

    //String path11 = "$.shipment.extensionFields.drivers[1].extensions.homePhones[0]";
    @Test
    public void an_array_path_followed_by_object_followed_by_array_created_and_value_set() {

        String path = "$.shipment.extensionFields.drivers[0].extensions.homePhones[1]";
        JsonPath compiledPath = JsonPath.compile(path);
        Object output = compiledPath.set(pathConfiguration.jsonProvider().parse(inputObject),
                99999,pathConfiguration);
        Integer result = parse(output).read(path);
        assertThat(result).isEqualTo(99999);
    }

    @Test
    public void an_array_path_followed_by_object_with_noexistent_index_followed_by_array_path_created_and_value_set() {

        String path = "$.shipment.extensionFields.drivers[1].extensions.homePhones[0]";
        JsonPath compiledPath = JsonPath.compile(path);
        Object output = compiledPath.set(pathConfiguration.jsonProvider().parse(inputObject),
                1111,pathConfiguration);
        Integer result = parse(output).read(path);
        assertThat(result).isEqualTo(1111);
    }
}
