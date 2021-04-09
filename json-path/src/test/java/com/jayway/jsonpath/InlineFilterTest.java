package com.jayway.jsonpath;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.ArrayList;
import java.util.List;

import static com.jayway.jsonpath.JsonPath.using;
import static com.jayway.jsonpath.TestUtils.assertHasNoResults;
import static com.jayway.jsonpath.TestUtils.assertHasOneResult;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(Parameterized.class)
public class InlineFilterTest extends BaseTest {

    private static int bookCount = 4;


    private Configuration conf = Configurations.GSON_CONFIGURATION;

    public InlineFilterTest(Configuration conf) {
        this.conf = conf;
    }

    @Parameterized.Parameters
    public static Iterable<Configuration> configurations() {
        return Configurations.configurations();
    }

    @Test
    public void root_context_can_be_referred_in_predicate() {
        List<Double> prices = using(conf).parse(JSON_DOCUMENT).read("store.book[?(@.display-price <= $.max-price)].display-price", List.class);

        assertThat(prices).containsAll(asList(8.95D, 8.99D));
    }

    @Test
    public void multiple_context_object_can_be_refered() {

        List all = using(conf).parse(JSON_DOCUMENT).read("store.book[ ?(@.category == @.category) ]", List.class);
        assertThat(all.size()).isEqualTo(bookCount);

        List all2 = using(conf).parse(JSON_DOCUMENT).read("store.book[ ?(@.category == @['category']) ]", List.class);
        assertThat(all2.size()).isEqualTo(bookCount);

        List all3 = using(conf).parse(JSON_DOCUMENT).read("store.book[ ?(@ == @) ]", List.class);
        assertThat(all3.size()).isEqualTo(bookCount);

        List none = using(conf).parse(JSON_DOCUMENT).read("store.book[ ?(@.category != @.category) ]", List.class);
        assertThat(none.size()).isEqualTo(0);

        List none2 = using(conf).parse(JSON_DOCUMENT).read("store.book[ ?(@.category != @) ]", List.class);
        assertThat(none2.size()).isEqualTo(4);

    }

    @Test
    public void simple_inline_or_statement_evaluates() {

        List a = using(conf).parse(JSON_DOCUMENT).read("store.book[ ?(@.author == 'Nigel Rees' || @.author == 'Evelyn Waugh') ].author", List.class);
        assertThat(a).containsExactly("Nigel Rees", "Evelyn Waugh");

        List b = using(conf).parse(JSON_DOCUMENT).read("store.book[ ?((@.author == 'Nigel Rees' || @.author == 'Evelyn Waugh') && @.display-price < 15) ].author", List.class);
        assertThat(b).containsExactly("Nigel Rees", "Evelyn Waugh");

        List c = using(conf).parse(JSON_DOCUMENT).read("store.book[ ?((@.author == 'Nigel Rees' || @.author == 'Evelyn Waugh') && @.category == 'reference') ].author", List.class);
        assertThat(c).containsExactly("Nigel Rees");

        List d = using(conf).parse(JSON_DOCUMENT).read("store.book[ ?((@.author == 'Nigel Rees') || (@.author == 'Evelyn Waugh' && @.category != 'fiction')) ].author", List.class);
        assertThat(d).containsExactly("Nigel Rees");
    }

    @Test
    public void no_path_ref_in_filter_hit_all() {

        List<String> res = JsonPath.parse(JSON_DOCUMENT).read("$.store.book[?('a' == 'a')].author");

        assertThat(res).containsExactly("Nigel Rees", "Evelyn Waugh", "Herman Melville", "J. R. R. Tolkien");

    }

    @Test
    public void no_path_ref_in_filter_hit_none() {

        List<String> res = JsonPath.parse(JSON_DOCUMENT).read("$.store.book[?('a' == 'b')].author");

        assertThat(res).isEmpty();

    }

    @Test
    public void path_can_be_on_either_side_of_operator() {
        List<String> resLeft = JsonPath.parse(JSON_DOCUMENT).read("$.store.book[?(@.category == 'reference')].author");
        List<String> resRight = JsonPath.parse(JSON_DOCUMENT).read("$.store.book[?('reference' == @.category)].author");

        assertThat(resLeft).containsExactly("Nigel Rees");
        assertThat(resRight).containsExactly("Nigel Rees");
    }

    @Test
    public void path_can_be_on_both_side_of_operator() {
        List<String> res = JsonPath.parse(JSON_DOCUMENT).read("$.store.book[?(@.category == @.category)].author");

        assertThat(res).containsExactly("Nigel Rees", "Evelyn Waugh", "Herman Melville", "J. R. R. Tolkien");
    }

    @Test
    public void patterns_can_be_evaluated() {
        List<String> resLeft = JsonPath.parse(JSON_DOCUMENT).read("$.store.book[?(@.category =~ /reference/)].author");
        assertThat(resLeft).containsExactly("Nigel Rees");

        resLeft = JsonPath.parse(JSON_DOCUMENT).read("$.store.book[?(/reference/ =~ @.category)].author");
        assertThat(resLeft).containsExactly("Nigel Rees");
    }

    @Test
    public void patterns_can_be_evaluated_with_ignore_case() {
        List<String> resLeft = JsonPath.parse(JSON_DOCUMENT).read("$.store.book[?(@.category =~ /REFERENCE/)].author");
        assertThat(resLeft).isEmpty();

        resLeft = JsonPath.parse(JSON_DOCUMENT).read("$.store.book[?(@.category =~ /REFERENCE/i)].author");
        assertThat(resLeft).containsExactly("Nigel Rees");
    }

    @Test
    public void negate_exists_check() {
        List<String> hasIsbn = JsonPath.parse(JSON_DOCUMENT).read("$.store.book[?(@.isbn)].author");
        assertThat(hasIsbn).containsExactly("Herman Melville", "J. R. R. Tolkien");

        List<String> noIsbn = JsonPath.parse(JSON_DOCUMENT).read("$.store.book[?(!@.isbn)].author");

        assertThat(noIsbn).containsExactly("Nigel Rees", "Evelyn Waugh");
    }

    @Test
    public void negate_exists_check_primitive() {
        List<Integer> ints = new ArrayList<Integer>();
        ints.add(0);
        ints.add(1);
        ints.add(null);
        ints.add(2);
        ints.add(3);


        List<Integer> hits = JsonPath.parse(ints).read("$[?(@)]");
        assertThat(hits).containsExactly(0,1,null,2,3);

        hits = JsonPath.parse(ints).read("$[?(@ != null)]");
        assertThat(hits).containsExactly(0,1,2,3);

        List<Integer> isNull = JsonPath.parse(ints).read("$[?(!@)]");
        assertThat(isNull).containsExactly(new Integer[]{});
        assertThat(isNull).containsExactly(new Integer[]{});
    }

    @Test
    public void equality_check_does_not_break_evaluation() {
        assertHasOneResult("[{\"value\":\"5\"}]", "$[?(@.value=='5')]", conf);
        assertHasOneResult("[{\"value\":5}]", "$[?(@.value==5)]", conf);

        assertHasOneResult("[{\"value\":\"5.1.26\"}]", "$[?(@.value=='5.1.26')]", conf);

        assertHasNoResults("[{\"value\":\"5\"}]", "$[?(@.value=='5.1.26')]", conf);
        assertHasNoResults("[{\"value\":5}]", "$[?(@.value=='5.1.26')]", conf);
        assertHasNoResults("[{\"value\":5.1}]", "$[?(@.value=='5.1.26')]", conf);

        assertHasNoResults("[{\"value\":\"5.1.26\"}]", "$[?(@.value=='5')]", conf);
        assertHasNoResults("[{\"value\":\"5.1.26\"}]", "$[?(@.value==5)]", conf);
        assertHasNoResults("[{\"value\":\"5.1.26\"}]", "$[?(@.value==5.1)]", conf);
    }

    @Test
    public void lt_check_does_not_break_evaluation() {
        assertHasOneResult("[{\"value\":\"5\"}]", "$[?(@.value<'7')]", conf);

        assertHasNoResults("[{\"value\":\"7\"}]", "$[?(@.value<'5')]", conf);

        assertHasOneResult("[{\"value\":5}]", "$[?(@.value<7)]", conf);
        assertHasNoResults("[{\"value\":7}]", "$[?(@.value<5)]", conf);

        assertHasOneResult("[{\"value\":5}]", "$[?(@.value<7.1)]", conf);
        assertHasNoResults("[{\"value\":7}]", "$[?(@.value<5.1)]", conf);

        assertHasOneResult("[{\"value\":5.1}]", "$[?(@.value<7)]", conf);
        assertHasNoResults("[{\"value\":7.1}]", "$[?(@.value<5)]", conf);
    }

    @Test
    public void escaped_literals() {
        if(conf.jsonProvider().getClass().getSimpleName().startsWith("Jackson")){
            return;
        }
        assertHasOneResult("[\"\\'foo\"]", "$[?(@ == '\\'foo')]", conf);
    }

    @Test
    public void escaped_literals2() {
        if(conf.jsonProvider().getClass().getSimpleName().startsWith("Jackson")){
            return;
        }
        assertHasOneResult("[\"\\\\'foo\"]", "$[?(@ == \"\\\\'foo\")]", conf);
    }


    @Test
    public void escape_pattern() {
        assertHasOneResult("[\"x\"]", "$[?(@ =~ /\\/|x/)]", conf);
    }

    @Test
    public void escape_pattern_after_literal() {
        assertHasOneResult("[\"x\"]", "$[?(@ == \"abc\" || @ =~ /\\/|x/)]", conf);
    }

    @Test
    public void escape_pattern_before_literal() {
        assertHasOneResult("[\"x\"]", "$[?(@ =~ /\\/|x/ || @ == \"abc\")]", conf);
    }

    @Test
    public void filter_evaluation_does_not_break_path_evaluation() {
        assertHasOneResult("[{\"s\": \"fo\", \"expected_size\": \"m\"}, {\"s\": \"lo\", \"expected_size\": 2}]", "$[?(@.s size @.expected_size)]", conf);
    }
}
