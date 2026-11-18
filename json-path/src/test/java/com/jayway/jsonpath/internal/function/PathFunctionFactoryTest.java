package com.jayway.jsonpath.internal.function;

import com.jayway.jsonpath.InvalidPathException;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PathFunctionFactoryTest {
    @Test
    void every_registered_function_creates_a_fresh_instance_of_its_documented_type() {
        PathFunctionFactory.FUNCTIONS.forEach((name, type) -> {
            PathFunction first = PathFunctionFactory.newFunction(name);
            PathFunction second = PathFunctionFactory.newFunction(name);
            assertThat(first).isInstanceOf(type).isNotSameAs(second);
        });
    }

    @Test
    void an_unknown_function_keeps_the_existing_error() {
        assertThat(assertThrows(InvalidPathException.class,
                () -> PathFunctionFactory.newFunction("missing")).getMessage())
                .isEqualTo("Function with name: missing does not exist.");
    }

    @Test
    void function_aliases_and_numeric_evaluation_still_work() {
        assertThat(JsonPath.<Integer>read("[1,2,3]", "$.length()")).isEqualTo(3);
        assertThat(JsonPath.<Integer>read("[1,2,3]", "$.size()")).isEqualTo(3);
        assertThat(JsonPath.<Double>read("[1,2,3]", "$.sum()")).isEqualTo(6.0);
    }
}
