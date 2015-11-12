package com.jayway.jsonpath;

import org.junit.Test;

import static com.jayway.jsonpath.internal.filter.FilterCompiler.compile;
import static org.assertj.core.api.Assertions.assertThat;

public class FilterCompilerTest {


    @Test
    public void filter_compiler_test() {
        assertThat(compile("[?(@)]").toString()).isEqualTo("[?(@)]");
        assertThat(compile("[?(@)]").toString()).isEqualTo("[?(@)]");
        assertThat(compile("[?(@.firstname)]").toString()).isEqualTo("[?(@['firstname'])]");
        assertThat(compile("[?($.firstname)]").toString()).isEqualTo("[?($['firstname'])]");
        assertThat(compile("[?(@['firstname'])]").toString()).isEqualTo("[?(@['firstname'])]");
        assertThat(compile("[?($['firstname'].lastname)]").toString()).isEqualTo("[?($['firstname']['lastname'])]");
        assertThat(compile("[?($['firstname']['lastname'])]").toString()).isEqualTo("[?($['firstname']['lastname'])]");
        assertThat(compile("[?($['firstname']['lastname'].*)]").toString()).isEqualTo("[?($['firstname']['lastname'][*])]");
        assertThat(compile("[?($['firstname']['num_eq'] == 1)]").toString()).isEqualTo("[?($['firstname']['num_eq'] == 1)]");
        assertThat(compile("[?($['firstname']['num_gt'] > 1.1)]").toString()).isEqualTo("[?($['firstname']['num_gt'] > 1.1)]");
        assertThat(compile("[?($['firstname']['num_lt'] < 11.11)]").toString()).isEqualTo("[?($['firstname']['num_lt'] < 11.11)]");
        assertThat(compile("[?($['firstname']['str_eq'] == 'hej')]").toString()).isEqualTo("[?($['firstname']['str_eq'] == 'hej')]");
        assertThat(compile("[?($['firstname']['str_eq'] == '')]").toString()).isEqualTo("[?($['firstname']['str_eq'] == '')]");
        assertThat(compile("[?($['firstname']['str_eq'] == null)]").toString()).isEqualTo("[?($['firstname']['str_eq'] == null)]");
        assertThat(compile("[?($['firstname']['str_eq'] == true)]").toString()).isEqualTo("[?($['firstname']['str_eq'] == true)]");
        assertThat(compile("[?($['firstname']['str_eq'] == false)]").toString()).isEqualTo("[?($['firstname']['str_eq'] == false)]");
        assertThat(compile("[?(@.firstname && @.lastname)]").toString()).isEqualTo("[?(@['firstname'] && @['lastname'])]");
        assertThat(compile("[?((@.firstname || @.lastname) && @.and)]").toString()).isEqualTo("[?((@['firstname'] || @['lastname']) && @['and'])]");
        assertThat(compile("[?((@.a || @.b || @.c) && @.x)]").toString()).isEqualTo("[?((@['a'] || @['b'] || @['c']) && @['x'])]");
        assertThat(compile("[?((@.a && @.b && @.c) || @.x)]").toString()).isEqualTo("[?((@['a'] && @['b'] && @['c']) || @['x'])]");
        assertThat(compile("[?((@.a && @.b || @.c) || @.x)]").toString()).isEqualTo("[?((@['a'] && (@['b'] || @['c'])) || @['x'])]");
        assertThat(compile("[?((@.a && @.b) || (@.c && @.d))]").toString()).isEqualTo("[?((@['a'] && @['b']) || (@['c'] && @['d']))]");
        assertThat(compile("[?(@.a IN [1,2,3])]").toString()).isEqualTo("[?(@['a'] IN [1,2,3])]");
        assertThat(compile("[?(@.a IN {'foo':'bar'})]").toString()).isEqualTo("[?(@['a'] IN {'foo':'bar'})]");
        assertThat(compile("[?(@.value<'7')]").toString()).isEqualTo("[?(@['value'] < '7')]");
        assertThat(compile("[?(@.message == 'it\\\\')]").toString()).isEqualTo("[?(@['message'] == 'it\\\\')]");
    }

}
