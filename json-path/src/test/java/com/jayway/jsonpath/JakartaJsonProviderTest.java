package com.jayway.jsonpath;

import org.junit.Test;

import jakarta.json.JsonObject;
import jakarta.json.JsonString;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.jayway.jsonpath.JsonPath.parse;
import static com.jayway.jsonpath.JsonPath.using;
import static java.util.Collections.emptyMap;
import static org.assertj.core.api.Assertions.assertThat;

public class JakartaJsonProviderTest extends BaseTest {

    private static final Map<String, Object> EMPTY_MAP = emptyMap();

	@Test
	public void an_object_can_be_read() {
		JsonObject book = using(JAKARTA_JSON_CONFIGURATION)
				.parse(JSON_DOCUMENT)
				.read("$.store.book[0]");

		assertThat(((JsonString) book.get("author")).getChars()).isEqualTo("Nigel Rees");
	}

	@Test
	public void a_property_can_be_read() {
		JsonString category = using(JAKARTA_JSON_CONFIGURATION)
				.parse(JSON_DOCUMENT)
				.read("$.store.book[0].category");

		assertThat(category.getString()).isEqualTo("reference");
	}

	@Test
	public void a_filter_can_be_applied() {
		List<Object> fictionBooks = using(JAKARTA_JSON_CONFIGURATION)
				.parse(JSON_DOCUMENT)
				.read("$.store.book[?(@.category == 'fiction')]");

		assertThat(fictionBooks.size()).isEqualTo(3);
	}

	@Test
	public void result_can_be_mapped_to_object() {
		@SuppressWarnings("unchecked")
		List<Map<String, Object>> books = using(JAKARTA_JSON_CONFIGURATION)
		.parse(JSON_DOCUMENT)
		.read("$.store.book", List.class);

		assertThat(books.size()).isEqualTo(4);
	}

	@Test
	public void read_books_with_isbn() {
		List<Object> books = using(JAKARTA_JSON_CONFIGURATION).parse(JSON_DOCUMENT).read("$..book[?(@.isbn)]");

		assertThat(books.size()).isEqualTo(2);
	}

	/**
	 * Functions take parameters, the length parameter for example takes an entire document which we anticipate
	 * will compute to a document that is an array of elements which can determine its length.
	 *
	 * Since we translate this query from $..books.length() to length($..books) verify that this particular translation
	 * works as anticipated.
	 */
	@Test
	public void read_book_length_using_translated_query() {
		Integer result = using(JAKARTA_JSON_CONFIGURATION)
				.parse(JSON_BOOK_STORE_DOCUMENT)
				.read("$..book.length()");
		assertThat(result).isEqualTo(4);
	}

	@Test
	public void read_book_length() {
		Object result = using(JAKARTA_JSON_CONFIGURATION)
				.parse(JSON_BOOK_STORE_DOCUMENT)
				.read("$.length($..book)");
		assertThat(result).isEqualTo(4);
	}

	@Test
	public void issue_97() {
		String json = "{ \"books\": [ " +
				"{ \"category\": \"fiction\" }, " +
				"{ \"category\": \"reference\" }, " +
				"{ \"category\": \"fiction\" }, " +
				"{ \"category\": \"fiction\" }, " +
				"{ \"category\": \"reference\" }, " +
				"{ \"category\": \"fiction\" }, " +
				"{ \"category\": \"reference\" }, " +
				"{ \"category\": \"reference\" }, " +
				"{ \"category\": \"reference\" }, " +
				"{ \"category\": \"reference\" }, " +
				"{ \"category\": \"reference\" } ]  }";

		DocumentContext dc = using(JAKARTA_JSON_RW_CONFIGURATION)
				.parse(json)
				.delete("$.books[?(@.category == 'reference')]");
		//System.out.println((Object) dc.read("$"));
		@SuppressWarnings("unchecked")
		List<String> categories = dc.read("$..category", List.class);

		assertThat(categories).containsOnly("fiction");
	}

	@Test
	public void test_delete_2() {
		DocumentContext dc = using(JAKARTA_JSON_RW_CONFIGURATION)
				.parse("[" +
						"{\"top\": {\"middle\": null}}," +
						"{\"top\": {\"middle\": {}  }}," +
						"{\"top\": {\"middle\": {\"bottom\": 2}  }}" +
						"]")
				.delete(JsonPath.compile("$[*].top.middle.bottom"));
		Object ans = dc.read("$");
		//System.out.println(ans);
		assert(ans.toString().equals("[{\"top\":{\"middle\":null}},{\"top\":{\"middle\":{}}},{\"top\":{\"middle\":{}}}]"));
	}

    @Test
    public void an_root_property_can_be_updated() {
        Object o = using(JAKARTA_JSON_RW_CONFIGURATION)
        		.parse(JSON_DOCUMENT)
        		.set("$.int-max-property", 1)
        		.json();

        Integer result = using(JAKARTA_JSON_RW_CONFIGURATION).parse(o)
        		.read("$.int-max-property", Integer.class);

        assertThat(result).isEqualTo(1);
    }

    @Test
    public void an_deep_scan_can_update() {
    	Object o = using(JAKARTA_JSON_RW_CONFIGURATION)
        		.parse(JSON_DOCUMENT)
        		.set("$..display-price", 1)
        		.json();

        List<Integer> result = using(JAKARTA_JSON_RW_CONFIGURATION).parse(o)
        		.read("$..display-price", new TypeRef<List<Integer>>() {});

        assertThat(result).containsExactly(1, 1, 1, 1, 1);
    }

    @Test
    public void an_filter_can_update() {
    	final String updatePathFunction = "$.store.book[?(@.display-price)].display-price";
    	Object o = using(JAKARTA_JSON_RW_CONFIGURATION)
        		.parse(JSON_DOCUMENT)
        		.set(updatePathFunction, 1)
        		.json();

        List<Integer> result = using(JAKARTA_JSON_RW_CONFIGURATION).parse(o)
        		.read(updatePathFunction, new TypeRef<List<Integer>>() {});

        assertThat(result).containsExactly(1, 1, 1, 1);
    }

    @Test
    public void a_path_can_be_deleted() {
    	final String deletePath = "$.store.book[*].display-price";
    	Object o = using(JAKARTA_JSON_RW_CONFIGURATION)
        		.parse(JSON_DOCUMENT)
        		.delete(deletePath)
        		.json();

        List<Integer> result = using(JAKARTA_JSON_RW_CONFIGURATION).parse(o)
        		.read(deletePath, new TypeRef<List<Integer>>() {});

        assertThat(result).isEmpty();
    }

    @Test
    public void operations_can_chained() {
    	Object o = using(JAKARTA_JSON_RW_CONFIGURATION)
        		.parse(JSON_DOCUMENT)
                .delete("$.store.book[*].display-price")
                .set("$.store.book[*].category", "A")
                .json();

        List<Integer> prices = using(JAKARTA_JSON_RW_CONFIGURATION).parse(o)
        		.read("$.store.book[*].display-price", new TypeRef<List<Integer>>() {});
        List<String> categories = using(JAKARTA_JSON_RW_CONFIGURATION).parse(o)
        		.read("$.store.book[*].category", new TypeRef<List<String>>() {});

        assertThat(prices).isEmpty();
        assertThat(categories).containsExactly("A", "A", "A", "A");
    }

    @Test
    public void an_array_index_can_be_updated() {
        String res = using(JAKARTA_JSON_RW_CONFIGURATION)
        		.parse(JSON_DOCUMENT)
        		.set("$.store.book[0]", "a")
        		.read("$.store.book[0]", String.class);

        assertThat(res).isEqualTo("a");
    }

    @Test
    public void an_array_slice_can_be_updated() {
        List<String> res = using(JAKARTA_JSON_RW_CONFIGURATION)
        		.parse(JSON_DOCUMENT)
        		.set("$.store.book[0:2]", "a")
        		.read("$.store.book[0:2]", new TypeRef<List<String>>() {});

        assertThat(res).containsExactly("a", "a");
    }

    @Test
    public void an_array_criteria_can_be_updated() {
        List<String> res = using(JAKARTA_JSON_RW_CONFIGURATION)
        		.parse(JSON_DOCUMENT)
                .set("$.store.book[?(@.category == 'fiction')]", "a")
                .read("$.store.book[?(@ == 'a')]", new TypeRef<List<String>>() {});

        assertThat(res).containsExactly("a", "a", "a");
    }

    @Test
    public void an_array_criteria_can_be_deleted() {
        List<String> res = using(JAKARTA_JSON_RW_CONFIGURATION)
        		.parse(JSON_DOCUMENT)
                .delete("$.store.book[?(@.category == 'fiction')]")
                .read("$.store.book[*].category", new TypeRef<List<String>>() {});

        assertThat(res).containsExactly("reference");
    }

    @Test
    public void an_array_criteria_with_multiple_results_can_be_deleted(){
        InputStream stream = this.getClass().getResourceAsStream("/json_array_multiple_delete.json");
        String deletePath = "$._embedded.mandates[?(@.count=~/0/)]";
        DocumentContext dc = using(JAKARTA_JSON_RW_CONFIGURATION).parse(stream).delete(deletePath);
        List<Object> result = dc.read(deletePath);
        assertThat(result.size()).isEqualTo(0);
    }

    @Test
    public void multi_prop_delete() {
        List<Map<String, Object>> res = using(JAKARTA_JSON_RW_CONFIGURATION)
        		.parse(JSON_DOCUMENT)
        		.delete("$.store.book[*]['author', 'category']")
        		.read("$.store.book[*]['author', 'category']", new TypeRef<List<Map<String, Object>>>() {});

        assertThat(res).containsExactly(EMPTY_MAP, EMPTY_MAP, EMPTY_MAP, EMPTY_MAP);
    }

    @Test
    public void multi_prop_update() {
        @SuppressWarnings("serial")
		Map<String, Object> expected = new HashMap<String, Object>(){{
            put("author", "a");
            put("category", "a");
        }};
        List<Map<String, Object>> res = using(JAKARTA_JSON_RW_CONFIGURATION)
        		.parse(JSON_DOCUMENT)
        		.set("$.store.book[*]['author', 'category']", "a")
        		.read("$.store.book[*]['author', 'category']", new TypeRef<List<Map<String, Object>>>() {});
        assertThat(res).containsExactly(expected, expected, expected, expected);
    }

	@Test
	public void add_to_array() {
		Object res = using(JAKARTA_JSON_RW_CONFIGURATION)
				.parse(JSON_DOCUMENT)
				.add("$.store.book", 1)
				.read("$.store.book[4]");
		res = JAKARTA_JSON_RW_CONFIGURATION.jsonProvider().unwrap(res);
		assertThat(res).isEqualTo(1);
	}

	@Test
	public void add_to_object() {
		Object res = using(JAKARTA_JSON_RW_CONFIGURATION)
				.parse(JSON_DOCUMENT)
				.put("$.store.book[0]", "new-key", "new-value")
				.read("$.store.book[0].new-key");
		res = JAKARTA_JSON_RW_CONFIGURATION.jsonProvider().unwrap(res);
		assertThat(res).isEqualTo("new-value");
	}

	@Test(expected = InvalidModificationException.class)
	public void add_to_object_on_array() {
		using(JAKARTA_JSON_RW_CONFIGURATION).parse(JSON_DOCUMENT).put("$.store.book", "new-key", "new-value");
	}

	@Test(expected = InvalidModificationException.class)
	public void add_to_array_on_object() {
		using(JAKARTA_JSON_RW_CONFIGURATION).parse(JSON_DOCUMENT).add("$.store.book[0]", "new-value");
	}

	@Test
	public void a_path_can_be_renamed(){
		Object o = using(JAKARTA_JSON_RW_CONFIGURATION)
				.parse(JSON_DOCUMENT)
				.renameKey("$.store", "book", "updated-book")
				.json();
		List<Object> result = parse(o).read("$.store.updated-book");

		assertThat(result).isNotEmpty();
	}

	@Test
	public void map_array_items_can_be_renamed(){
		Object o = using(JAKARTA_JSON_RW_CONFIGURATION)
				.parse(JSON_DOCUMENT)
				.renameKey("$.store.book[*]", "category", "renamed-category")
				.json();
		List<Object> result = parse(o).read("$.store.book[*].renamed-category");
		assertThat(result).isNotEmpty();
	}

	@Test(expected = PathNotFoundException.class)
	public void non_existent_key_rename_not_allowed() {
		using(JAKARTA_JSON_RW_CONFIGURATION)
				.parse(JSON_DOCUMENT)
				.renameKey("$", "fake", "new-fake")
				.json();
	}

	@Test
	public void single_match_value_can_be_mapped() {
		MapFunction mapFunction = new ToStringMapFunction();
		String stringResult = using(JAKARTA_JSON_RW_CONFIGURATION)
				.parse(JSON_DOCUMENT)
				.map("$.string-property", mapFunction)
				.read("$.string-property", String.class);
		assertThat(stringResult.endsWith("converted")).isTrue();
	}

	@Test
	public void object_can_be_mapped() {
		TypeRef<List<String>> typeRef = new TypeRef<List<String>>() {};
		MapFunction mapFunction = new ToStringMapFunction();
		DocumentContext dc = using(JAKARTA_JSON_RW_CONFIGURATION).parse(JSON_DOCUMENT);
		Object list = dc.read("$..book");
		assertThat(list).isInstanceOf(List.class);
		Object res = dc.map("$..book", mapFunction).read("$..book", typeRef).get(0);
		assertThat(res).isInstanceOf(String.class);
		assertThat((String) res).endsWith("converted");
	}

	@Test
	public void multi_match_path_can_be_mapped() {
		MapFunction mapFunction = new ToStringMapFunction();
		List<Double> doubleResult = using(JAKARTA_JSON_RW_CONFIGURATION)
				.parse(JSON_DOCUMENT)
				.read("$..display-price", new TypeRef<List<Double>>() {});
		for (Double dRes : doubleResult){
			assertThat(dRes).isInstanceOf(Double.class);
		}
		List<String> stringResult = using(JAKARTA_JSON_RW_CONFIGURATION)
				.parse(JSON_DOCUMENT)
				.map("$..display-price", mapFunction)
				.read("$..display-price", new TypeRef<List<String>>() {});
		for (String sRes : stringResult){
			assertThat(sRes).isInstanceOf(String.class);
			assertThat(sRes.endsWith("converted")).isTrue();
		}
	}

	// Helper converter implementation for test cases.
	private class ToStringMapFunction implements MapFunction {

		@Override
		public Object map(Object currentValue, Configuration configuration) {
			return currentValue.toString()+"converted";
		}
	}

}
