package com.jayway.jsonpath.internal.filter;

import com.jayway.jsonpath.BaseTest;
import com.jayway.jsonpath.Predicate;
import com.jayway.jsonpath.internal.Path;
import com.jayway.jsonpath.internal.path.CompiledPath;
import com.jayway.jsonpath.internal.path.PathTokenFactory;
import org.assertj.core.util.Maps;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.HashMap;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import static com.jayway.jsonpath.internal.filter.ValueNode.*;

@RunWith(Parameterized.class)
public class RegexpEvaluatorTest extends BaseTest {

    private String regexp;
    private ValueNode valueNode;
    private boolean expectedResult;

    public RegexpEvaluatorTest(String regexp, ValueNode valueNode, boolean expectedResult) {
        this.regexp = regexp;
        this.valueNode = valueNode;
        this.expectedResult = expectedResult;
    }

    @Test
    public void should_evaluate_regular_expression() {
        //given
        Evaluator evaluator = EvaluatorFactory.createEvaluator(RelationalOperator.REGEX);
        ValueNode patternNode = createPatternNode(regexp);
        Predicate.PredicateContext ctx = createPredicateContext();

        //when
        boolean result = evaluator.evaluate(patternNode, valueNode, ctx);

        //then
        assertThat(result, is(equalTo(expectedResult)));
    }

    @Parameterized.Parameters(name="Regexp {0} for {1} node should evaluate to {2}")
    public static Iterable data() {
        return Arrays.asList(
            new Object[][]{
                { "/true|false/",      createStringNode("true", true),       true  },
                { "/9.*9/",            createNumberNode("9979"),             true  },
                { "/fa.*se/",          createBooleanNode("false"),           true  },
                { "/Eval.*or/",        createClassNode(String.class),        false },
                { "/JsonNode/",        createJsonNode(json()),               false },
                { "/PathNode/",        createPathNode(path()),               false },
                { "/Undefined/",       createUndefinedNode(),                false },
                { "/NullNode/",        createNullNode(),                     false },
                { "/test/i",           createStringNode("tEsT", true),       true  },
                { "/test/",            createStringNode("tEsT", true),       false },
                { "/\u00de/ui",        createStringNode("\u00fe", true),     true  },
                { "/\u00de/",          createStringNode("\u00fe", true),     false },
                { "/\u00de/i",         createStringNode("\u00fe", true),     false },
                { "/test# code/",      createStringNode("test", true),       false },
                { "/test# code/x",     createStringNode("test", true),       true  },
                { "/.*test.*/d",       createStringNode("my\rtest", true),   true  },
                { "/.*test.*/",        createStringNode("my\rtest", true),   false },
                { "/.*tEst.*/is",      createStringNode("test\ntest", true), true  },
                { "/.*tEst.*/i",       createStringNode("test\ntest", true), false },
                { "/^\\w+$/U",         createStringNode("\u00fe", true),     true  },
                { "/^\\w+$/",          createStringNode("\u00fe", true),     false },
                { "/^test$\\ntest$/m", createStringNode("test\ntest", true), true  }
            }
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
