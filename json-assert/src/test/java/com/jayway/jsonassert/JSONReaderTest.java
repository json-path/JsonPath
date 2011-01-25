package com.jayway.jsonassert;

import com.jayway.jsonassert.impl.JSONReaderImpl;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.junit.Test;

import java.io.InputStreamReader;
import java.text.ParseException;
import java.util.List;

import static org.junit.Assert.*;

/**
 * User: kalle stenflo
 * Date: 1/20/11
 * Time: 9:31 AM
 */
public class JSONReaderTest {


    private static String TEST_DOCUMENT = "{  \"nullField\" : null \"stringField\" : \"string-field\" , \"numberField\" : 1234 , \"doubleField\" : 12.34 , \"booleanField\" : true , \"subDocument\" : {\"subField\" : \"sub-field\"} , \"stringList\" : [\"ONE\", \"TWO\"], \"objectList\" : [{\"subField\" : \"sub-field-0\"}, {\"subField\" : \"sub-field-1\"}], \"listList\" : [[\"0.0\", \"0.1\"], [\"1.0\", \"1.1\"]], }";
    private static String TEST_DOCUMENT_ARRAY = "{  \"listList\" : [[\"0.0\", \"0.1\"], [\"1.0\", \"1.1\"]], }";
    private static String TEST_DEEP_PATH_DOCUMENT = "{  \"a\" :  {  \"b\" : {  \"c\" : {  \"say\" : \"hello\" } } }}";
    private static String TEST_ARRAY = "[{\"name\" : \"name0\"}, {\"name\" : \"name1\"}]";


    @Test(expected = ParseException.class)
    public void invalid_json_not_accepted() throws Exception {
        JSONReaderImpl.parse("not json");
    }

    @Test
    public void reader_can_be_created_with_input_stream() throws Exception {

        JSONReader reader = JSONAssert.parse(getInputStreamReader("json-test-doc.json"), true);

        assertEquals("donut", reader.getString("type"));

        assertThat(reader.<String>getList("toppings"), Matchers.hasItems("Glazed", "Sugar"));
    }


    @Test
    public void a_string_field_can_be_accessed() throws Exception {
        JSONReader reader = JSONAssert.parse(TEST_DOCUMENT);

        assertEquals("string-field", reader.getString("stringField"));
    }

    @Test
    public void is_null_returns_true_for_null_fields() throws Exception {
        JSONReader reader = JSONAssert.parse(TEST_DOCUMENT);

        assertTrue(reader.isNull("nullField"));
    }

    @Test
    public void is_null_returns_false_for_not_null_fields() throws Exception {
        JSONReader reader = JSONAssert.parse(TEST_DOCUMENT);

        assertFalse(reader.isNull("stringField"));
    }


    @Test
    public void a_long_field_can_be_accessed() throws Exception {
        JSONReader reader = JSONAssert.parse(TEST_DOCUMENT);

        assertTrue(1234L == reader.getLong("numberField"));
    }

    @Test
    public void a_double_field_can_be_accessed() throws Exception {
        JSONReader reader = JSONAssert.parse(TEST_DOCUMENT);

        assertEquals(12.34D, reader.getDouble("doubleField"), 0.001);
    }

    @Test
    public void a_boolean_field_can_be_accessed() throws Exception {
        JSONReader reader = JSONAssert.parse(TEST_DOCUMENT);

        assertEquals(true, reader.getBoolean("booleanField"));
    }

    @Test
    public void a_path_can_be_checked_for_existence() throws Exception {
        JSONReader reader = JSONAssert.parse(TEST_DEEP_PATH_DOCUMENT);

        assertTrue(reader.hasJsonPath("a.b.c.say"));
    }

    @Test
    public void a_path_can_be_checked_for_non_existence() throws Exception {



        JSONReader reader = JSONAssert.parse(TEST_DEEP_PATH_DOCUMENT);

        assertFalse(reader.hasJsonPath("a.b.c.FOO"));
    }

    @Test
    public void a_string_field_can_be_accessed_in_a_nested_document() throws Exception {
        JSONReader reader = JSONAssert.parse(TEST_DOCUMENT);

        assertEquals("sub-field", reader.getString("subDocument.subField"));
    }

    @Test
    public void a_list_can_be_accessed_in_a_document() throws Exception {
        JSONReader reader = JSONAssert.parse(TEST_DOCUMENT);

        List<String> l = reader.<String>getList("stringList");

        assertEquals(2, l.size());
    }

    @Test
    public void a_list_can_be_accessed_by_array_index() throws Exception {
        JSONReader reader = JSONAssert.parse(TEST_DOCUMENT);

        assertEquals("ONE", reader.getString("stringList[0]"));

        assertEquals("TWO", reader.getString("stringList[1]"));
    }

    @Test
    public void a_list_can_be_accessed_by_groovy_index() throws Exception {
        JSONReader reader = JSONAssert.parse(TEST_DOCUMENT);

        assertEquals("ONE", reader.getString("stringList.get(0)"));

        assertEquals("TWO", reader.getString("stringList.get(1)"));
    }

    @Test
    public void a_document_contained_in_a_list_can_be_accessed_by_array_index() throws Exception {
        JSONReader reader = JSONAssert.parse(TEST_DOCUMENT);

        assertEquals("sub-field-0", reader.getString("objectList[0].subField"));
    }

    @Test
    public void a_document_contained_in_a_list_can_be_accessed_by_groovy_index() throws Exception {
        JSONReader reader = JSONAssert.parse(TEST_DOCUMENT);

        assertEquals("sub-field-0", reader.getString("objectList.get(0).subField"));
    }

    @Test
    public void an_array_in_an_array_can_be_accessed_by_array_index() throws Exception {
        JSONReader reader = JSONAssert.parse(TEST_DOCUMENT_ARRAY);

        assertEquals("0.0", reader.getString("listList[0][0]"));
        assertEquals("0.1", reader.getString("listList[0][1]"));
        assertEquals("1.0", reader.getString("listList[1][0]"));
        assertEquals("1.1", reader.getString("listList[1][1]"));
    }

    @Test
    public void an_array_in_an_array_can_be_accessed_by_groovy_index() throws Exception {
        JSONReader reader = JSONAssert.parse(TEST_DOCUMENT_ARRAY);

        assertEquals("0.0", reader.getString("listList.get(0).get(0)"));
        assertEquals("0.1", reader.getString("listList.get(0).get(1)"));
        assertEquals("1.0", reader.getString("listList.get(1).get(0)"));
        assertEquals("1.1", reader.getString("listList.get(1).get(1)"));
    }

    @Test
    public void an_array_with_documents_can_be_accessed_by_index() throws Exception {
        JSONReader reader = JSONAssert.parse(TEST_ARRAY);

        assertEquals("name0", reader.getString("[0].name"));
        assertEquals("name1", reader.getString("[1].name"));
    }

    @Test
    public void a_nested_document_can_be_accessed_as_a_map() throws Exception {
        JSONReader reader = JSONAssert.parse(TEST_DOCUMENT);

        assertEquals("sub-field", reader.getMap("subDocument").get("subField"));
    }


    @Test
    public void every_thing_can_be_fetched_as_object() throws Exception {
        JSONReader reader = JSONAssert.parse(TEST_DOCUMENT);

        assertEquals(true, reader.get("booleanField"));
        assertEquals(1234L, reader.get("numberField"));
        assertEquals("string-field", reader.get("stringField"));
    }

    @Test
    public void a_deep_document_path_can_be_read() throws Exception {
        JSONReader reader = JSONAssert.parse(TEST_DEEP_PATH_DOCUMENT);

        assertEquals("hello", reader.getString("a.b.c.say"));
    }

    @Test(expected = InvalidPathException.class)
    public void exception_is_thrown_when_field_is_not_found() throws Exception {
        JSONReader reader = JSONAssert.parse(TEST_DOCUMENT);

        String s = reader.getString("invalidProperty");

        System.out.println("S= " + s);

    }


    @Test
    public void array_wildcard_property_extract() throws Exception {
        JSONReader reader = JSONAssert.parse(TEST_DOCUMENT);

        List<String> l = reader.<String>getList("objectList[*].subField");

        assertEquals(2, l.size());

        /*

        assertEquals(2, reader.getList("objectList[*.subField]").size());

        assertEquals(2, reader.getList("objectList[subField='foo']").size());

        assertEquals(2, reader.getList("objectList[].subField#foo").size());

        */

    }


    @Test
    public void list_to_string_returns_json() throws Exception {
        JSONReader reader = JSONAssert.parse(TEST_DOCUMENT);


        assertEquals("[\"ONE\",\"TWO\"]", reader.getList("stringList").toString());
    }


    //----------------------------------------------------------
    //
    // helpers
    //
    //----------------------------------------------------------
    private InputStreamReader getInputStreamReader(String resource) {
        return new InputStreamReader(ClassLoader.getSystemResourceAsStream(resource));
    }
}
