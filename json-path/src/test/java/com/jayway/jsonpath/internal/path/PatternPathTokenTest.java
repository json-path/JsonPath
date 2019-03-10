package com.jayway.jsonpath.internal.path;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

import static com.jayway.jsonpath.internal.path.PathCompiler.compile;
import static org.junit.Assert.*;

/**
 * Tests the parser for PatternPathTokens
 */
@RunWith(Parameterized.class)
public class PatternPathTokenTest {

    public PatternPathTokenTest(String pattern) {
        this.pattern = pattern;
    }

    @Parameterized.Parameters
    public static Collection<String> data() {
        return Arrays.asList(new String[] {
                "[/a/dixmsuU]","[/.*/x]","[/a.*/i]","[/].*/x]",
        });
    }

    final String prefix="$..";
    final String postfix = ".x.[?(@ == @)]";
    final String pattern;

    @Test
    public void patternPathTokenIsDetected() {
        CompiledPath compiled = (CompiledPath) compile(prefix+pattern+postfix);
        PathToken thirdToken = compiled.root.next().next();
        assertTrue(thirdToken instanceof PatternPathToken);
        PatternPathToken patternToken = (PatternPathToken) thirdToken;
        assertEquals(pattern, patternToken.getPathFragment());
        assertFalse(patternToken.isTokenDefinite());
    }
}
