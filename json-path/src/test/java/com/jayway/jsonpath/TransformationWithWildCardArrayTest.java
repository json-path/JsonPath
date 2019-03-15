package com.jayway.jsonpath;

import com.jayway.jsonpath.spi.transformer.TransformationSpec;
import org.junit.Before;
import org.junit.Test;

import java.io.InputStream;
import java.nio.charset.Charset;

public class TransformationWithWildCardArrayTest {


    InputStream sourceStream;
    InputStream transformSpec;
    Configuration configuration;
    TransformationSpec spec;
    Object sourceJson;

    @Before
    public void setup() {
        configuration = Configuration.builder()
                .options(Option.CREATE_MISSING_PROPERTIES_ON_DEFINITE_PATH).build();
        sourceStream = this.getClass().getClassLoader().getResourceAsStream(
                "transforms/goessner_example.json");
        sourceJson = configuration.jsonProvider().parse(
                sourceStream, Charset.defaultCharset().name());

        DocumentContext jsonContext = JsonPath.parse(sourceJson);
        System.out.println("Document Input :" + jsonContext.jsonString());

        transformSpec = this.getClass().getClassLoader().getResourceAsStream(
                "transforms/goessner_example_wildcard_transform_spec.json");

        spec = configuration.transformationProvider().spec(transformSpec, configuration);

    }

    @Test
    public void transform_spec_with_wildcard_array_test() {

        Object transformed = configuration.transformationProvider()
                .transform(sourceJson, spec, configuration);
        DocumentContext jsonContext = JsonPath.parse(transformed);
        System.out.println("Document Created by Transformation:" + jsonContext.jsonString());
    }

}
