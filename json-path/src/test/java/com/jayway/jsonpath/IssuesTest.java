package com.jayway.jsonpath;

import com.jayway.jsonpath.internal.IOUtils;
import com.jayway.jsonpath.spi.JsonProviderFactory;
import com.jayway.jsonpath.spi.impl.JacksonProvider;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;

import java.io.InputStream;
import java.util.List;
import java.util.regex.Matcher;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertThat;

/**
 * Created by IntelliJ IDEA.
 * User: kallestenflo
 * Date: 2/29/12
 * Time: 8:42 AM
 */
public class IssuesTest {

    @Test
    public void issue_11() throws Exception {
        String json = "{ \"foo\" : [] }";
        List<String> result = JsonPath.read(json, "$.foo[?(@.rel= 'item')][0].uri");

        System.out.println(JsonPath.compile("$.foo[?(@.rel= 'item')][0].uri").isPathDefinite());
        System.out.println(JsonPath.compile("$.foo[?(@.rel= 'item')][0]").isPathDefinite());
        System.out.println(JsonPath.compile("$.foo[?(@.rel= 'item')]").isPathDefinite());

        assertTrue(result.isEmpty());
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
            IOUtils.closeQuietly(is);
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
        String json = "{\"a\":{\"b\":1,\"c\":2}}";
        System.out.println(JsonPath.read(json, "a.d"));
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
    public void issue_29_b() throws Exception {
        String json = "{\"list\": [ { \"a\":\"atext\", \"b\":{ \"b-a\":\"batext\", \"b-b\":\"bbtext\" } }, { \"a\":\"atext2\", \"b\":{ \"b-a\":\"batext2\", \"b-b\":\"bbtext2\" } } ] }";
        List<String> result = JsonPath.read(json, "$.list[?]", Filter.filter(Criteria.where("b.b-a").eq("batext2")));

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


}
