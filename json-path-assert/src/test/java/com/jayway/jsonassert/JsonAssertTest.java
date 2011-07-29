package com.jayway.jsonassert;

import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.jayway.jsonpath.json.JsonElement;
import com.jayway.jsonpath.json.JsonException;
import com.jayway.jsonpath.json.JsonFactory;

import java.io.InputStream;

import static com.jayway.jsonassert.JsonAssert.*;
import static org.hamcrest.Matchers.*;

/**
 * User: kalle stenflo
 * Date: 1/21/11
 * Time: 4:04 PM
 */
public abstract class JsonAssertTest {

    public final static String JSON =
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
                    "      \"price\": 19.95\n," +
                    "      \"nullValue\": null\n" +
                    "    }\n" +
                    "  }\n" +
                    "}";
	
    protected  JsonFactory factory = null;
    @Before
    public void init(){
    	init_factory();
    }
	protected void init_factory(){


	}
    
    @Test
    public void links_document() throws Exception {

        with(getResourceAsStream("links.json")).assertEquals("count", 2)
                .assertThat("links.gc:this.href", endsWith("?pageNumber=1&pageSize=2"))
                .assertNotDefined("links.gc:prev")
                .assertNotDefined("links.gc:next")
                .assertThat("rows", collectionWithSize(equalTo(2)));

    }


    @Test
    public void a_document_can_be_expected_not_to_contain_a_path() throws Exception {
        with(JSON).assertNotDefined("$.store.bicycle.cool");
    }

    @Test
    public void a_value_can_asserted_to_be_null() throws Exception {
        with(JSON).assertEquals("$.store.bicycle.nullValue",factory.createJsonPrimitive(null));
    }

    @Test
    public void ends_with_evalueates() throws Exception {
        with(JSON).assertThat("$.store.book[0].category", endsWith("nce"));
    }

    @Test
    public void a_path_can_be_asserted_with_matcher() throws Exception {

        with(JSON).assertThat("$.store.bicycle.color", equalTo(w("red")))
                .assertThat("$.store.bicycle.price", equalTo(w(19.95D)));
    }

    @Test
    public void list_content_can_be_asserted_with_matcher() throws Exception {

        with(JSON).assertThat("$..book[*].author", hasItems( factory.createJsonPrimitive("Nigel Rees"), factory.createJsonPrimitive("Evelyn Waugh"), factory.createJsonPrimitive("Herman Melville"),factory.createJsonPrimitive( "J. R. R. Tolkien")));

        with(JSON).assertThat("$..author.(value)", hasItems( factory.createJsonPrimitive("Nigel Rees"), factory.createJsonPrimitive("Evelyn Waugh"), factory.createJsonPrimitive("Herman Melville"),factory.createJsonPrimitive( "J. R. R. Tolkien")))
                  .assertThat("$..author.(value)", is(collectionWithSize(equalTo(4))));
    }

    @Test
    public void list_content_can_be_asserted_with_nested_matcher() throws Exception {
        with(JSON).assertThat("$..book[*]", hasItems(hasEntry("author", factory.createJsonPrimitive("Nigel Rees")), hasEntry("author", factory.createJsonPrimitive("Evelyn Waugh"))));
    }

    @Test
    public void map_content_can_be_asserted_with_matcher() throws Exception {

        with(JSON).assertThat("$.store.book[0]", hasEntry("category", w("reference")))
                .assertThat("$.store.book[0]", hasEntry("title", w("Sayings of the Century")))
                .and()
                .assertThat("$..book[0]", hasEntry("category", w("reference")))
                .and()
                .assertThat("$.store.book[0]", mapContainingKey(equalTo("category")))
                .and()
                .assertThat("$.store.book[0]", mapContainingValue(equalTo( w("reference"))));

        with(JSON).assertThat("$.['store'].['book'][0]", hasEntry(w("category"), w("reference")))
                .assertThat("$.['store'].['book'][0]", hasEntry("title", w("Sayings of the Century")))
                .and()
                .assertThat("$..['book'][0]", hasEntry(w("category"), w("reference")))
                .and()
                .assertThat("$.['store'].['book'][0]", mapContainingKey(equalTo(("category"))))
                .and()
                .assertThat("$.['store'].['book'][0]", mapContainingValue(equalTo(w("reference"))));
    }

    
    private JsonElement w(Object obj) throws JsonException {
    	return factory.createJsonPrimitive(obj);
    	
	}
    private JsonElement[] w(Object ... objs) throws JsonException {
    	JsonElement je[]= new JsonElement[objs.length]; 
    	for(int i=0;i<objs.length;i++){
    		je[i] = w(objs[i]);
    	}
    	return je;
	}


	@Test
    public void an_empty_collection() throws Exception {
        with(JSON).assertThat("$.store.book[?(@.category = 'x')]", isnull());
    }

    @Test
    public void a_path_can_be_asserted_equal_to() throws Exception {

        with(JSON).assertEquals("$.store.book[0].title", w("Sayings of the Century"))
                .assertThat("$.store.book[0].title", equalTo(w("Sayings of the Century")));

        with(JSON).assertEquals("$['store']['book'][0].['title']", w("Sayings of the Century"))
                .assertThat("$['store'].book[0].title", equalTo(w("Sayings of the Century")));
    }

    @Test
    public void no_hit_returns_null() throws Exception {
        with(JSON).assertThat("$.store.book[1000]", equalTo(null));
    }

    @Test
    public void invalid_path() throws Exception {
    	
        with(JSON).assertThat("$.store.book[*].fooBar", Matchers.<Object>nullValue());
    }

    @Test
    public void path_including_wildcard_path_followed_by_another_path_concatenates_results_to_list() throws Exception {
        with(getResourceAsStream("lotto.json")).assertThat("lotto.winners[*].winnerId", hasItems( factory.createJsonPrimitive(23), factory.createJsonPrimitive(54)));
    }


    private InputStream getResourceAsStream(String resourceName) {
        return getClass().getClassLoader().getResourceAsStream(resourceName);
    }

}
