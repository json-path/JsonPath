package com.jayway.jsonpath;

import static com.jayway.jsonpath.JsonPath.using;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

public final class TestUtils {
    private TestUtils() {}

    public static void assertEvaluationThrows(final String json, final String path,
                                              Class<? extends JsonPathException> expected) {
        assertEvaluationThrows(json, path, expected, Configuration.defaultConfiguration());
    }

    /**
     * Shortcut for expected exception testing during path evaluation.
     *
     * @param json json to parse
     * @param path jsonpath do evaluate
     * @param expected expected exception class (reference comparison, not an instanceof)
     * @param conf conf to use during evaluation
     */
    public static void assertEvaluationThrows(final String json, final String path,
                                              Class<? extends JsonPathException> expected, final Configuration conf) {
        try {
            using(conf).parse(json).read(path);
            fail("Should throw " + expected.getName());
        } catch (JsonPathException exc) {
            if (exc.getClass() != expected)
                throw exc;
        }
    }

    /**
     * Assertion which requires empty list as a result of indefinite path search.
     * @param json json to be parsed
     * @param path path to be evaluated
     * @param conf conf to use during evaluation
     */
    public static void assertHasNoResults(final String json, final String path, Configuration conf) {
        assertHasResults(json, path, 0, conf);
    }

    /**
     * Assertion which requires list of one element as a result of indefinite path search.
     * @param json json to be parsed
     * @param path path to be evaluated
     */
    public static void assertHasOneResult(final String json, final String path, Configuration conf) {
        assertHasResults(json, path, 1, conf);
    }

    /**
     * Shortcut for counting found nodes.
     * @param json json to be parsed
     * @param path path to be evaluated
     * @param expectedResultCount expected number of nodes to be found
     * @param conf conf to use during evaluation
     */
    public static void assertHasResults(final String json, final String path, final int expectedResultCount, Configuration conf) {
        Object result = JsonPath.using(conf).parse(json).read(path);
        assertThat(conf.jsonProvider().length(result)).isEqualTo(expectedResultCount);
    }
}
