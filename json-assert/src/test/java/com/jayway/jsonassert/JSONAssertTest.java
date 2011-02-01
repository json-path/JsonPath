package com.jayway.jsonassert;

import org.junit.Test;

import static org.hamcrest.Matchers.*;

/**
 * User: kalle stenflo
 * Date: 1/21/11
 * Time: 4:04 PM
 */
public class JsonAssertTest {

    private static String TEST_DOCUMENT = "{  \"nullField\" : null \"stringField\" : \"string-field\" , \"numberField\" : 1234 , \"booleanField\" : true , \"subDocument\" : {\"subField\" : \"sub-field\"} , \"stringList\" : [\"ONE\", \"TWO\"], \"objectList\" : [{\"subField\" : \"sub-field-0\"}, {\"subField\" : \"sub-field-1\"}], \"listList\" : [[\"0.0\", \"0.1\"], [\"1.0\", \"1.1\"]], }";


    @Test
    public void a_path_can_be_asserted_with_matcher() throws Exception {

        JsonAssert.with(TEST_DOCUMENT).assertThat("stringField", equalTo("string-field"));
    }

    @Test
    public void list_content_can_be_asserted_with_matcher() throws Exception {

        JsonAssert.with(TEST_DOCUMENT).assertThat("stringList", hasItems("ONE", "TWO"));
    }

    @Test
    public void map_content_can_be_asserted_with_matcher() throws Exception {

        JsonAssert.with(TEST_DOCUMENT).assertThat("subDocument", hasEntry("subField", "sub-field"));
    }

    @Test
    public void a_sub_document_can_asserted_on__by_path() throws Exception {
        JsonAssert.with(TEST_DOCUMENT).assertThat("subDocument.subField", is(equalTo("sub-field")));
    }

    @Test
    public void a_path_can_be_asserted_equal_to() throws Exception {

        JsonAssert.with(TEST_DOCUMENT).assertEquals("stringField", "string-field");
    }

    @Test
    public void a_path_can_be_asserted_is_null() throws Exception {

        JsonAssert.with(TEST_DOCUMENT).assertNull("nullField");
    }

    @Test(expected = AssertionError.class)
    public void failed_assert_throws() throws Exception {

        JsonAssert.with(TEST_DOCUMENT).assertThat("stringField", equalTo("SOME CRAP"));
    }

    @Test
    public void multiple_asserts_can_be_chained() throws Exception {

        JsonAssert.with(TEST_DOCUMENT)
                .assertThat("stringField", equalTo("string-field"))
                .assertThat("numberField", is(notNullValue()))
                .and()
                .assertNull("nullField")
                .and()
                .assertEquals("stringField", "string-field");

    }


}
