package com.jayway.jsonpath;

import com.jayway.jsonpath.spi.json.JsonProvider;
import com.jayway.jsonpath.spi.json.JsonProviderFactory;
import org.junit.Test;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import static com.jayway.jsonpath.Criteria.where;
import static com.jayway.jsonpath.Filter.filter;
import static java.util.Arrays.asList;
import static junit.framework.Assert.*;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class FilterTest {

    public final static String DOCUMENT =
            "{ \"store\": {\n" +
                    "    \"book\": [ \n" +
                    "      { \"category\": \"reference\",\n" +
                    "        \"author\": \"Nigel Rees\",\n" +
                    "        \"title\": \"Sayings of the Century\",\n" +
                    "        \"price\": 8.95\n" +
                    "      },\n" +
                    "      { \"category\": \"fiction\",\n" +
                    "        \"author\": \"Evelyn Waugh\",\n" +
                    "        \"title\": \"Sword of Honour\",\n" +
                    "        \"price\": 12.99\n" +
                    "      },\n" +
                    "      { \"category\": \"fiction\",\n" +
                    "        \"author\": \"Herman Melville\",\n" +
                    "        \"title\": \"Moby Dick\",\n" +
                    "        \"isbn\": \"0-553-21311-3\",\n" +
                    "        \"price\": 8.99\n" +
                    "      },\n" +
                    "      { \"category\": \"fiction\",\n" +
                    "        \"author\": \"J. R. R. Tolkien\",\n" +
                    "        \"title\": \"The Lord of the Rings\",\n" +
                    "        \"isbn\": \"0-395-19395-8\",\n" +
                    "        \"price\": 22.99\n" +
                    "      }\n" +
                    "    ],\n" +
                    "    \"bicycle\": {\n" +
                    "      \"color\": \"red\",\n" +
                    "      \"price\": 19.95,\n" +
                    "      \"foo:bar\": \"fooBar\",\n" +
                    "      \"dot.notation\": \"new\"\n" +
                    "    }\n" +
                    "  }\n" +
                    "}";

    private static final JsonProvider jp = JsonProviderFactory.createProvider();

    private static final Configuration conf = Configuration.defaultConfiguration();
    //-------------------------------------------------
    //
    // Single filter tests
    //
    //-------------------------------------------------
    @Test
    public void is_filters_evaluates() throws Exception {
        Map<String, Object> check = new HashMap<String, Object>();
        check.put("foo", "foo");
        check.put("bar", null);

        assertTrue(filter(where("bar").is(null)).apply(check, conf));
        assertTrue(filter(where("foo").is("foo")).apply(check, conf));
        assertFalse(filter(where("foo").is("xxx")).apply(check, conf));
        assertFalse(filter(where("bar").is("xxx")).apply(check, conf));
    }

    @Test
    public void ne_filters_evaluates() throws Exception {
        Map<String, Object> check = new HashMap<String, Object>();
        check.put("foo", "foo");
        check.put("bar", null);

        assertTrue(filter(where("foo").ne(null)).apply(check, conf));
        assertTrue(filter(where("foo").ne("not foo")).apply(check, conf));
        assertFalse(filter(where("foo").ne("foo")).apply(check, conf));
        assertFalse(filter(where("bar").ne(null)).apply(check, conf));
    }

    @Test
    public void gt_filters_evaluates() throws Exception {
        Map<String, Object> check = new HashMap<String, Object>();
        check.put("foo", 12.5D);
        check.put("foo_null", null);

        assertTrue(filter(where("foo").gt(12D)).apply(check, conf));
        assertFalse(filter(where("foo").gt(null)).apply(check, conf));
        assertFalse(filter(where("foo").gt(20D)).apply(check, conf));
        assertFalse(filter(where("foo_null").gt(20D)).apply(check, conf));
    }

    @Test
    public void gte_filters_evaluates() throws Exception {
        Map<String, Object> check = new HashMap<String, Object>();
        check.put("foo", 12.5D);
        check.put("foo_null", null);

        assertTrue(filter(where("foo").gte(12D)).apply(check, conf));
        assertTrue(filter(where("foo").gte(12.5D)).apply(check, conf));
        assertFalse(filter(where("foo").gte(null)).apply(check, conf));
        assertFalse(filter(where("foo").gte(20D)).apply(check, conf));
        assertFalse(filter(where("foo_null").gte(20D)).apply(check, conf));
    }

    @Test
    public void lt_filters_evaluates() throws Exception {
        Map<String, Object> check = new HashMap<String, Object>();
        check.put("foo", 10.5D);
        check.put("foo_null", null);

        //assertTrue(filter(where("foo").lt(12D)).apply(check, conf));
        assertFalse(filter(where("foo").lt(null)).apply(check, conf));
        //assertFalse(filter(where("foo").lt(5D)).apply(check, conf));
        //assertFalse(filter(where("foo_null").lt(5D)).apply(check, conf));
    }

    @Test
    public void lte_filters_evaluates() throws Exception {
        Map<String, Object> check = new HashMap<String, Object>();
        check.put("foo", 12.5D);
        check.put("foo_null", null);

        assertTrue(filter(where("foo").lte(13D)).apply(check, conf));
        assertFalse(filter(where("foo").lte(null)).apply(check, conf));
        assertFalse(filter(where("foo").lte(5D)).apply(check, conf));
        assertFalse(filter(where("foo_null").lte(5D)).apply(check, conf));
    }

    @Test
    public void in_filters_evaluates() throws Exception {
        Map<String, Object> check = new HashMap<String, Object>();
        check.put("item", 3);
        check.put("null_item", null);

        assertTrue(filter(where("item").in(1, 2, 3)).apply(check, conf));
        assertTrue(filter(where("item").in(asList(1, 2, 3))).apply(check, conf));
        assertFalse(filter(where("item").in(4, 5, 6)).apply(check, conf));
        assertFalse(filter(where("item").in(asList(4, 5, 6))).apply(check, conf));
        assertFalse(filter(where("item").in(asList('A'))).apply(check, conf));
        assertFalse(filter(where("item").in(asList((Object) null))).apply(check, conf));

        assertTrue(filter(where("null_item").in((Object) null)).apply(check, conf));
        assertFalse(filter(where("null_item").in(1, 2, 3)).apply(check, conf));
    }

    @Test
    public void nin_filters_evaluates() throws Exception {
        Map<String, Object> check = new HashMap<String, Object>();
        check.put("item", 3);
        check.put("null_item", null);

        assertTrue(filter(where("item").nin(4, 5)).apply(check, conf));
        assertTrue(filter(where("item").nin(asList(4, 5))).apply(check, conf));
        assertTrue(filter(where("item").nin(asList('A'))).apply(check, conf));
        assertTrue(filter(where("null_item").nin(1, 2, 3)).apply(check, conf));
        assertTrue(filter(where("item").nin(asList((Object) null))).apply(check, conf));

        assertFalse(filter(where("item").nin(3)).apply(check, conf));
        assertFalse(filter(where("item").nin(asList(3))).apply(check, conf));
    }

    @Test
    public void all_filters_evaluates() throws Exception {
        Map<String, Object> check = new HashMap<String, Object>();
        check.put("items", asList(1, 2, 3));

        assertTrue(filter(where("items").all(1, 2, 3)).apply(check, conf));
        assertFalse(filter(where("items").all(1, 2, 3, 4)).apply(check, conf));
    }

    @Test
    public void size_filters_evaluates() throws Exception {
        Map<String, Object> check = new HashMap<String, Object>();
        check.put("items", asList(1, 2, 3));
        check.put("items_empty", Collections.emptyList());

        assertTrue(filter(where("items").size(3)).apply(check, conf));
        assertTrue(filter(where("items_empty").size(0)).apply(check, conf));
        assertFalse(filter(where("items").size(2)).apply(check, conf));
    }

    @Test
    //@Ignore //TODO: finalize behaviour
    public void exists_filters_evaluates() throws Exception {
        Map<String, Object> check = new HashMap<String, Object>();
        check.put("foo", "foo");
        check.put("foo_null", null);

        assertTrue(filter(where("foo").exists(true)).apply(check, conf));
        assertFalse(filter(where("foo").exists(false)).apply(check, conf));

        assertTrue(filter(where("foo_null").exists(true)).apply(check, conf));
        assertFalse(filter(where("foo_null").exists(false)).apply(check, conf));

        assertTrue(filter(where("bar").exists(false)).apply(check, conf));
        assertFalse(filter(where("bar").exists(true)).apply(check, conf));
    }

    @Test
    public void type_filters_evaluates() throws Exception {
        Map<String, Object> check = new HashMap<String, Object>();
        check.put("string", "foo");
        check.put("string_null", null);
        check.put("int", 1);
        check.put("long", 1L);
        check.put("double", 1.12D);

        assertFalse(filter(where("string_null").type(String.class)).apply(check, conf));
        assertTrue(filter(where("string").type(String.class)).apply(check, conf));
        assertFalse(filter(where("string").type(Number.class)).apply(check, conf));

        assertTrue(filter(where("int").type(Integer.class)).apply(check, conf));
        assertFalse(filter(where("int").type(Long.class)).apply(check, conf));

        assertTrue(filter(where("long").type(Long.class)).apply(check, conf));
        assertFalse(filter(where("long").type(Integer.class)).apply(check, conf));

        assertTrue(filter(where("double").type(Double.class)).apply(check, conf));
        assertFalse(filter(where("double").type(Integer.class)).apply(check, conf));
    }

    @Test
    public void pattern_filters_evaluates() throws Exception {
        Map<String, Object> check = new HashMap<String, Object>();
        check.put("name", "kalle");
        check.put("name_null", null);

        assertFalse(filter(where("name_null").regex(Pattern.compile(".alle"))).apply(check, conf));
        assertTrue(filter(where("name").regex(Pattern.compile(".alle"))).apply(check, conf));
        assertFalse(filter(where("name").regex(Pattern.compile("KALLE"))).apply(check, conf));
        assertTrue(filter(where("name").regex(Pattern.compile("KALLE", Pattern.CASE_INSENSITIVE))).apply(check, conf));

    }

    @Test
    public void combine_filter_deep_criteria() {

        String json = "[\n" +
                "   {\n" +
                "      \"first-name\" : \"John\",\n" +
                "      \"last-name\" : \"Irving\",\n" +
                "      \"address\" : {\"state\" : \"Texas\"}\n" +
                "   },\n" +
                "   {\n" +
                "      \"first-name\" : \"Jock\",\n" +
                "      \"last-name\" : \"Ewing\",\n" +
                "      \"address\" : {\"state\" : \"Texas\"}\n" +
                "   },\n" +
                "   {\n" +
                "      \"first-name\" : \"Jock\",\n" +
                "      \"last-name\" : \"Barnes\",\n" +
                "      \"address\" : {\"state\" : \"Nevada\"}\n" +
                "   } \n" +
                "]";


        Filter filter = filter(
                where("first-name").is("Jock")
                .and("address.state").is("Texas"));

        List<Map<String, Object>> jocksInTexas1 = JsonPath.read(json, "$[?]", filter);
        List<Map<String, Object>> jocksInTexas2  = JsonPath.read(json, "$[?(@.first-name == 'Jock' && @.address.state == 'Texas')]");


        JsonPath.parse(json).json();

        assertThat((String)JsonPath.read(jocksInTexas1, "[0].address.state"), is("Texas"));
        assertThat((String)JsonPath.read(jocksInTexas1, "[0].first-name"), is("Jock"));
        assertThat((String)JsonPath.read(jocksInTexas1, "[0].last-name"), is("Ewing"));


        System.out.println("res1" + jocksInTexas1);
        System.out.println("res2" + jocksInTexas2);


        System.out.println("done");
    }

    //-------------------------------------------------
    //
    // Single filter tests
    //
    //-------------------------------------------------

    @Test
    public void filters_can_be_combined() throws Exception {

        Map<String, Object> check = new HashMap<String, Object>();
        check.put("string", "foo");
        check.put("string_null", null);
        check.put("int", 10);
        check.put("long", 1L);
        check.put("double", 1.12D);

        Filter shouldMarch = filter(where("string").is("foo").and("int").lt(11));
        Filter shouldNotMarch = filter(where("string").is("foo").and("int").gt(11));

        assertTrue(shouldMarch.apply(check, conf));
        assertFalse(shouldNotMarch.apply(check, conf));
    }

    @Test
    public void filters_can_be_extended_with_new_criteria() throws Exception {

        Map<String, Object> check = new HashMap<String, Object>();
        check.put("string", "foo");
        check.put("string_null", null);
        check.put("int", 10);
        check.put("long", 1L);
        check.put("double", 1.12D);

        Filter filter = filter(where("string").is("foo").and("int").lt(11));

        assertTrue(filter.apply(check, conf));

        filter.addCriteria(where("long").ne(1L));

        assertFalse(filter.apply(check, conf));

    }

    @Test
    public void filters_criteria_can_be_refined() throws Exception {

        Map<String, Object> check = new HashMap<String, Object>();
        check.put("string", "foo");
        check.put("string_null", null);
        check.put("int", 10);
        check.put("long", 1L);
        check.put("double", 1.12D);

        Filter filter = filter(where("string").is("foo"));

        assertTrue(filter.apply(check, conf));

        Criteria criteria = where("string").is("not eq");

        filter.addCriteria(criteria);

        assertFalse(filter.apply(check, conf));


        filter = filter(where("string").is("foo").and("string").is("not eq"));
        assertFalse(filter.apply(check, conf));


        filter = filter(where("string").is("foo").and("string").is("foo"));
        assertTrue(filter.apply(check, conf));

    }


    @Test
    public void arrays_of_maps_can_be_filtered() throws Exception {

         /*
        Map<String, Object> rootGrandChild_A = new HashMap<String, Object>();
        rootGrandChild_A.put("name", "rootGrandChild_A");

        Map<String, Object> rootGrandChild_B = new HashMap<String, Object>();
        rootGrandChild_B.put("name", "rootGrandChild_B");

        Map<String, Object> rootGrandChild_C = new HashMap<String, Object>();
        rootGrandChild_C.put("name", "rootGrandChild_C");


        Map<String, Object> rootChild_A = new HashMap<String, Object>();
        rootChild_A.put("name", "rootChild_A");
        rootChild_A.put("children", asList(rootGrandChild_A, rootGrandChild_B, rootGrandChild_C));

        Map<String, Object> rootChild_B = new HashMap<String, Object>();
        rootChild_B.put("name", "rootChild_B");
        rootChild_B.put("children", asList(rootGrandChild_A, rootGrandChild_B, rootGrandChild_C));

        Map<String, Object> rootChild_C = new HashMap<String, Object>();
        rootChild_C.put("name", "rootChild_C");
        rootChild_C.put("children", asList(rootGrandChild_A, rootGrandChild_B, rootGrandChild_C));

        Map<String, Object> root = new HashMap<String, Object>();
        root.put("children", asList(rootChild_A, rootChild_B, rootChild_C));


        Filter customFilter = new Filter.FilterAdapter<Map<String, Object>>() {
            @Override
            public boolean apply(check, confMap<String, Object> map) {
                if (map.get("name").equals("rootGrandChild_A")) {
                    return true;
                }
                return false;
            }
        };

        Filter rootChildFilter = filter(where("name").regex(Pattern.compile("rootChild_[A|B]")));
        Filter rootGrandChildFilter = filter(where("name").regex(Pattern.compile("rootGrandChild_[A|B]")));

        //TODO: breaking v2 (solved by [?,?])
        //List read = JsonPath.read(root, "children[?].children[?][?]", rootChildFilter, rootGrandChildFilter, customFilter);
        List read = JsonPath.read(root, "children[?].children[?, ?]", rootChildFilter, rootGrandChildFilter, customFilter);


        System.out.println(read.size());
        */
    }


    @Test
    public void arrays_of_objects_can_be_filtered() throws Exception {
        /*
        Map<String, Object> doc = new HashMap<String, Object>();
        doc.put("items", asList(1, 2, 3));

        Filter customFilter = new Filter.FilterAdapter() {
            @Override
            public boolean apply(check, confObject o, Configuration configuration) {
                return 1 == (Integer) o;
            }
        };

        List<Integer> res = JsonPath.read(doc, "$.items[?]", customFilter);

        assertEquals(1, res.get(0).intValue());
        */
    }

    @Test
    public void filters_can_contain_json_path_expressions() throws Exception {
    System.out.println(DOCUMENT);
        Object doc = JsonProviderFactory.createProvider().parse(DOCUMENT);


        assertFalse(filter(where("$.store.bicycle.color").ne("red")).apply(doc, conf));
        /*
        assertFalse(filter(where("store..price").gt(10)).apply(doc, conf));
        assertFalse(filter(where("$.store..price").gte(100)).apply(doc, conf));
        assertTrue(filter(where("$.store..category").ne("fiction")).apply(doc, conf));
        assertTrue(filter(where("$.store.bicycle.color").ne("blue")).apply(doc, conf));
        assertTrue(filter(where("$.store..color").exists(true)).apply(doc, conf));
        assertTrue(filter(where("$.store..color").regex(Pattern.compile("^r.d$"))).apply(doc, conf));
        assertTrue(filter(where("$.store..color").type(String.class)).apply(doc, conf));
        assertTrue(filter(where("$.store..price").is(12.99)).apply(doc, conf));
        assertFalse(filter(where("$.store..price").is(13.99)).apply(doc, conf));

        assertFalse(filter(where("$.store..flavor").exists(true)).apply(doc, conf));
        */
    }

    @Test
    public void not_empty_filter_evaluates() {

        String json = "{\n" +
                "    \"fields\": [\n" +
                "        {\n" +
                "            \"errors\": [], \n" +
                "            \"name\": \"\", \n" +
                "            \"empty\": true \n" +
                "        }, \n" +
                "        {\n" +
                "            \"errors\": [], \n" +
                "            \"name\": \"foo\"\n" +
                "        }, \n" +
                "        {\n" +
                "            \"errors\": [\n" +
                "                \"first\", \n" +
                "                \"second\"\n" +
                "            ], \n" +
                "            \"name\": \"invalid\"\n" +
                "        }\n" +
                "    ]\n" +
                "}\n";


        Object doc = JsonProviderFactory.createProvider().parse(json);

        List<Map<String, Object>> result = JsonPath.read(doc, "$.fields[?]", filter(where("errors").notEmpty()));
        assertEquals(1, result.size());
        System.out.println(result);

        List<Map<String, Object>> result2 = JsonPath.read(doc, "$.fields[?]", filter(where("name").notEmpty()));
        assertEquals(2, result2.size());
        System.out.println(result2);
    }

    @Test(expected = InvalidCriteriaException.class)
    public void filter_path_must_be_absolute() {

        where("$.store.*").size(4);
    }

}
