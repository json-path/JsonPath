package com.jayway.jsonpath;

import java.util.Arrays;
import java.util.List;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.StringNode;
import com.jayway.jsonpath.spi.json.Jackson3JsonNodeJsonProvider;
import com.jayway.jsonpath.spi.mapper.Jackson3MappingProvider;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import static org.assertj.core.api.Assertions.assertThat;

public class Jackson3JsonNodeJsonProviderMapperSupportTest {

    @ParameterizedTest
    @MethodSource("testDataSource")
    public void mapMethod_withJacksonJsonNodeJsonProvider_shouldUsingJsonNodeForMappingValues(TestData testData) {
        DocumentContext testJsonDocumentContext = cloneDocumentContext(testData.jsonDocumentContext);

        testJsonDocumentContext.map(testData.jsonPath, (value, config) -> {
            assertThat(value.getClass()).isEqualTo(testData.expectedJsonValueNodeType);
            return testData.newJsonValue;
        });
        assertThat((JsonNode) testJsonDocumentContext.json())
                .isEqualTo(testData.expectedUpdatedJsonDocument);
    }


    @ParameterizedTest
    @MethodSource("testDataSource")
    public void readMethod_withJacksonJsonNodeJsonProvider_shouldReturnJsonNode(TestData testData) {
        DocumentContext testJsonDocumentContext = cloneDocumentContext(testData.jsonDocumentContext);

        final JsonNode actualJsonValue = testJsonDocumentContext.read(testData.jsonPath);
        assertThat(actualJsonValue).isEqualTo(testData.expectedJsonValue);
    }


    @ParameterizedTest
    @MethodSource("testDataSource")
    public void setMethod_withJacksonJsonNodeJsonProvider_shouldAcceptJsonNode(TestData testData) {
        DocumentContext testJsonDocumentContext = cloneDocumentContext(testData.jsonDocumentContext);

        testJsonDocumentContext.set(testData.jsonPath, testData.newJsonValue);
        assertThat((JsonNode) testJsonDocumentContext.json())
                .isEqualTo(testData.expectedUpdatedJsonDocument);
    }

    private static class TestData {

        public final DocumentContext jsonDocumentContext;
        public final String jsonPath;
        public final JsonNode newJsonValue;
        public final JsonNode expectedJsonValue;
        public final Class<? extends JsonNode> expectedJsonValueNodeType;
        public final JsonNode expectedUpdatedJsonDocument;

        public TestData(
                DocumentContext jsonDocumentContext,
                String jsonPath,
                JsonNode newJsonValue,
                JsonNode expectedJsonValue,
                Class<? extends JsonNode> expectedJsonValueNodeType,
                JsonNode expectedUpdatedJsonDocument) {
            this.jsonDocumentContext = jsonDocumentContext;
            this.jsonPath = jsonPath;
            this.newJsonValue = newJsonValue;
            this.expectedJsonValue = expectedJsonValue;
            this.expectedJsonValueNodeType = expectedJsonValueNodeType;
            this.expectedUpdatedJsonDocument = expectedUpdatedJsonDocument;
        }
    }


    public static List<TestData> testDataSource() throws Exception {
        final Configuration configuration = Configuration.builder()
                .jsonProvider(new Jackson3JsonNodeJsonProvider())
                .mappingProvider(new Jackson3MappingProvider())
                .build();
        final ParseContext parseContext = JsonPath.using(configuration);
        final ObjectMapper objectMapper = new ObjectMapper();

        return Arrays.asList(
                // Single value JSON path
                new TestData(
                        parseContext.parse("{"
                                + "    \"attr1\":  \"val1\","
                                + "    \"attr2\":  \"val2\""
                                + "}"),
                        "$.attr1",
                        objectMapper.readTree("{\"attr1\": \"val1\"}"),
                        objectMapper.readTree("\"val1\""),
                        StringNode.class,
                        objectMapper.readTree("{"
                                + "    \"attr1\": {\"attr1\": \"val1\"},"
                                + "    \"attr2\": \"val2\""
                                + "}")),
                // Multi-value JSON path
                new TestData(
                        parseContext.parse("{"
                                + "    \"attr1\":  [\"val1\", \"val2\"],"
                                + "    \"attr2\":  \"val2\""
                                + "}"),
                        "$.attr1[*]",
                        objectMapper.readTree("{\"attr1\": \"val1\"}"),
                        objectMapper.readTree("[\"val1\", \"val2\"]"),
                        StringNode.class,
                        objectMapper.readTree("{"
                                + "    \"attr1\": [{\"attr1\": \"val1\"}, {\"attr1\": \"val1\"}],"
                                + "    \"attr2\": \"val2\""
                                + "}")),
                // Multi-value object JSON path
                new TestData(
                        parseContext.parse("{"
                                + "    \"attr1\":  ["
                                + "         {\"inAttr1\": \"val1a\", \"inAttr2\": \"val2a\"},"
                                + "         {\"inAttr1\": \"val1a\", \"inAttr2\": \"val2b\"},"
                                + "         {\"inAttr1\": \"val1b\", \"inAttr2\": \"val2c\"}"
                                + "    ],"
                                + "    \"attr2\":  \"val2\""
                                + "}"),
                        "$.attr1.[?(@.inAttr1 == \"val1a\")].inAttr2",
                        objectMapper.readTree("{\"attr1\": \"val1\"}"),
                        objectMapper.readTree("[\"val2a\", \"val2b\"]"),
                        StringNode.class,
                        objectMapper.readTree("{"
                                + "    \"attr1\": ["
                                + "         {\"inAttr1\": \"val1a\", \"inAttr2\": {\"attr1\": \"val1\"}},"
                                + "         {\"inAttr1\": \"val1a\", \"inAttr2\": {\"attr1\": \"val1\"}},"
                                + "         {\"inAttr1\": \"val1b\", \"inAttr2\": \"val2c\"}"
                                + "    ],"
                                + "    \"attr2\": \"val2\""
                                + "}"))
        );
    }

    private static DocumentContext cloneDocumentContext(DocumentContext documentContext) {
        return JsonPath.using(documentContext.configuration()).parse(documentContext.jsonString());
    }
}
