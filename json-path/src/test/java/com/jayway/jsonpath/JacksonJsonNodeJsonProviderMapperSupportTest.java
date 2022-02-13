package com.jayway.jsonpath;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.TextNode;
import com.jayway.jsonpath.spi.json.JacksonJsonNodeJsonProvider;
import com.jayway.jsonpath.spi.mapper.JacksonMappingProvider;
import java.util.Arrays;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@RunWith(Parameterized.class)
public class JacksonJsonNodeJsonProviderMapperSupportTest {

    private final TestData testData;

    public JacksonJsonNodeJsonProviderMapperSupportTest(final TestData testData) {
        this.testData = testData;
    }
    @Test
    public void mapMethod_withJacksonJsonNodeJsonProvider_shouldUsingJsonNodeForMappingValues() {
        DocumentContext testJsonDocumentContext = cloneDocumentContext(testData.jsonDocumentContext);

        testJsonDocumentContext.map(testData.jsonPath, (value, config) -> {
            assertThat(value.getClass()).isEqualTo(testData.expectedJsonValueNodeType);
            return testData.newJsonValue;
        });
        assertThat((JsonNode) testJsonDocumentContext.json())
                .isEqualTo(testData.expectedUpdatedJsonDocument);
    }

    @Test
    public void readMethod_withJacksonJsonNodeJsonProvider_shouldReturnJsonNode() {
        DocumentContext testJsonDocumentContext = cloneDocumentContext(testData.jsonDocumentContext);

        final JsonNode actualJsonValue = testJsonDocumentContext.read(testData.jsonPath);
        assertThat(actualJsonValue).isEqualTo(testData.expectedJsonValue);
    }

    @Test
    public void setMethod_withJacksonJsonNodeJsonProvider_shouldAcceptJsonNode() {
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

    @Parameterized.Parameters
    public static List<TestData> testDataSource() throws Exception {
        final Configuration configuration = Configuration.builder()
                .jsonProvider(new JacksonJsonNodeJsonProvider())
                .mappingProvider(new JacksonMappingProvider())
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
                        TextNode.class,
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
                        TextNode.class,
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
                        TextNode.class,
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
