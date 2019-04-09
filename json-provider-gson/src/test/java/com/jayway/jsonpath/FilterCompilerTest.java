package com.jayway.jsonpath;

import org.junit.Test;

import static com.jayway.jsonpath.internal.filter.FilterCompiler.compile;
import static org.assertj.core.api.Assertions.assertThat;

public class FilterCompilerTest {

    @Test
    public void valid_filters_compile() {
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
        assertThat(compile("[?((@.a && @.b || @.c) || @.x)]").toString()).isEqualTo("[?(((@['a'] && @['b']) || @['c']) || @['x'])]");
        assertThat(compile("[?((@.a && @.b) || (@.c && @.d))]").toString()).isEqualTo("[?((@['a'] && @['b']) || (@['c'] && @['d']))]");
        assertThat(compile("[?(@.a IN [1,2,3])]").toString()).isEqualTo("[?(@['a'] IN [1,2,3])]");
        assertThat(compile("[?(@.a IN {'foo':'bar'})]").toString()).isEqualTo("[?(@['a'] IN {'foo':'bar'})]");
        assertThat(compile("[?(@.value<'7')]").toString()).isEqualTo("[?(@['value'] < '7')]");
        assertThat(compile("[?(@.message == 'it\\\\')]").toString()).isEqualTo("[?(@['message'] == 'it\\\\')]");
        assertThat(compile("[?(@.message.min() > 10)]").toString()).isEqualTo("[?(@['message'].min() > 10)]");
        assertThat(compile("[?(@.message.min()==10)]").toString()).isEqualTo("[?(@['message'].min() == 10)]");
        assertThat(compile("[?(10 == @.message.min())]").toString()).isEqualTo("[?(10 == @['message'].min())]");
        assertThat(compile("[?(((@)))]").toString()).isEqualTo("[?(@)]");
        assertThat(compile("[?(@.name =~ /.*?/i)]").toString()).isEqualTo("[?(@['name'] =~ /.*?/i)]");
        assertThat(compile("[?(@.name =~ /.*?/)]").toString()).isEqualTo("[?(@['name'] =~ /.*?/)]");
        assertThat(compile("[?($[\"firstname\"][\"lastname\"])]").toString()).isEqualTo("[?($[\"firstname\"][\"lastname\"])]");
        assertThat(compile("[?($[\"firstname\"].lastname)]").toString()).isEqualTo("[?($[\"firstname\"]['lastname'])]");
        assertThat(compile("[?($[\"firstname\", \"lastname\"])]").toString()).isEqualTo("[?($[\"firstname\",\"lastname\"])]");
        assertThat(compile("[?(((@.a && @.b || @.c)) || @.x)]").toString()).isEqualTo("[?(((@['a'] && @['b']) || @['c']) || @['x'])]");

    }

    @Test
    public void string_quote_style_is_serialized() {
        assertThat(compile("[?('apa' == 'apa')]").toString()).isEqualTo("[?('apa' == 'apa')]");
        assertThat(compile("[?('apa' == \"apa\")]").toString()).isEqualTo("[?('apa' == \"apa\")]");
    }

    @Test
    public void string_can_contain_path_chars() {
        assertThat(compile("[?(@[')]@$)]'] == ')]@$)]')]").toString()).isEqualTo("[?(@[')]@$)]'] == ')]@$)]')]");
        assertThat(compile("[?(@[\")]@$)]\"] == \")]@$)]\")]").toString()).isEqualTo("[?(@[\")]@$)]\"] == \")]@$)]\")]");
    }

    @Test(expected = InvalidPathException.class)
    public void invalid_path_when_string_literal_is_unquoted() {
        compile("[?(@.foo == x)]");
    }

    @Test
    public void or_has_lower_priority_than_and() {
        assertThat(compile("[?(@.category == 'fiction' && @.author == 'Evelyn Waugh' || @.price > 15)]").toString())
                .isEqualTo("[?((@['category'] == 'fiction' && @['author'] == 'Evelyn Waugh') || @['price'] > 15)]");
    }

    @Test
    public void invalid_filters_does_not_compile() {
        assertInvalidPathException("[?(@))]");
        assertInvalidPathException("[?(@ FOO 1)]");
        assertInvalidPathException("[?(@ || )]");
        assertInvalidPathException("[?(@ == 'foo )]");
        assertInvalidPathException("[?(@ == 1' )]");
        assertInvalidPathException("[?(@.foo bar == 1)]");
        assertInvalidPathException("[?(@.i == 5 @.i == 8)]");
        assertInvalidPathException("[?(!5)]");
        assertInvalidPathException("[?(!'foo')]");
    }

    @Test
    // issue #178
    public void compile_and_serialize_not_exists_filter(){
        Filter compiled = compile("[?(!@.foo)]");
        String serialized = compiled.toString();
        assertThat(serialized).isEqualTo("[?(!@['foo'])]");
    }



    private void assertInvalidPathException(String filter){
        try {
            compile(filter);
            throw new AssertionError("Expected " + filter + " to throw InvalidPathException");
        } catch (InvalidPathException e){
            //e.printStackTrace();
        }
    }
}
