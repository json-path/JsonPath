package com.jayway.jsonpath;

import com.jayway.jsonpath.internal.filter.FilterCompiler;
import org.junit.Test;

public class FilterCompilerTest {

    @Test
    public void filter_compiler_test() {

        FilterCompiler.compile("[?(@)]");

        FilterCompiler.compile("[?($)]");

        FilterCompiler.compile("[?(@.firstname)]");
        FilterCompiler.compile("[?(@.firstname)]");
        FilterCompiler.compile("[?($.firstname)]");
        FilterCompiler.compile("[?(@['firstname'])]");


        FilterCompiler.compile("[?($['firstname'].lastname)]");
        FilterCompiler.compile("[?($['firstname']['lastname'])]");

        FilterCompiler.compile("[?($['firstname']['lastname'].*)]");

        FilterCompiler.compile("[?($['firstname']['num_eq'] == 1)]");
        FilterCompiler.compile("[?($['firstname']['num_gt'] > 1.1)]");

        FilterCompiler.compile("[?($['firstname']['num_lt'] < 11.11)]");

        FilterCompiler.compile("[?($['firstname']['str_eq'] == 'hej')]");
        FilterCompiler.compile("[?($['firstname']['str_eq'] == '')]");

        FilterCompiler.compile("[?($['firstname']['str_eq'] == null)]");
        FilterCompiler.compile("[?($['firstname']['str_eq'] == true)]");
        FilterCompiler.compile("[?($['firstname']['str_eq'] == false)]");


        FilterCompiler.compile("[?(@.firstname && @.lastname)]");
        FilterCompiler.compile("[?((@.firstname || @.lastname) && @.and)]");

        FilterCompiler.compile("[?((@.a || @.b || @.c) && @.x)]");
        FilterCompiler.compile("[?((@.a && @.b && @.c) || @.x)]");
        FilterCompiler.compile("[?((@.a && @.b || @.c) || @.x)]");
        FilterCompiler.compile("[?((@.a && @.b) || (@.c && @.d))]");


        FilterCompiler.compile("[?(@.a IN [1,2,3])]");
        FilterCompiler.compile("[?(@.a IN {'foo':'bar'})]");
        FilterCompiler.compile("[?(@.value<'7')]");

        FilterCompiler.compile("[?(@.message == 'it\\\\')]");
    }
}
