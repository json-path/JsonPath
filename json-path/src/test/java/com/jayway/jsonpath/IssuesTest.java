package com.jayway.jsonpath;

import com.jayway.jsonpath.internal.Utils;
import com.jayway.jsonpath.spi.json.JsonProvider;
import com.jayway.jsonpath.spi.json.JsonProviderFactory;
import org.hamcrest.Matchers;
import org.junit.Test;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

import static junit.framework.Assert.*;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * Created by IntelliJ IDEA.
 * User: kallestenflo
 * Date: 2/29/12
 * Time: 8:42 AM
 */
public class IssuesTest {

    private static final JsonProvider jp = JsonProviderFactory.createProvider();

    @Test
    public void issue_36() {
        String json = "{\n" +
                "\n" +
                " \"arrayOfObjectsAndArrays\" : [ { \"k\" : [\"json\"] }, { \"k\":[\"path\"] }, { \"k\" : [\"is\"] }, { \"k\" : [\"cool\"] } ],\n" +
                "\n" +
                "  \"arrayOfObjects\" : [{\"k\" : \"json\"}, {\"k\":\"path\"}, {\"k\" : \"is\"}, {\"k\" : \"cool\"}]\n" +
                "\n" +
                " }";

        Object o1 = JsonPath.read(json, "$.arrayOfObjectsAndArrays..k ");
        Object o2 = JsonPath.read(json, "$.arrayOfObjects..k ");

        assertEquals("[[\"json\"],[\"path\"],[\"is\"],[\"cool\"]]", jp.toJson(o1));
        assertEquals("[\"json\",\"path\",\"is\",\"cool\"]", jp.toJson(o2));
    }

    @Test
    public void issue_11() throws Exception {
        String json = "{ \"foo\" : [] }";
        List<String> result = JsonPath.read(json, "$.foo[?(@.rel == 'item')][0].uri");
        assertTrue(result.isEmpty());
    }

    @Test(expected = PathNotFoundException.class)
    public void issue_11b() throws Exception {
        String json = "{ \"foo\" : [] }";
        JsonPath.read(json, "$.foo[0].uri");
    }

    @Test
    public void issue_15() throws Exception {
        String json = "{ \"store\": {\n" +
                "    \"book\": [ \n" +
                "      { \"category\": \"reference\",\n" +
                "        \"author\": \"Nigel Rees\",\n" +
                "        \"title\": \"Sayings of the Century\",\n" +
                "        \"price\": 8.95\n" +
                "      },\n" +
                "      { \"category\": \"fiction\",\n" +
                "        \"author\": \"Herman Melville\",\n" +
                "        \"title\": \"Moby Dick\",\n" +
                "        \"isbn\": \"0-553-21311-3\",\n" +
                "        \"price\": 8.99,\n" +
                "        \"retailer\": null, \n" +
                "        \"children\": true,\n" +
                "        \"number\": -2.99\n" +
                "      },\n" +
                "      { \"category\": \"fiction\",\n" +
                "        \"author\": \"J. R. R. Tolkien\",\n" +
                "        \"title\": \"The Lord of the Rings\",\n" +
                "        \"isbn\": \"0-395-19395-8\",\n" +
                "        \"price\": 22.99,\n" +
                "        \"number\":0,\n" +
                "        \"children\": false\n" +
                "      }\n" +
                "    ]\n" +
                "  }\n" +
                "}";

        List<String> titles = JsonPath.read(json, "$.store.book[?(@.children==true)].title");

        assertThat(titles, Matchers.contains("Moby Dick"));
        assertEquals(1, titles.size());
    }


    @Test
    public void issue_24() {

        InputStream is = null;
        try {
            is = this.getClass().getResourceAsStream("/issue_24.json");


            //Object o = JsonPath.read(is, "$.project[?(@.template.@key == 'foo')].field[*].@key");
            Object o = JsonPath.read(is, "$.project.field[*].@key");
            //Object o = JsonPath.read(is, "$.project.template[?(@.@key == 'foo')].field[*].@key");

            System.out.println(o);

            is.close();
        } catch (Exception e) {
            e.printStackTrace();
            Utils.closeQuietly(is);
        }

    }

    @Test
    public void issue_28_string() {
        String json = "{\"contents\": [\"one\",\"two\",\"three\"]}";

        List<String> result = JsonPath.read(json, "$.contents[?(@  == 'two')]");

        assertThat(result, Matchers.contains("two"));
        assertEquals(1, result.size());
    }

    @Test
    public void issue_37() {
        String json = "[\n" +
                "    {\n" +
                "        \"id\": \"9\",\n" +
                "        \"sku\": \"SKU-001\",\n" +
                "        \"compatible\": false\n" +
                "    },\n" +
                "    {\n" +
                "        \"id\": \"13\",\n" +
                "        \"sku\": \"SKU-005\",\n" +
                "        \"compatible\": true\n" +
                "    },\n" +
                "    {\n" +
                "        \"id\": \"11\",\n" +
                "        \"sku\": \"SKU-003\",\n" +
                "        \"compatible\": true\n" +
                "    }\n" +
                "]";

        List<Double> result = JsonPath.read(json, "$.[?(@.compatible == true)].sku");

        System.out.println(result);

        //assertThat(result.get(0), is(new Double(10.1)));
        //assertThat(result.get(1), is(new Double(21.0)));
    }


    @Test
    public void issue_38() {
        String json = "{\n" +
                "   \"datapoints\":[\n" +
                "      [\n" +
                "         10.1,\n" +
                "         13.0\n" +
                "      ],\n" +
                "      [\n" +
                "         21.0,\n" +
                "         22.0\n" +
                "      ]\n" +
                "   ]\n" +
                "}";

        List<Double> result = JsonPath.read(json, "$.datapoints.[*].[0]");

        assertThat(result.get(0), is(new Double(10.1)));
        assertThat(result.get(1), is(new Double(21.0)));
    }

    @Test
    public void issue_39() {
        String json = "{\n" +
                "    \"obj1\": {\n" +
                "        \"arr\": [\"1\", \"2\"]\n" +
                "    },\n" +
                "    \"obj2\": {\n" +
                "       \"arr\": [\"3\", \"4\"]\n" +
                "    }\n" +
                "}\n";

        List<String> result = JsonPath.read(json, "$..arr");
        assertThat(result.size(), is(2));
    }

    @Test
    public void issue_28_int() {
        String json = "{\"contents\": [1,2,3]}";

        List<Integer> result = JsonPath.read(json, "$.contents[?(@ == 2)]");

        assertThat(result, Matchers.contains(2));
        assertEquals(1, result.size());
    }

    @Test
    public void issue_28_boolean() {
        String json = "{\"contents\": [true, true, false]}";

        List<Boolean> result = JsonPath.read(json, "$.contents[?(@  == true)]");

        assertThat(result, Matchers.contains(true, true));
        assertEquals(2, result.size());
    }


    @Test(expected = PathNotFoundException.class)
    public void issue_22() throws Exception {

        Configuration configuration = Configuration.builder().options(Option.THROW_ON_MISSING_PROPERTY).build();
        //Configuration configuration = Configuration.defaultConfiguration();

        String json = "{\"a\":{\"b\":1,\"c\":2}}";
        System.out.println(JsonPath.parse(json, configuration).read("a.d"));
    }
    @Test
    public void issue_22c() throws Exception {
        Configuration configuration = Configuration.builder().build();

        String json = "{\"a\":{\"b\":1,\"c\":2}}";
        assertNull(JsonPath.parse(json, configuration).read("a.d"));
    }



    @Test
    public void issue_22b() throws Exception {
        String json = "{\"a\":[{\"b\":1,\"c\":2},{\"b\":5,\"c\":2}]}";
        System.out.println(JsonPath.read(json, "a[?(@.b==5)].d"));
        System.out.println(JsonPath.read(json, "a[?(@.b==5)].d"));
    }

    @Test(expected = PathNotFoundException.class)
    public void issue_26() throws Exception {
        String json = "[{\"a\":[{\"b\":1,\"c\":2}]}]";
        Object o = JsonPath.read(json, "$.a");
    }

    @Test
    public void issue_29_a() throws Exception {
        String json = "{\"list\": [ { \"a\":\"atext\", \"b.b-a\":\"batext2\", \"b\":{ \"b-a\":\"batext\", \"b-b\":\"bbtext\" } }, { \"a\":\"atext2\", \"b\":{ \"b-a\":\"batext2\", \"b-b\":\"bbtext2\" } } ] }";

        List<Map<String, Object>> result = JsonPath.read(json, "$.list[?(@['b.b-a']=='batext2')]");
        assertEquals(1, result.size());
        Object a = result.get(0).get("a");
        assertEquals("atext", a);

        result = JsonPath.read(json, "$.list[?(@.b.b-a=='batext2')]");
        assertEquals(1, result.size());
        assertEquals("atext2", result.get(0).get("a"));

        
    }
    
    @Test
    public void issue_29_b() throws Exception {
        String json = "{\"list\": [ { \"a\":\"atext\", \"b\":{ \"b-a\":\"batext\", \"b-b\":\"bbtext\" } }, { \"a\":\"atext2\", \"b\":{ \"b-a\":\"batext2\", \"b-b\":\"bbtext2\" } } ] }";
        List<String> result = JsonPath.read(json, "$.list[?]", Filter2.filter(Criteria2.where("b.b-a").eq("batext2")));

        assertTrue(result.size() == 1);
    }
    @Test
    public void issue_30() throws Exception {
        String json = "{\"foo\" : {\"@id\" : \"123\", \"$\" : \"hello\"}}";

        assertEquals("123", JsonPath.read(json, "foo.@id"));
        assertEquals("hello", JsonPath.read(json, "foo.$"));
    }

    @Test
    public void issue_32(){
        String json = "{\"text\" : \"skill: \\\"Heuristic Evaluation\\\"\", \"country\" : \"\"}";
        assertEquals("skill: \"Heuristic Evaluation\"", JsonPath.read(json, "$.text"));
    }

    @Test
    public void issue_33(){
        String json = "{ \"store\": {\n" +
                "    \"book\": [ \n" +
                "      { \"category\": \"reference\",\n" +
                "        \"author\": {\n" +
                "          \"name\": \"Author Name\",\n" +
                "          \"age\": 36\n" +
                "        },\n" +
                "        \"title\": \"Sayings of the Century\",\n" +
                "        \"price\": 8.95\n" +
                "      },\n" +
                "      { \"category\": \"fiction\",\n" +
                "        \"author\": \"Evelyn Waugh\",\n" +
                "        \"title\": \"Sword of Honour\",\n" +
                "        \"price\": 12.99,\n" +
                "        \"isbn\": \"0-553-21311-3\"\n" +
                "      }\n" +
                "    ],\n" +
                "    \"bicycle\": {\n" +
                "      \"color\": \"red\",\n" +
                "      \"price\": 19.95\n" +
                "    }\n" +
                "  }\n" +
                "}";

        List<Map<String, Object>> result = JsonPath.read(json, "$.store.book[?(@.author.age == 36)]");

        System.out.println(result);

    }

    @Test
    public void array_root() {
        String json = "[\n" +
                "    {\n" +
                "        \"a\": 1,\n" +
                "        \"b\": 2,\n" +
                "        \"c\": 3\n" +
                "    }\n" +
                "]";


        assertEquals(1, JsonPath.read(json, "$[0].a"));
    }

}
