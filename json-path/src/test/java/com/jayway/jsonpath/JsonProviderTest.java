package com.jayway.jsonpath;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import static com.jayway.jsonpath.JsonPath.using;
import static org.assertj.core.api.Assertions.assertThat;

public class JsonProviderTest extends BaseTest {

    public static Iterable<Configuration> configurations() {
        return Configurations.configurations();
    }


    @ParameterizedTest
    @MethodSource("configurations")
    public void strings_are_unwrapped(Configuration conf) {
        assertThat(using(conf).parse(JSON_DOCUMENT).read("$.string-property", String.class)).isEqualTo("string-value");
    }


    @ParameterizedTest
    @MethodSource("configurations")
    public void integers_are_unwrapped(Configuration conf) {
        assertThat(using(conf).parse(JSON_DOCUMENT).read("$.int-max-property", Integer.class)).isEqualTo(Integer.MAX_VALUE);
    }


    @ParameterizedTest
    @MethodSource("configurations")
    public void ints_are_unwrapped(Configuration conf) {
        assertThat(using(conf).parse(JSON_DOCUMENT).read("$.int-max-property", int.class)).isEqualTo(Integer.MAX_VALUE);
    }
}
