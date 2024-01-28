package com.jayway.jsonpath.internal.filter;

import com.jayway.jsonpath.BaseTest;
import com.jayway.jsonpath.Predicate;
import com.jayway.jsonpath.internal.Path;
import com.jayway.jsonpath.internal.path.CompiledPath;
import com.jayway.jsonpath.internal.path.PathTokenFactory;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.HashMap;
import java.util.stream.Stream;

import static com.jayway.jsonpath.internal.filter.ValueNode.*;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class RegexpEvaluatorTest extends BaseTest {


    @ParameterizedTest
    @MethodSource("testData")
    public void should_evaluate_regular_expression(String regexp, ValueNode valueNode, boolean expectedResult) {
        //given
        Evaluator evaluator = EvaluatorFactory.createEvaluator(RelationalOperator.REGEX);
        ValueNode patternNode = createPatternNode(regexp);
        Predicate.PredicateContext ctx = createPredicateContext();

        //when
        boolean result = evaluator.evaluate(patternNode, valueNode, ctx);

        //then
        assertThat(result, is(equalTo(expectedResult)));
    }

    public static Stream<Arguments> testData() {
        return Stream.of(
                Arguments.arguments("/true|false/", createStringNode("true", true), true),
                Arguments.arguments("/9.*9/", createNumberNode("9979"), true),
                Arguments.arguments("/fa.*se/", createBooleanNode("false"), true),
                Arguments.arguments("/Eval.*or/", createClassNode(String.class), false),
                Arguments.arguments("/JsonNode/", createJsonNode(json()), false),
                Arguments.arguments("/PathNode/", createPathNode(path()), false),
                Arguments.arguments("/Undefined/", createUndefinedNode(), false),
                Arguments.arguments("/NullNode/", createNullNode(), false),
                Arguments.arguments("/test/i", createStringNode("tEsT", true), true),
                Arguments.arguments("/test/", createStringNode("tEsT", true), false),
                Arguments.arguments("/\u00de/ui", createStringNode("\u00fe", true), true),
                Arguments.arguments("/\u00de/", createStringNode("\u00fe", true), false),
                Arguments.arguments("/\u00de/i", createStringNode("\u00fe", true), false),
                Arguments.arguments("/test# code/", createStringNode("test", true), false),
                Arguments.arguments("/test# code/x", createStringNode("test", true), true),
                Arguments.arguments("/.*test.*/d", createStringNode("my\rtest", true), true),
                Arguments.arguments("/.*test.*/", createStringNode("my\rtest", true), false),
                Arguments.arguments("/.*tEst.*/is", createStringNode("test\ntest", true), true),
                Arguments.arguments("/.*tEst.*/i", createStringNode("test\ntest", true), false),
                Arguments.arguments("/^\\w+$/U", createStringNode("\u00fe", true), true),
                Arguments.arguments("/^\\w+$/", createStringNode("\u00fe", true), false)
        );
    }

    private static Path path() {
        return new CompiledPath(PathTokenFactory.createRootPathToken('$'), true);
    }

    private static String json() {
        return "{ 'some': 'JsonNode' }";
    }

    private Predicate.PredicateContext createPredicateContext() {
        return createPredicateContext(new HashMap<>());
    }

}
