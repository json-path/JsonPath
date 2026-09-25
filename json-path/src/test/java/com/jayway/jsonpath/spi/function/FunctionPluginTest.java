package com.jayway.jsonpath.spi.function;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.InvalidPathException;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.internal.EvaluationContext;
import com.jayway.jsonpath.internal.PathRef;
import com.jayway.jsonpath.internal.function.Parameter;
import com.jayway.jsonpath.internal.function.PathFunctionFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class FunctionPluginTest {

    private static final String JSON_DATA = "{\n" +
            "  \"text\": [\"hello\", \"world\"],\n" +
            "  \"singleText\": \"jsonpath\",\n" +
            "  \"numbers\": [1, 2, 3, 4, 5],\n" +
            "  \"items\": [\n" +
            "    {\"id\": 1, \"word\": \"racecar\", \"tags\": [\"a\", \"b\"]},\n" +
            "    {\"id\": 2, \"word\": \"apple\", \"tags\": [\"c\"]},\n" +
            "    {\"id\": 3, \"word\": \"level\", \"tags\": [\"a\", \"b\"]}\n" +
            "  ],\n" +
            "  \"user\": {\n" +
            "    \"firstName\": \"John\",\n" +
            "    \"lastName\": \"Doe\"\n" +
            "  },\n" +
            "  \"server\": {\n" +
            "    \"host\": \"localhost\",\n" +
            "    \"port\": 8080\n" +
            "  },\n" +
            "  \"fruits\": [\"apple\", \"banana\", \"cherry\"]\n" +
            "}";

    @AfterEach
    public void tearDown() {
        PathFunctionFactory.clearRegisteredFunctions();
    }

    /**
     * Custom function that reverses strings.
     */
    public static class ReverseFunction implements PathFunction {
        @Override
        public Object invoke(String currentPath, PathRef parent, Object model, EvaluationContext ctx, List<Parameter> parameters) {
            if (model instanceof String) {
                return new StringBuilder((String) model).reverse().toString();
            } else if (ctx.configuration().jsonProvider().isArray(model)) {
                List<String> reversed = new ArrayList<>();
                for (Object item : ctx.configuration().jsonProvider().toIterable(model)) {
                    if (item != null) {
                        reversed.add(new StringBuilder(item.toString()).reverse().toString());
                    }
                }
                return reversed;
            }
            return null;
        }
    }

    /**
     * Custom function that takes a multiplier parameter and multiplies elements.
     */
    public static class MultiplyFunction implements PathFunction {
        @Override
        public Object invoke(String currentPath, PathRef parent, Object model, EvaluationContext ctx, List<Parameter> parameters) {
            int factor = 1;
            if (parameters != null && !parameters.isEmpty()) {
                Object paramVal = parameters.get(0).getValue();
                if (paramVal instanceof Number) {
                    factor = ((Number) paramVal).intValue();
                } else if (paramVal instanceof String) {
                    factor = Integer.parseInt((String) paramVal);
                }
            }
            List<Integer> result = new ArrayList<>();
            if (ctx.configuration().jsonProvider().isArray(model)) {
                for (Object item : ctx.configuration().jsonProvider().toIterable(model)) {
                    if (item instanceof Number) {
                        result.add(((Number) item).intValue() * factor);
                    }
                }
            }
            return result;
        }
    }

    /**
     * Custom function implementing the deprecated internal PathFunction interface.
     */
    public static class LegacyCustomFunction implements com.jayway.jsonpath.internal.function.PathFunction {
        @Override
        public Object invoke(String currentPath, PathRef parent, Object model, EvaluationContext ctx, List<Parameter> parameters) {
            return "legacy-invoked";
        }
    }

    @Test
    public void testGlobalFunctionRegistrationByClass() {
        PathFunctionFactory.registerFunction("reverse", ReverseFunction.class);
        assertThat(PathFunctionFactory.hasFunction("reverse")).isTrue();
        assertThat(PathFunctionFactory.isRegistered("reverse")).isTrue();

        List<String> result = JsonPath.read(JSON_DATA, "$.text.reverse()");
        assertThat(result).containsExactly("olleh", "dlrow");
    }

    @Test
    public void testGlobalFunctionRegistrationByInstance() {
        PathFunctionFactory.registerFunction("shout", new PathFunction() {
            @Override
            public Object invoke(String currentPath, PathRef parent, Object model, EvaluationContext ctx, List<Parameter> parameters) {
                return model.toString().toUpperCase() + "!";
            }
        });

        Object result = JsonPath.read(JSON_DATA, "$.singleText.shout()");
        assertThat(result).isEqualTo("JSONPATH!");
    }

    @Test
    public void testGlobalFunctionUnregistration() {
        PathFunctionFactory.registerFunction("tempFunc", ReverseFunction.class);
        assertThat(PathFunctionFactory.hasFunction("tempFunc")).isTrue();

        boolean removed = PathFunctionFactory.unregisterFunction("tempFunc");
        assertThat(removed).isTrue();
        assertThat(PathFunctionFactory.hasFunction("tempFunc")).isFalse();

        assertThatThrownBy(() -> JsonPath.read(JSON_DATA, "$.text.tempFunc()"))
                .isInstanceOf(InvalidPathException.class)
                .hasMessageContaining("does not exist");
    }

    @Test
    public void testConfigurationScopedRegistrationByClass() {
        Configuration conf = Configuration.builder()
                .registerFunction("reverse", ReverseFunction.class)
                .build();

        // Should work with scoped configuration
        List<String> reversed = JsonPath.using(conf).parse(JSON_DATA).read("$.text.reverse()");
        assertThat(reversed).containsExactly("olleh", "dlrow");

        // Should fail on default configuration (no global pollution)
        assertThatThrownBy(() -> JsonPath.read(JSON_DATA, "$.text.reverse()"))
                .isInstanceOf(InvalidPathException.class)
                .hasMessageContaining("does not exist");
    }

    @Test
    public void testConfigurationScopedRegistrationByInstance() {
        Configuration conf = Configuration.defaultConfiguration()
                .registerFunction("multiply", new MultiplyFunction());

        List<Integer> multiplied = JsonPath.using(conf).parse(JSON_DATA).read("$.numbers.multiply(10)");
        assertThat(multiplied).containsExactly(10, 20, 30, 40, 50);

        // Does not affect defaultConfiguration
        assertThatThrownBy(() -> JsonPath.read(JSON_DATA, "$.numbers.multiply(10)"))
                .isInstanceOf(InvalidPathException.class)
                .hasMessageContaining("does not exist");
    }

    @Test
    public void testCustomFunctionWithParameters() {
        Configuration conf = Configuration.builder()
                .registerFunction("multiply", MultiplyFunction.class)
                .build();

        List<Integer> multiplied = JsonPath.using(conf).parse(JSON_DATA).read("$.numbers.multiply(3)");
        assertThat(multiplied).containsExactly(3, 6, 9, 12, 15);
    }

    @Test
    public void testCustomFunctionWithinPredicate() {
        Configuration conf = Configuration.builder()
                .registerFunction("reverse", ReverseFunction.class)
                .build();

        // Find items where reversed word equals original word (palindromes: racecar, level)
        List<Integer> palindromeIds = JsonPath.using(conf).parse(JSON_DATA).read("$.items[?(@.word == @.word.reverse())].id");
        assertThat(palindromeIds).containsExactly(1, 3);
    }

    @Test
    public void testCustomFunctionOverrideBuiltIn() {
        // Built-in min returns 1 on $.numbers.min()
        Number defaultMin = JsonPath.read(JSON_DATA, "$.numbers.min()");
        assertThat(defaultMin.doubleValue()).isEqualTo(1.0);

        // Override min in a scoped configuration to return a dummy value
        Configuration overrideConf = Configuration.builder()
                .registerFunction("min", new PathFunction() {
                    @Override
                    public Object invoke(String currentPath, PathRef parent, Object model, EvaluationContext ctx, List<Parameter> parameters) {
                        return -999;
                    }
                })
                .build();

        Object overriddenResult = JsonPath.using(overrideConf).parse(JSON_DATA).read("$.numbers.min()");
        assertThat(overriddenResult).isEqualTo(-999);

        // Default remains untouched
        Number defaultMinAfter = JsonPath.read(JSON_DATA, "$.numbers.min()");
        assertThat(defaultMinAfter.doubleValue()).isEqualTo(1.0);
    }

    @Test
    public void testCustomFunctionProvider() {
        FunctionProvider customProvider = new FunctionProvider() {
            @Override
            public PathFunction getFunction(String name) throws InvalidPathException {
                if ("dynamic".equals(name)) {
                    return (currentPath, parent, model, ctx, parameters) -> "dynamic-result";
                }
                return DefaultFunctionProvider.INSTANCE.getFunction(name);
            }

            @Override
            public boolean hasFunction(String name) {
                return "dynamic".equals(name) || DefaultFunctionProvider.INSTANCE.hasFunction(name);
            }
        };

        Configuration conf = Configuration.builder()
                .functionProvider(customProvider)
                .build();

        String result = JsonPath.using(conf).parse(JSON_DATA).read("$.singleText.dynamic()");
        assertThat(result).isEqualTo("dynamic-result");

        // Built-in functions still work through delegation
        Number min = JsonPath.using(conf).parse(JSON_DATA).read("$.numbers.min()");
        assertThat(min.doubleValue()).isEqualTo(1.0);
    }

    @Test
    public void testLegacyPathFunctionCompatibility() {
        PathFunctionFactory.registerFunction("legacy", LegacyCustomFunction.class);
        Object result = JsonPath.read(JSON_DATA, "$.singleText.legacy()");
        assertThat(result).isEqualTo("legacy-invoked");
    }

    @Test
    public void testDefaultFunctionProviderDirectUsage() {
        DefaultFunctionProvider provider = new DefaultFunctionProvider();
        provider.register("reverse", ReverseFunction.class);
        assertThat(provider.hasFunction("reverse")).isTrue();
        assertThat(provider.hasFunction("min")).isTrue(); // delegates to factory

        PathFunction fn = provider.getFunction("reverse");
        assertThat(fn).isInstanceOf(ReverseFunction.class);

        provider.unregister("reverse");
        assertThat(provider.getCustomFunctions()).doesNotContainKey("reverse");
    }

    @Test
    public void testConfigurationChainingPreservesFunctionProvider() {
        Configuration conf = Configuration.builder()
                .registerFunction("reverse", ReverseFunction.class)
                .build();

        // Chaining options or other providers shouldn't lose registered functions
        Configuration chained = conf.addOptions();
        List<String> reversed = JsonPath.using(chained).parse(JSON_DATA).read("$.text.reverse()");
        assertThat(reversed).containsExactly("olleh", "dlrow");
    }

    // =========================================================================
    // JoinFunction Tests
    // =========================================================================

    @Test
    public void testJoinFunctionDelimiterFirstWithPaths() {
        Configuration conf = Configuration.builder()
                .registerFunction("join", JoinFunction.class)
                .build();

        // Join two string paths with space delimiter
        String fullName = JsonPath.using(conf).parse(JSON_DATA)
                .read("$.join(\" \", $.user.firstName, $.user.lastName)");
        assertThat(fullName).isEqualTo("John Doe");

        // Join string and numeric paths with colon delimiter
        String hostPort = JsonPath.using(conf).parse(JSON_DATA)
                .read("$.join(\":\", $.server.host, $.server.port)");
        assertThat(hostPort).isEqualTo("localhost:8080");
    }

    @Test
    public void testJoinFunctionDelimiterFirstWithMultiValuePath() {
        Configuration conf = Configuration.builder()
                .registerFunction("join", JoinFunction.class)
                .build();

        // Path evaluates to a list of strings
        String fruits = JsonPath.using(conf).parse(JSON_DATA)
                .read("$.join(\", \", $.fruits[*])");
        assertThat(fruits).isEqualTo("apple, banana, cherry");
    }

    @Test
    public void testJoinFunctionDelimiterLast() {
        Configuration conf = Configuration.builder()
                .registerFunction("join", new JoinFunction())
                .build();

        String fullName = JsonPath.using(conf).parse(JSON_DATA)
                .read("$.join($.user.firstName, $.user.lastName, \" - \")");
        assertThat(fullName).isEqualTo("John - Doe");
    }

    @Test
    public void testJoinFunctionOnArray() {
        Configuration conf = Configuration.builder()
                .registerFunction("join", JoinFunction.class)
                .build();

        String joinedFruits = JsonPath.using(conf).parse(JSON_DATA)
                .read("$.fruits.join(\"-\")");
        assertThat(joinedFruits).isEqualTo("apple-banana-cherry");

        String joinedNumbers = JsonPath.using(conf).parse(JSON_DATA)
                .read("$.numbers.join(\",\")");
        assertThat(joinedNumbers).isEqualTo("1,2,3,4,5");
    }

    @Test
    public void testJoinFunctionInPredicate() {
        Configuration conf = Configuration.builder()
                .registerFunction("join", JoinFunction.class)
                .build();

        // Filter items where joining tags with "," equals "a,b" (items 1 and 3)
        List<Integer> itemIds = JsonPath.using(conf).parse(JSON_DATA)
                .read("$.items[?(@.tags.join(\",\") == 'a,b')].id");
        assertThat(itemIds).containsExactly(1, 3);
    }

    @Test
    public void testJoinFunctionGlobalRegistration() {
        PathFunctionFactory.registerFunction("join", JoinFunction.class);

        String fullName = JsonPath.read(JSON_DATA, "$.join(\" / \", $.user.firstName, $.user.lastName)");
        assertThat(fullName).isEqualTo("John / Doe");

        String fruits = JsonPath.read(JSON_DATA, "$.fruits.join(\" | \")");
        assertThat(fruits).isEqualTo("apple | banana | cherry");
    }
}
