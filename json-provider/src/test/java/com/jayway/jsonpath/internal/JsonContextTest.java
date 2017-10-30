package com.jayway.jsonpath.internal;

import com.jayway.jsonpath.BaseTestConfiguration;
import com.jayway.jsonpath.BaseTestJson;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.Filter;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.spi.builder.JsonSmartNodeBuilder;

import org.assertj.core.api.Assertions;
import org.junit.Test;

import java.util.List;

public class JsonContextTest extends BaseTestConfiguration {

	JsonSmartNodeBuilder builder = new JsonSmartNodeBuilder();
	
	@Test
    public void cached_path_with_predicates() {

        Filter feq = Filter.filter(builder.where("category").eq("reference"));
        Filter fne = Filter.filter(builder.where("category").ne("reference"));
        
        DocumentContext JsonDoc = JsonPath.parse(BaseTestJson.JSON_DOCUMENT);

        List<String> eq = JsonDoc.read("$.store.book[?].category", feq);
        List<String> ne = JsonDoc.read("$.store.book[?].category", fne);

        Assertions.assertThat(eq).contains("reference");
        Assertions.assertThat(ne).doesNotContain("reference");
    }

}
