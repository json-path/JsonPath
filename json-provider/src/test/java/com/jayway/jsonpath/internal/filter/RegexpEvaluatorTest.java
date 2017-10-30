package com.jayway.jsonpath.internal.filter;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.Arrays;

import org.assertj.core.util.Maps;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import com.jayway.jsonpath.BaseTestConfiguration;
import com.jayway.jsonpath.Predicate;
import com.jayway.jsonpath.internal.Path;
import com.jayway.jsonpath.internal.path.CompiledPath;
import com.jayway.jsonpath.internal.path.PathTokenFactory;
import com.jayway.jsonpath.spi.builder.JsonSmartNodeBuilder;
import com.jayway.jsonpath.spi.builder.NodeBuilder;

@RunWith(Parameterized.class)
public class RegexpEvaluatorTest
{
    private static NodeBuilder builder = new JsonSmartNodeBuilder();

    @Parameterized.Parameters(name="Regexp {0} for {1} node should evaluate to {2}")
    public static Iterable data() {
        return Arrays.asList(
            new Object[][]{
                { "/true|false/", builder.createStringNode("true", true),   true  },
                { "/9.*9/",       builder.createNumberNode("9979"),         true  },
                { "/fa.*se/",     builder.createBooleanNode("false"),       true  },
                { "/Eval.*or/",   builder.createClassNode(String.class),    false },
                { "/JsonNode/",   builder.createJsonNode(json()),           false },
                { "/PathNode/",   builder.createPathNode(path()),           false },
                { "/Undefined/",  builder.createUndefinedNode(),            false },
                { "/NullNode/",   builder.createNullNode(),                 false }
            }
        );
    }
    
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
        ValueNode patternNode = builder.createPatternNode(regexp);
        Predicate.PredicateContext ctx = createPredicateContext();

        //when
        boolean result = evaluator.evaluate(patternNode, valueNode, ctx);

        //then
        assertThat(result, is(equalTo(expectedResult)));
    }

    private static Path path() {
        return new CompiledPath(PathTokenFactory.createRootPathToken('$'), true);
    }

    private static String json() {
        return "{ 'some': 'JsonNode' }";
    }

    private Predicate.PredicateContext createPredicateContext() {
        return BaseTestConfiguration.createPredicateContext(Maps.newHashMap());
    }

}
