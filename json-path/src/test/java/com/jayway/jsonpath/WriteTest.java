package com.jayway.jsonpath;

import org.junit.Test;

import java.io.InputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static com.jayway.jsonpath.JsonPath.parse;
import static java.util.Collections.emptyMap;
import static org.assertj.core.api.Assertions.assertThat;

public class WriteTest extends BaseTest {

    private static final Map<String, Object> EMPTY_MAP = emptyMap();

    @Test
    public void an_array_child_property_can_be_updated() {

        Object o = parse(JSON_DOCUMENT).set("$.store.book[*].display-price", 1).json();

        List<Integer> result = parse(o).read("$.store.book[*].display-price");

        assertThat(result).containsExactly(1, 1, 1, 1);
    }


    @Test
    public void an_root_property_can_be_updated() {

        Object o = parse(JSON_DOCUMENT).set("$.int-max-property", 1).json();

        Integer result = parse(o).read("$.int-max-property");

        assertThat(result).isEqualTo(1);
    }

    @Test
    public void an_deep_scan_can_update() {

        Object o = parse(JSON_DOCUMENT).set("$..display-price", 1).json();

        List<Integer> result = parse(o).read("$..display-price");

        assertThat(result).containsExactly(1, 1, 1, 1, 1);
    }


    @Test
    public void an_filter_can_update() {

        Object o = parse(JSON_DOCUMENT).set("$.store.book[?(@.display-price)].display-price", 1).json();

        List<Integer> result = parse(o).read("$.store.book[?(@.display-price)].display-price");

        assertThat(result).containsExactly(1, 1, 1, 1);
    }

    @Test
    public void a_path_can_be_deleted() {

        Object o = parse(JSON_DOCUMENT).delete("$.store.book[*].display-price").json();

        List<Integer> result = parse(o).read("$.store.book[*].display-price");

        assertThat(result).isEmpty();
    }

    @Test
    public void operations_can_chained() {

        Object o = parse(JSON_DOCUMENT)
                .delete("$.store.book[*].display-price")
                .set("$.store.book[*].category", "A")
                .json();

        List<Integer> prices = parse(o).read("$.store.book[*].display-price");
        List<String> categories = parse(o).read("$.store.book[*].category");

        assertThat(prices).isEmpty();
        assertThat(categories).containsExactly("A", "A", "A", "A");
    }

    @Test
    public void an_array_can_be_updated() {

        List<Integer> ints = parse("[0,1,2,3]").set("$[?(@ == 1)]", 9).json();

        assertThat(ints).containsExactly(0, 9, 2, 3);
    }

    @Test
    public void an_array_index_can_be_updated() {

        String res = parse(JSON_DOCUMENT).set("$.store.book[0]", "a").read("$.store.book[0]");

        assertThat(res).isEqualTo("a");
    }

    @Test
    public void an_array_slice_can_be_updated() {

        List<String> res = parse(JSON_DOCUMENT).set("$.store.book[0:2]", "a").read("$.store.book[0:2]");

        assertThat(res).containsExactly("a", "a");
    }

    @Test
    public void an_array_criteria_can_be_updated() {

        List<String> res = parse(JSON_DOCUMENT)
                .set("$.store.book[?(@.category == 'fiction')]", "a")
                .read("$.store.book[?(@ == 'a')]");

        assertThat(res).containsExactly("a", "a", "a");
    }

    @Test
    public void an_array_criteria_can_be_deleted() {

        List<String> res = parse(JSON_DOCUMENT)
                .delete("$.store.book[?(@.category == 'fiction')]")
                .read("$.store.book[*].category");

        assertThat(res).containsExactly("reference");
    }

    @Test
    public void an_array_criteria_with_multiple_results_can_be_deleted(){
        InputStream stream = this.getClass().getResourceAsStream("/json_array_multiple_delete.json");
        String deletePath = "$._embedded.mandates[?(@.count=~/0/)]";
        DocumentContext documentContext = JsonPath.parse(stream);
        documentContext.delete(deletePath);
        List<Object> result = documentContext.read(deletePath);
        assertThat(result.size()).isEqualTo(0);
    }


    @Test
    public void multi_prop_delete() {

        List<Map<String, Object>> res = parse(JSON_DOCUMENT).delete("$.store.book[*]['author', 'category']").read("$.store.book[*]['author', 'category']");

        assertThat(res).containsExactly(EMPTY_MAP, EMPTY_MAP, EMPTY_MAP, EMPTY_MAP);
    }

    @Test
    public void multi_prop_update() {

        Map<String, Object> expected = new HashMap<String, Object>(){{
            put("author", "a");
            put("category", "a");
        }};

        List<Map<String, Object>> res = parse(JSON_DOCUMENT).set("$.store.book[*]['author', 'category']", "a").read("$.store.book[*]['author', 'category']");

        assertThat(res).containsExactly(expected, expected, expected, expected);
    }


    @Test
    public void multi_prop_update_not_all_defined() {

        Map<String, Object> expected = new HashMap<String, Object>(){{
            put("author", "a");
            put("isbn", "a");
        }};

        List<Map<String, Object>> res = parse(JSON_DOCUMENT).set("$.store.book[*]['author', 'isbn']", "a").read("$.store.book[*]['author', 'isbn']");

        assertThat(res).containsExactly(expected, expected, expected, expected);
    }

    @Test
    public void add_to_array() {
        Object res = parse(JSON_DOCUMENT).add("$.store.book", 1).read("$.store.book[4]");
        assertThat(res).isEqualTo(1);
    }

    @Test
    public void add_to_object() {
        Object res = parse(JSON_DOCUMENT).put("$.store.book[0]", "new-key", "new-value").read("$.store.book[0].new-key");
        assertThat(res).isEqualTo("new-value");
    }

    @Test
    public void item_can_be_added_to_root_array() {
        List<Integer> model = new LinkedList<Integer>();
        model.add(1);
        model.add(2);

        List<Integer> ints = parse(model).add("$", 3).read("$");

        assertThat(ints).containsExactly(1,2,3);
    }

    @Test
    public void key_val_can_be_added_to_root_object() {
        Map model = new HashMap();
        model.put("a", "a-val");

        String newVal = parse(model).put("$", "new-key", "new-val").read("$.new-key");

        assertThat(newVal).isEqualTo("new-val");
    }

    @Test(expected = InvalidModificationException.class)
    public void add_to_object_on_array() {
        parse(JSON_DOCUMENT).put("$.store.book", "new-key", "new-value");
    }

    @Test(expected = InvalidModificationException.class)
    public void add_to_array_on_object() {
        parse(JSON_DOCUMENT).add("$.store.book[0]", "new-value");
    }


    @Test(expected = InvalidModificationException.class)
    public void root_object_can_not_be_updated() {
        Map model = new HashMap();
        model.put("a", "a-val");

        parse(model).set("$[?(@.a == 'a-val')]", 1);
    }

    @Test
    public void a_path_can_be_renamed(){
        Object o = parse(JSON_DOCUMENT).renameKey("$.store", "book", "updated-book").json();
        List<Object> result = parse(o).read("$.store.updated-book");

        assertThat(result).isNotEmpty();
    }

    @Test
    public void keys_in_root_containing_map_can_be_renamed(){
        Object o = parse(JSON_DOCUMENT).renameKey("$", "store", "new-store").json();
        List<Object> result = parse(o).read("$.new-store[*]");
        assertThat(result).isNotEmpty();
    }

    @Test
    public void map_array_items_can_be_renamed(){
        Object o = parse(JSON_DOCUMENT).renameKey("$.store.book[*]", "category", "renamed-category").json();
        List<Object> result = parse(o).read("$.store.book[*].renamed-category");
        assertThat(result).isNotEmpty();
    }

    @Test(expected = InvalidModificationException.class)
    public void non_map_array_items_cannot_be_renamed(){
        List<Integer> model = new LinkedList<Integer>();
        model.add(1);
        model.add(2);
        parse(model).renameKey("$[*]", "oldKey", "newKey");
    }

    @Test(expected = InvalidModificationException.class)
    public void multiple_properties_cannot_be_renamed(){
        parse(JSON_DOCUMENT).renameKey("$.store.book[*]['author', 'category']", "old-key", "new-key");
    }

    @Test(expected = PathNotFoundException.class)
    public void non_existent_key_rename_not_allowed(){
        Object o = parse(JSON_DOCUMENT).renameKey("$", "fake", "new-fake").json();
    }

    @Test(expected = InvalidModificationException.class)
    public void rootCannotBeMapped(){
        MapFunction mapFunction = new MapFunction() {
            @Override
            public Object map(Object currentValue, Configuration configuration) {
                return currentValue.toString()+"converted";
            }
        };
        Object o = parse(JSON_DOCUMENT).map("$", mapFunction).json();
    }

    @Test
    public void single_match_value_can_be_mapped(){
        MapFunction mapFunction = new ToStringMapFunction();
        String stringResult = parse(JSON_DOCUMENT).map("$.string-property", mapFunction).read("$.string-property");
        assertThat(stringResult.endsWith("converted")).isTrue();
    }

    @Test
    public void object_can_be_mapped(){
        TypeRef<List<String>> typeRef = new TypeRef<List<String>>() {};
        MapFunction mapFunction = new ToStringMapFunction();
        DocumentContext documentContext = JsonPath.using(JACKSON_CONFIGURATION).parse(JSON_DOCUMENT);
        Object list = documentContext.read("$..book");
        assertThat(list).isInstanceOf(List.class);
        String result = documentContext.map("$..book", mapFunction).read("$..book", typeRef).get(0);
        assertThat(result).isInstanceOf(String.class);
        assertThat(result).endsWith("converted");
    }

    @Test
    public void multi_match_path_can_be_mapped(){
        MapFunction mapFunction = new ToStringMapFunction();
        List<Double> doubleResult = parse(JSON_DOCUMENT).read("$..display-price");
        for(Double dRes : doubleResult){
            assertThat(dRes).isInstanceOf(Double.class);
        }
        List<String> stringResult = parse(JSON_DOCUMENT).map("$..display-price", mapFunction).read("$..display-price");
        for(String sRes : stringResult){
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