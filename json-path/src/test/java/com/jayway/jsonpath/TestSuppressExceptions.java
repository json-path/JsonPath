package com.jayway.jsonpath;

import com.jayway.jsonpath.spi.json.Jackson3JsonProvider;
import com.jayway.jsonpath.spi.json.JacksonJsonProvider;
import com.jayway.jsonpath.spi.mapper.Jackson3MappingProvider;
import com.jayway.jsonpath.spi.mapper.JacksonMappingProvider;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNull;


public class TestSuppressExceptions {

    @Test
    public void testSuppressExceptionsIsRespected() {
        ParseContext parseContext = JsonPath.using(
                new Configuration.ConfigurationBuilder().jsonProvider(new JacksonJsonProvider())
                        .mappingProvider(new JacksonMappingProvider()).options(Option.SUPPRESS_EXCEPTIONS)
                        .build());
        String json = "{}";
        assertNull(parseContext.parse(json).read(JsonPath.compile("$.missing")));
    }

    @Test
    public void testSuppressExceptionsIsRespectedPath() {
        ParseContext parseContext = JsonPath.using(
                new Configuration.ConfigurationBuilder().jsonProvider(new JacksonJsonProvider())
                        .mappingProvider(new JacksonMappingProvider()).options(Option.SUPPRESS_EXCEPTIONS, Option.AS_PATH_LIST)
                        .build());
        String json = "{}";

        List<String> result = parseContext.parse(json).read(JsonPath.compile("$.missing"));
        assertThat(result).isEmpty();
    }
    @Test
    public void testSuppressExceptionsIsRespectedJackson3() {
        ParseContext parseContext = JsonPath.using(
            new Configuration.ConfigurationBuilder().jsonProvider(new Jackson3JsonProvider())
                                                    .mappingProvider(new Jackson3MappingProvider()).options(Option.SUPPRESS_EXCEPTIONS)
                                                    .build());
        String json = "{}";
        assertNull(parseContext.parse(json).read(JsonPath.compile("$.missing")));
    }

    @Test
    public void testSuppressExceptionsIsRespectedPathJackson3() {
        ParseContext parseContext = JsonPath.using(
            new Configuration.ConfigurationBuilder().jsonProvider(new Jackson3JsonProvider())
                                                    .mappingProvider(new Jackson3MappingProvider()).options(Option.SUPPRESS_EXCEPTIONS, Option.AS_PATH_LIST)
                                                    .build());
        String json = "{}";

        List<String> result = parseContext.parse(json).read(JsonPath.compile("$.missing"));
        assertThat(result).isEmpty();
    }
}
