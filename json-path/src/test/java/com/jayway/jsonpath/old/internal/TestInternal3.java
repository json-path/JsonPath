package com.jayway.jsonpath.old.internal;

import org.junit.Test;

import java.util.List;
import java.util.Map;

import static com.jayway.jsonpath.internal.path.PathCompiler.compile;
import static org.assertj.core.api.Assertions.assertThat;

/**
 *
 */
public class TestInternal3 extends TestBase {

    /*
            RootPathToken rootToken = new RootPathToken();
        //rootToken.append(new PropertyPathToken("stores"));
        //rootToken.append(new ArrayPathToken(asList(0, 1), ArrayPathToken.Operation.INDEX_SEQUENCE));
        //rootToken.append(new ArrayPathToken(asList(0, 2), ArrayPathToken.Operation.SLICE_BETWEEN));
        //rootToken.append(new FilterPathToken(Filter.filter(Criteria.where("name").is("store_1"))));
        //rootToken.append(new WildcardPathToken());
        rootToken.append(new ScanPathToken());
        rootToken.append(new ArrayPathToken(asList(0), ArrayPathToken.Operation.INDEX_SEQUENCE));
        rootToken.append(new PropertyPathToken("name"));
     */



    @Test
    public void a_root_object_can_be_evaluated() {
        Map<String, Object> result = compile("$").evaluate(DOC, DOC, CONF).getValue();

        assertThat(result)
                .containsKey("store")
                .hasSize(1);
    }

    @Test
    public void a_definite_array_item_property_can_be_evaluated() {

        String result = compile("$.store.book[0].author").evaluate(DOC, DOC, CONF).getValue();

        assertThat(result).isEqualTo("Nigel Rees");
    }

    @Test
    public void a_wildcard_array_item_property_can_be_evaluated() {

        List result = compile("$.store.book[*].author").evaluate(DOC, DOC, CONF).getValue();

        assertThat(result).containsOnly(
                "Nigel Rees", "Evelyn Waugh", "Herman Melville", "J. R. R. Tolkien");
    }

}
