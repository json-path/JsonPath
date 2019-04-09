package com.jayway.jsonpath;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static com.jayway.jsonpath.JsonPath.using;
import static org.assertj.core.api.Assertions.assertThat;

public class JsonProviderTest extends BaseTest {

    @Test
    public void strings_are_unwrapped() {
        assertThat(using(CONFIGURATION).parse(JSON_DOCUMENT).read("$.string-property", String.class)).isEqualTo("string-value");
    }

    @Test
    public void integers_are_unwrapped() {
        assertThat(using(CONFIGURATION).parse(JSON_DOCUMENT).read("$.int-max-property", Integer.class)).isEqualTo(Integer.MAX_VALUE);
    }

    @Test
    public void ints_are_unwrapped() {
        assertThat(using(CONFIGURATION).parse(JSON_DOCUMENT).read("$.int-max-property", int.class)).isEqualTo(Integer.MAX_VALUE);
    }






}
