package com.jayway.jsonpath.internal.function;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;
import com.jayway.jsonpath.Predicate;
import org.junit.Test;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import static org.junit.Assert.assertNull;

public class Issue628 {

    @Test
    public void testEmptyConfiguration() {
        String document = "{}";
        Configuration config = Configuration.builder().options(Option.SUPPRESS_EXCEPTIONS).build();
        String nonExistentPath = "$.doesNotExist";

        assertNull(JsonPath.read(config.jsonProvider().parse(document), nonExistentPath));
    }

    @Test
    public void testReadingEmptyMapWithFilter(){
        Map<String, Object> doc = new HashMap<String, Object>();

        Predicate customFilter = new Predicate() {
            @Override
            public boolean apply(PredicateContext ctx) {
                return 1 == (Integer)ctx.item();
            }
        };

        List<Integer> res = JsonPath.read(doc, "$.items[?]", customFilter);

        assertNull(res);
    }
}