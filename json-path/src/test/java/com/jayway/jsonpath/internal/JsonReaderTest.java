package com.jayway.jsonpath.internal;

import static org.junit.Assert.*;

import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.Test;

import com.jayway.jsonpath.BaseTest;
import com.jayway.jsonpath.Criteria;
import com.jayway.jsonpath.Filter;
import com.jayway.jsonpath.JsonPath;

public class JsonReaderTest extends BaseTest {

	@Test
    public void cached_path_with_predicates() {

        Filter feq = Filter.filter(Criteria.where("category").eq("reference"));
        Filter fne = Filter.filter(Criteria.where("category").ne("reference"));

        List<String> eq = JsonPath.parse(JSON_DOCUMENT).read("$.store.book[?].category", feq);
        List<String> ne = JsonPath.parse(JSON_DOCUMENT).read("$.store.book[?].category", fne);

        Assertions.assertThat(eq).contains("reference");
        Assertions.assertThat(ne).doesNotContain("reference");
    }

}
