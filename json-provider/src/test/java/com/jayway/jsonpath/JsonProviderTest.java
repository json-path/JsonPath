package com.jayway.jsonpath;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static com.jayway.jsonpath.JsonPath.using;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(Parameterized.class)
public class JsonProviderTest extends BaseTestConfiguration {

    @Parameterized.Parameters
    public static Iterable<Configuration> configurations() {
        return BaseTestConfiguration.configurations();
    }
    
    private final Configuration conf;

    public JsonProviderTest(Configuration conf) {
        this.conf = conf;
    }

    @Test
    public void strings_are_unwrapped() {
        assertThat(using(conf).parse(BaseTestJson.JSON_DOCUMENT, false).read("$.string-property", String.class)).isEqualTo("string-value");
    }

    @Test
    public void integers_are_unwrapped() {
        assertThat(using(conf).parse(BaseTestJson.JSON_DOCUMENT, false).read("$.int-max-property", Integer.class)).isEqualTo(Integer.MAX_VALUE);
    }

    @Test
    public void ints_are_unwrapped() {
        assertThat(using(conf).parse(BaseTestJson.JSON_DOCUMENT, false).read("$.int-max-property", int.class)).isEqualTo(Integer.MAX_VALUE);
    }






}
