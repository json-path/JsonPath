package com.jayway.jsonassert;

import com.jayway.jsonassert.impl.JSONAsserter;
import org.junit.Test;

import static org.hamcrest.Matchers.*;

/**
 * User: kalle stenflo
 * Date: 1/21/11
 * Time: 4:04 PM
 */
public class JSONAsserterTest {

    private static String TEST_DOCUMENT = "{  \"nullField\" : null \"stringField\" : \"string-field\" , \"numberField\" : 1234 , \"booleanField\" : true , \"subDocument\" : {\"subField\" : \"sub-field\"} , \"stringList\" : [\"ONE\", \"TWO\"], \"objectList\" : [{\"subField\" : \"sub-field-0\"}, {\"subField\" : \"sub-field-1\"}], \"listList\" : [[\"0.0\", \"0.1\"], [\"1.0\", \"1.1\"]], }";


    @Test
    public void a_path_can_be_asserted_with_matcher() throws Exception {

        JSONAsserter.with(TEST_DOCUMENT).assertThat("stringField", equalTo("string-field"));
    }

    @Test
    public void a_path_can_be_asserted_equal_to() throws Exception {

        JSONAsserter.with(TEST_DOCUMENT).assertEquals("stringField", "string-field");
    }

    @Test
    public void a_path_can_be_asserted_is_null() throws Exception {

        JSONAsserter.with(TEST_DOCUMENT).assertNull("nullField");
    }

    @Test(expected = AssertionError.class)
    public void failed_assert_throws() throws Exception {

        JSONAsserter.with(TEST_DOCUMENT).assertThat("stringField", equalTo("SOME CRAP"));
    }


    @Test
    public void multiple_asserts_can_be_chained() throws Exception {

        JSONAsserter.with(TEST_DOCUMENT)
                .assertThat("stringField", equalTo("string-field"))
                .and()
                .assertNull("nullField")
                .and()
                .assertThat("numberField", is(notNullValue()))
                .assertEquals("stringField", "string-field");

    }




}
