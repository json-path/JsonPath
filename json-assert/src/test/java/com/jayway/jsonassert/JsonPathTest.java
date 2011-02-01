package com.jayway.jsonassert;

import com.jayway.jsonassert.impl.JsonPathImpl;
import org.junit.Test;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * Created by IntelliJ IDEA.
 * User: kallestenflo
 * Date: 2/1/11
 * Time: 10:08 AM
 */
public class JsonPathTest {

    private static String TEST_DOCUMENT = "{  \"nullField\" : null, \"stringField\" : \"string-field\" , \"numberField\" : 1234 , \"doubleField\" : 12.34 , \"booleanField\" : true , \"subDocument\" : {\"subField\" : \"sub-field\"} , \"stringList\" : [\"ONE\", \"TWO\"], \"objectList\" : [{\"subField\" : \"sub-field-0\", \"subDoc\": {\"subField\": \"A\"}}, {\"subField\" : \"sub-field-1\", \"subDoc\": {\"subField\": \"B\"}}], \"listList\" : [[\"0.0\", \"0.1\"], [\"1.0\", \"1.1\"]], }";
    private static String TEST_DOCUMENT_ARRAY = "{  \"listList\" : [[\"0.0\", \"0.1\"], [\"1.0\", \"1.1\"]], }";
    private static String TEST_DEEP_PATH_DOCUMENT = "{  \"a\" :  {  \"b\" : {  \"c\" : {  \"say\" : \"hello\" } } }}";
    private static String TEST_ARRAY = "[{\"name\" : \"name0\"}, {\"name\" : \"name1\"}]";
    private static String TEST_DEEP_PATH_DOCUMENT_ARRAY = "{  \"arr0\" : [{  \"arr0_0\" : [{  \"arr0_0_0\" : [{\"val\": \"0A\"}, {\"val\": \"1A\"}] }, {  \"arr0_0\" : [{  \"arr0_0_0\" : [{\"val\": \"1A\"}, {\"val\": \"1B\"}] }] } ] }";


    @Test
    public void a_string_field_can_be_accessed() throws Exception {
        JsonPath reader = JsonPathImpl.parse(TEST_DOCUMENT);

        assertEquals("string-field", reader.getString("stringField"));
        assertEquals("string-field", reader.get("stringField"));
    }

    @Test
    public void is_null_returns_true_for_null_fields() throws Exception {
        JsonPath reader = JsonPathImpl.parse(TEST_DOCUMENT);

        assertTrue(reader.isNull("nullField"));
    }

    @Test
    public void is_null_returns_false_for_not_null_fields() throws Exception {
        JsonPath reader = JsonPathImpl.parse(TEST_DOCUMENT);

        assertFalse(reader.isNull("stringField"));
    }


    @Test
    public void a_long_field_can_be_accessed() throws Exception {
        JsonPath reader = JsonPathImpl.parse(TEST_DOCUMENT);

        assertTrue(1234L == reader.getLong("numberField"));
        assertEquals(1234L, reader.get("numberField"));
    }

    @Test
    public void a_double_field_can_be_accessed() throws Exception {
        JsonPath reader = JsonPathImpl.parse(TEST_DOCUMENT);

        assertEquals(12.34D, reader.getDouble("doubleField"), 0.001);
        assertEquals(12.34D, reader.<Double>get("doubleField"), 0.001);

    }

    @Test
    public void a_boolean_field_can_be_accessed() throws Exception {
        JsonPath reader = JsonPathImpl.parse(TEST_DOCUMENT);

        assertEquals(true, reader.getBoolean("booleanField"));
        assertEquals(true, reader.get("booleanField"));
    }

    @Test
    public void a_path_can_be_checked_for_existence() throws Exception {
        JsonPath reader = JsonPathImpl.parse(TEST_DEEP_PATH_DOCUMENT);

        assertTrue(reader.hasJsonPath("a.b.c.say"));
    }

    @Test
    public void a_path_can_be_checked_for_non_existence() throws Exception {
        JsonPath reader = JsonPathImpl.parse(TEST_DEEP_PATH_DOCUMENT);

        assertFalse(reader.hasJsonPath("a.b.c.FOO"));
    }

    @Test
    public void a_string_field_can_be_accessed_in_a_nested_document() throws Exception {
        JsonPath reader = JsonPathImpl.parse(TEST_DOCUMENT);

        assertEquals("sub-field", reader.getString("subDocument.subField"));
        assertEquals("sub-field", reader.get("subDocument.subField"));
    }

    @Test
    public void a_list_can_be_accessed_in_a_document() throws Exception {
        JsonPath reader = JsonPathImpl.parse(TEST_DOCUMENT);

        assertEquals(2, reader.<String>getList("stringList").size());
        assertEquals(2, reader.<List<String>>get("stringList").size());
    }

    @Test
    public void a_list_can_be_accessed_by_array_index() throws Exception {
        JsonPath reader = JsonPathImpl.parse(TEST_DOCUMENT);

        assertEquals("ONE", reader.getString("stringList[0]"));
        assertEquals("TWO", reader.getString("stringList[1]"));

        assertEquals("ONE", reader.get("stringList[0]"));
        assertEquals("TWO", reader.get("stringList[1]"));
    }

    @Test
    public void a_list_can_be_accessed_by_groovy_index() throws Exception {
        JsonPath reader = JsonPathImpl.parse(TEST_DOCUMENT);


        assertEquals("ONE", reader.getString("stringList.get(0)"));
        assertEquals("TWO", reader.getString("stringList.get(1)"));

        assertEquals("ONE", reader.get("stringList.get(0)"));
        assertEquals("TWO", reader.get("stringList.get(1)"));
    }

    @Test
    public void a_document_contained_in_a_list_can_be_accessed_by_array_index() throws Exception {
        JsonPath reader = JsonPathImpl.parse(TEST_DOCUMENT);

        assertEquals("sub-field-0", reader.getString("objectList[0].subField"));
        assertEquals("sub-field-0", reader.get("objectList[0].subField"));
    }


    @Test
    public void a_document_contained_in_a_list_can_be_accessed_by_groovy_index() throws Exception {
        JsonPath reader = JsonPathImpl.parse(TEST_DOCUMENT);

        assertEquals("sub-field-0", reader.getString("objectList.get(0).subField"));
        assertEquals("sub-field-0", reader.get("objectList.get(0).subField"));
    }

    @Test
    public void an_array_in_an_array_can_be_accessed_by_array_index() throws Exception {
        JsonPath reader = JsonPathImpl.parse(TEST_DOCUMENT_ARRAY);

        assertEquals("0.0", reader.getString("listList[0][0]"));

        assertEquals("0.1", reader.getString("listList[0][1]"));
        assertEquals("1.0", reader.getString("listList[1][0]"));
        assertEquals("1.1", reader.getString("listList[1][1]"));

        assertEquals("0.0", reader.get("listList[0][0]"));
        assertEquals("0.1", reader.get("listList[0][1]"));
        assertEquals("1.0", reader.get("listList[1][0]"));
        assertEquals("1.1", reader.get("listList[1][1]"));

    }

    @Test
    public void an_array_in_an_array_can_be_accessed_by_groovy_index() throws Exception {
        JsonPath reader = JsonPathImpl.parse(TEST_DOCUMENT_ARRAY);

        assertEquals("0.0", reader.getString("listList.get(0).get(0)"));
        assertEquals("0.1", reader.getString("listList.get(0).get(1)"));
        assertEquals("1.0", reader.getString("listList.get(1).get(0)"));
        assertEquals("1.1", reader.getString("listList.get(1).get(1)"));

        assertEquals("0.0", reader.get("listList.get(0).get(0)"));
        assertEquals("0.1", reader.get("listList.get(0).get(1)"));
        assertEquals("1.0", reader.get("listList.get(1).get(0)"));
        assertEquals("1.1", reader.get("listList.get(1).get(1)"));
    }

    @Test
    public void an_array_with_documents_can_be_accessed_by_index() throws Exception {
        JsonPath reader = JsonPathImpl.parse(TEST_ARRAY);

        assertEquals("name0", reader.getString("[0].name"));
        assertEquals("name1", reader.getString("[1].name"));

        assertEquals("name0", reader.get("[0].name"));
        assertEquals("name1", reader.get("[1].name"));
    }


    @Test
    public void a_nested_document_can_be_accessed_as_a_map() throws Exception {
        JsonPath reader = JsonPathImpl.parse(TEST_DOCUMENT);

        assertEquals("sub-field", reader.getMap("subDocument").get("subField"));

        assertEquals("sub-field", reader.<Map>get("subDocument").get("subField"));
    }


    @Test
    public void a_deep_document_path_can_be_read() throws Exception {
        JsonPath reader = JsonPathImpl.parse(TEST_DEEP_PATH_DOCUMENT);

        assertEquals("hello", reader.getString("a.b.c.say"));

        assertEquals("hello", reader.get("a.b.c.say"));
    }
/*
    @Test(expected = InvalidPathException.class)
    public void exception_is_thrown_when_field_is_not_found() throws Exception {
        JSONReader reader = JsonQueryNew.parse(TEST_DOCUMENT);

        reader.getString("invalidProperty");
    }
*/

    @Test
    public void null_is_returned_when_field_is_not_found() throws Exception {
        JsonPath reader = JsonPathImpl.parse(TEST_DOCUMENT);

        assertNull(reader.getString("invalidProperty"));
    }

    public void null_is_when_field_is_not_found_with_get() throws Exception {
        JsonPath reader = JsonPathImpl.parse(TEST_DOCUMENT);

        assertNull(reader.get("invalidProperty"));
    }


    @Test
    public void array_wildcard_property_extract() throws Exception {
        JsonPath reader = JsonPathImpl.parse(TEST_DOCUMENT);

        assertEquals(2, reader.<String>getList("objectList[*].subField").size());

        assertEquals(2, reader.<List<String>>get("objectList[*].subField").size());


    }

    @Test
    public void array_wildcard_property_extract_new() throws Exception {
        JsonPath reader = JsonPathImpl.parse(TEST_DOCUMENT);

        assertEquals("A", reader.<String>getList("objectList[*].subDoc.subField").get(0));
        assertEquals("B", reader.<String>getList("objectList[*].subDoc.subField").get(1));

        assertEquals("A", reader.<List<String>>get("objectList[*].subDoc.subField").get(0));
        assertEquals("B", reader.<List<String>>get("objectList[*].subDoc.subField").get(1));


    }


    @Test
    public void list_to_string_returns_json() throws Exception {
        JsonPath reader = JsonPathImpl.parse(TEST_DOCUMENT);

        assertEquals("[\"ONE\",\"TWO\"]", reader.getList("stringList").toString());
    }


    @Test
    public void get_sub_reader_for_document() throws Exception {
        JsonPath reader = JsonPathImpl.parse(TEST_DOCUMENT);

        assertEquals("sub-field", reader.getReader("subDocument").getString("subField"));
    }

    @Test
    public void get_sub_reader_for_list() throws Exception {
        JsonPath reader = JsonPathImpl.parse(TEST_DOCUMENT);

        assertEquals("ONE", reader.getReader("stringList").get("[0]"));
    }


}

