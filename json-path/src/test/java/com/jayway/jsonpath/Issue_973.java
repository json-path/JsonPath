package com.jayway.jsonpath;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

public class Issue_973 {
    @Test
    public void shouldNotCauseStackOverflow() {
        assertThatNoException().isThrownBy(() -> Criteria.parse("@[\"\",/\\"));
    }
}
