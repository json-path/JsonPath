package com.jayway.jsonpath;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThatNoException;

public class Issue_970 {
    @Test
    public void shouldNotCauseStackOverflow() {
        assertThatNoException().isThrownBy(() -> Criteria.where("[']',"));
    }
}
