package com.jayway.jsonpath;

import org.hamcrest.Matchers;
import org.junit.Test;

import java.util.List;
import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * User: kalle
 * Date: 8/22/13
 * Time: 10:39 AM
 */
public class NullHandlingTest {

    public static final String DOCUMENT = "{\n" +
            "   \"root-property\": \"root-property-value\",\n" +
            "   \"root-property-null\": null,\n" +
            "   \"children\": [\n" +
            "      {\n" +
            "         \"id\": 0,\n" +
            "         \"name\": \"name-0\",\n" +
            "         \"age\": 0\n" +
            "      },\n" +
            "      {\n" +
            "         \"id\": 1,\n" +
            "         \"name\": \"name-1\",\n" +
            "         \"age\": null" +
            "      },\n" +
            "      {\n" +
            "         \"id\": 3,\n" +
            "         \"name\": \"name-3\"\n" +
            "      }\n" +
            "   ]\n" +
            "}";


    @Test(expected = PathNotFoundException.class)
    public void not_defined_property_throws_PathNotFoundException () {
         JsonPath.read(DOCUMENT, "$.children[2].age");
    }

    @Test
    public void null_property_returns_null () {
        Integer age = JsonPath.read(DOCUMENT, "$.children[1].age");
        assertEquals(null, age);
    }

    @Test
    public void the_age_of_all_with_age_defined() {
        List<Integer> result = JsonPath.read(DOCUMENT, "$.children[*].age");

        assertThat(result, Matchers.hasItems(0, null));
    }
    @Test
    public void path2(){
        System.out.println(JsonPath.read("{\"a\":[{\"b\":1,\"c\":2},{\"b\":5,\"c\":2}]}", "a[?(@.b==4)].c"));
    }

    public void path(){
        System.out.println(JsonPath.read("{\"a\":[{\"b\":1,\"c\":2},{\"b\":5,\"c\":2}]}", "a[?(@.b==5)].d"));
    }


}
