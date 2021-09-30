package com.jayway.jsonpath;

import org.junit.Test;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.LinkedHashMap;
import java.util.Map;

import static com.jayway.jsonpath.Criteria.where;
import static com.jayway.jsonpath.Filter.filter;
import static org.assertj.core.api.Assertions.assertThat;

//test for issue: https://github.com/json-path/JsonPath/issues/613
public class issue_613 extends BaseTest{
    final OffsetDateTime ofdt_small = OffsetDateTime.of(1999,2,1,1,1,1,1, ZoneOffset.UTC);
    final OffsetDateTime ofdt_middle = OffsetDateTime.of(2000,2,1,1,1,1,1, ZoneOffset.UTC);
    final OffsetDateTime ofdt_big = OffsetDateTime.of(2001,3,1,1,1,1,1, ZoneOffset.MAX);

    Map<String,OffsetDateTime> map_middle = new LinkedHashMap<String, OffsetDateTime>(){
        {
            put("time",ofdt_middle);
        }};

    @Test
    public void issue_613_eq_ne_test() {
        assertThat(filter(where("time").eq(ofdt_middle)).apply(createPredicateContext(map_middle))).isEqualTo(true);
        assertThat(filter(where("time").ne(ofdt_big)).apply(createPredicateContext(map_middle))).isEqualTo(true);
    }
    @Test
    public void issue_613_lt_lte_test() {
        assertThat(filter(where("time").lt(ofdt_big)).apply(createPredicateContext(map_middle))).isEqualTo(true);
        assertThat(filter(where("time").lte(ofdt_small)).apply(createPredicateContext(map_middle))).isEqualTo(false);
    }
    @Test
    public void issue_613_gt_gte_test() {
        assertThat(filter(where("time").gt(ofdt_big)).apply(createPredicateContext(map_middle))).isEqualTo(false);
        assertThat(filter(where("time").gte(ofdt_small)).apply(createPredicateContext(map_middle))).isEqualTo(true);
    }
}
