package com.jayway.jsonpath.internal.function;

import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.JsonPathException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SequenceBoundaryTest {
    @Test
    void lastOfSingleton() {
        Object result = JsonPath.read("[42]", "$.last()");
        assertThat(result).isEqualTo(42);
    }

    @Test
    void negativeIndexCanSelectFirstElement() {
        Object result = JsonPath.read("[1,2,3]", "$.index(-3)");
        assertThat(result).isEqualTo(1);
    }

    @Test
    void negativeIndexBeforeStartStillFails() {
        assertThatThrownBy(() -> JsonPath.read("[1,2,3]", "$.index(-4)"))
            .isInstanceOf(JsonPathException.class);
    }
}
