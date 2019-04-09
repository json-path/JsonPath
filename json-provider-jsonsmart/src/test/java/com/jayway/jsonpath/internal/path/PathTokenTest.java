package com.jayway.jsonpath.internal.path;

import com.jayway.jsonpath.BaseTest;
import org.junit.Test;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

public class PathTokenTest extends BaseTest {

    @Test
    public void is_upstream_definite_in_simple_case() {
        assertThat(makePathReturningTail(makePPT("foo")).isUpstreamDefinite()).isTrue();

        assertThat(makePathReturningTail(makePPT("foo"), makePPT("bar")).isUpstreamDefinite()).isTrue();

        assertThat(makePathReturningTail(makePPT("foo", "foo2"), makePPT("bar")).isUpstreamDefinite()).isFalse();

        assertThat(makePathReturningTail(new WildcardPathToken(), makePPT("bar")).isUpstreamDefinite()).isFalse();

        assertThat(makePathReturningTail(new ScanPathToken(), makePPT("bar")).isUpstreamDefinite()).isFalse();
    }

    @Test
    public void is_upstream_definite_in_complex_case() {
        assertThat(makePathReturningTail(makePPT("foo"), makePPT("bar"), makePPT("baz")).isUpstreamDefinite()).isTrue();

        assertThat(makePathReturningTail(makePPT("foo"), new WildcardPathToken()).isUpstreamDefinite()).isTrue();

        assertThat(makePathReturningTail(new WildcardPathToken(), makePPT("bar"), makePPT("baz")).isUpstreamDefinite()).isFalse();
    }


    private PathToken makePPT(final String ... properties) {
        return new PropertyPathToken(Arrays.asList(properties), '\'');
    }

    private PathToken makePathReturningTail(final PathToken ... tokens) {
        PathToken last = null;
        for (final PathToken token : tokens) {
            if (last != null) {
                last.appendTailToken(token);
            }
            last = token;
        }
        return last;
    }
}
