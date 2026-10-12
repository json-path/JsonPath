package com.jayway.jsonpath.internal.function;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.Configurations;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.spi.json.JacksonJsonNodeJsonProvider;
import com.jayway.jsonpath.spi.json.JakartaJsonProvider;
import com.jayway.jsonpath.spi.mapper.JacksonMappingProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.List;

import static com.jayway.jsonpath.JsonPath.using;
import static org.assertj.core.api.Assertions.assertThat;

public class LiteralFunctionParameterTest extends BaseFunctionTest {

    public static Iterable<Configuration> configurations() {
        return Configurations.configurations();
    }

    public static Iterable<Configuration> scalarConfigurations() {
        List<Configuration> configurations = new ArrayList<>();
        for (Configuration conf : Configurations.configurations()) {
            // JSON-P parses only object and array roots, so it cannot parse scalar literals.
            if (!(conf.jsonProvider() instanceof JakartaJsonProvider)) {
                configurations.add(conf);
            }
        }
        return configurations;
    }

    public static Iterable<Configuration> mutableConfigurations() {
        List<Configuration> configurations = new ArrayList<>();
        for (Configuration conf : Configurations.configurations()) {
            configurations.add(conf.jsonProvider() instanceof JakartaJsonProvider
                    ? conf.jsonProvider(new JakartaJsonProvider(true)) : conf);
        }
        return configurations;
    }

    @Override
    protected void verifyFunction(Configuration conf, String pathExpr, String json, Object expectedValue) {
        Object document = conf.jsonProvider().parse(json);
        // Each configuration needs its own function parameters rather than a cached provider's late binding.
        Object result = JsonPath.compile(pathExpr).read(document, conf);
        assertThat(conf.jsonProvider().unwrap(result)).isEqualTo(expectedValue);
    }

    @Test
    public void concatenatesLiteralSeparatorWithJacksonJsonNodeProvider() {
        Configuration conf = Configuration.builder()
                .jsonProvider(new JacksonJsonNodeJsonProvider())
                .mappingProvider(new JacksonMappingProvider())
                .build();
        Object result = using(conf).parse("{\"one\":\"one\",\"two\":\"two\"}")
                .read("concat($.one, \"-\", $.two)");
        assertThat(result).isEqualTo("one-two");
    }

    @ParameterizedTest
    @MethodSource("scalarConfigurations")
    public void concatenatesLiteralSeparatorBetweenPathValues(Configuration conf) {
        verifyFunction(conf, "concat($.one, \"-\", $.two)",
                "{\"one\":\"one\",\"two\":\"two\"}", "one-two");
    }

    @ParameterizedTest
    @MethodSource("scalarConfigurations")
    public void concatenatesScalarStringLiteral(Configuration conf) {
        verifyFunction(conf, "$.concat(\"hello\")", "{}", "hello");
    }

    @ParameterizedTest
    @MethodSource("scalarConfigurations")
    public void consumesScalarNumberLiterals(Configuration conf) {
        verifyFunction(conf, "$.avg(1, 2)", "{}", 1.5);
    }

    @ParameterizedTest
    @MethodSource("scalarConfigurations")
    public void consumesLiteralIndex(Configuration conf) {
        verifyFunction(conf, "$.index(1)", "[\"a\",\"b\",\"c\"]", "b");
    }

    @ParameterizedTest
    @MethodSource("configurations")
    public void consumesStringArrayParameter(Configuration conf) {
        verifyFunction(conf, "$.concat($.values)", "{\"values\":[\"a\",\"b\"]}", "ab");
    }

    @ParameterizedTest
    @MethodSource("configurations")
    public void consumesNumberArrayParameter(Configuration conf) {
        verifyFunction(conf, "$.sum($.values)", "{\"values\":[1,2,3]}", 6.0);
    }

    @ParameterizedTest
    @MethodSource("scalarConfigurations")
    public void convertsScalarNumberLiteralsToStrings(Configuration conf) {
        verifyFunction(conf, "$.concat(1, 2.5)", "{}", "12.5");
    }

    @ParameterizedTest
    @MethodSource("configurations")
    public void consumesBooleanValues(Configuration conf) {
        verifyFunction(conf, "$.concat($.enabled, $.disabled)",
                "{\"enabled\":true,\"disabled\":false}", "truefalse");
    }

    @ParameterizedTest
    @MethodSource("configurations")
    public void consumesBooleanArrayParameterAndIgnoresNull(Configuration conf) {
        verifyFunction(conf, "$.concat($.values)", "{\"values\":[true,false,null]}", "truefalse");
    }

    @ParameterizedTest
    @MethodSource("scalarConfigurations")
    public void keepsLiteralStructureWhenAppending(Configuration conf) {
        Object document = conf.jsonProvider().parse("{\"target\":[0],\"values\":[3,4]}");
        Object result = JsonPath.compile("$.target.append(\"text\", 2, $.values, {\"value\":5})")
                .read(document, conf);
        assertThat(conf.jsonProvider().toJson(result)).isEqualTo("[0,\"text\",2,[3,4],{\"value\":5}]");
    }

    @ParameterizedTest
    @MethodSource("mutableConfigurations")
    public void keepsPathValueStructureWhenAppending(Configuration conf) {
        Object document = conf.jsonProvider().parse(
                "{\"target\":[0],\"text\":\"text\",\"number\":2,\"values\":[3,4]}");
        Object result = JsonPath.compile("$.target.append($.text, $.number, $.values, {\"value\":5})")
                .read(document, conf);
        assertThat(conf.jsonProvider().toJson(result)).isEqualTo("[0,\"text\",2,[3,4],{\"value\":5}]");
    }
}
