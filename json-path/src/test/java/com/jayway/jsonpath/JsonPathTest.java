package com.jayway.jsonpath;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.jayway.jsonpath.json.JsonArray;
import com.jayway.jsonpath.json.JsonElement;
import com.jayway.jsonpath.json.JsonException;
import com.jayway.jsonpath.json.JsonFactory;
import com.jayway.jsonpath.json.JsonObject;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItems;
import static org.junit.Assert.*;

/**
 * Created by IntelliJ IDEA.
 * User: kallestenflo
 * Date: 2/2/11
 * Time: 3:07 PM
 */
public abstract class JsonPathTest {

    public final static String ARRAY = "[{\"value\": 1},{\"value\": 2}, {\"value\": 3},{\"value\": 4}]";

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
                    "      \"foo:bar\": \"fooBar\"\n" +
                    "    }\n" +
                    "  }\n" +
                    "}";

    @Before
    public void init_factory(){
    	
    }
    
    @Test
    public void filter_an_array() throws Exception {
        JsonElement matches = JsonPath.read(ARRAY, "$.[?(@.value = 1)]");

        assertEquals(true, matches.isJsonObject());
        System.out.println(matches);
    }

    @Test
    public void read_path_with_colon() throws Exception {

        assertEquals(JsonPath.read(DOCUMENT, "$.store.bicycle.foo:bar").toObject(), "fooBar");
        assertEquals(JsonPath.read(DOCUMENT, "$.['store'].['bicycle'].['foo:bar']").toObject(), "fooBar");
    }

    
    
    
    @Test
    public void parent_ref() throws Exception {

        
    	String doc = "{foo:{biz:{id:1}}}";
    	
    	
    	
    	assertEquals(JsonPath.read(doc, "$.foo.biz").toString(), "{\"id\":1}");
        
        JsonElement j = JsonPath.read(doc, "$.foo.biz");
        assertEquals(j.getParentReference().getParent().toString() , "{\"biz\":{\"id\":1}}");;
        assertEquals(j.getParentReference().getParent().getParentReference().getParent().toString(), "{\"foo\":{\"biz\":{\"id\":1}}}");
        
        
        doc = "{foo:{biz:[{Id:1},{Id:2},{Id:4,foo:1234}]}}";    	
    	
        JsonElement root = JsonPath.parse(doc);
        assertEquals(JsonPath.read(doc, "$.foo.biz.[2].foo").toString(), "1234");
        assertEquals(JsonPath.read(doc, "$.foo.biz.[2].foo").getParentReference().getParent().toJsonObject().get("Id").toString(), "4");
        
        JsonElement doc_json = JsonFactory.getInstance().parse( "{foo:[[[1],2,3,4],1,2,3]}");
        JsonElement je = JsonPath.read(doc_json, "$.foo[0][0]");
        je.getParentReference().setReference( JsonFactory.getInstance().createJsonPrimitive("foo"));
        assertEquals(doc_json.toString(),"{\"foo\":[[\"foo\",2,3,4],1,2,3]}");
        
        

    }
    
    
    @Test
    public void type_test() throws Exception {

        
    	String doc = "{foo:{biz:{id:1}}}";
    	
    	assertEquals(JsonPath.read(doc, "$.foo.biz.(object)").toString(), "{\"id\":1}");
    	assertEquals(JsonPath.read(doc, "$.foo.biz.(collection)"), null);
        
        
        doc = "{foo:{biz:[{Id:1},{Id:2},{Id:4,foo:1234}]}}";    	
    	
        JsonElement root = JsonPath.parse(doc);
        assertEquals(JsonPath.read(doc, "$.foo.biz.(collection)[2].foo.(value)").toString(), "1234");

        

    }
    
    
    
    @Test
    public void read_document_from_root() throws Exception {

        com.jayway.jsonpath.json.JsonObject result = JsonPath.read(DOCUMENT, "$.store").toJsonObject();

        assertEquals(2, result.getProperties().size());


    }


    @Test
    public void read_store_book_1() throws Exception {

        JsonPath path = JsonPath.compile("$.store.book[1]");

        JsonElement map = path.read(DOCUMENT);

        assertEquals("Evelyn Waugh", map.toJsonObject().get("author").toObject());
    }

    @Test
    public void read_store_book_wildcard() throws Exception {
        JsonPath path = JsonPath.compile("$.store.book[*]");

        JsonElement list = path.read(DOCUMENT);

    }

    @Test
    public void read_store_book_author() throws Exception {
    	Iterable<String> l1 = toList(JsonPath.read(DOCUMENT, "$.store.book[0,1].author"));
    	assertThat(l1, hasItems("Nigel Rees", "Evelyn Waugh"));
        
    	Iterable<String> l2 = toList(JsonPath.read(DOCUMENT, "$.store.book[*].author"));
    	assertThat(l2, hasItems("Nigel Rees", "Evelyn Waugh", "Herman Melville", "J. R. R. Tolkien"));
        
    	Iterable<String> l3 = toList(JsonPath.read(DOCUMENT, "$.['store'].['book'][*].['author']"));
    	assertThat(l3, hasItems("Nigel Rees", "Evelyn Waugh", "Herman Melville", "J. R. R. Tolkien"));
                	
    }


    private <T> List<T> toList(JsonElement read) throws JsonException {
		List l = new ArrayList<T>();
    	for(JsonElement e: read.toJsonArray()){
    		l.add((T)	e.toObject());    		
    	}
    	return l ;
    }

	@Test
    public void all_authors() throws Exception {
		Iterable<String> l1 = toList(JsonPath.read(DOCUMENT, "$..author"));
        assertThat(l1, hasItems("Nigel Rees", "Evelyn Waugh", "Herman Melville", "J. R. R. Tolkien"));
    }


    @Test
    public void all_store_properties() throws Exception {
        JsonArray itemsInStore = JsonPath.read(DOCUMENT, "$.store.*").toJsonArray();

        assertEquals(JsonPath.read(itemsInStore, "$.[0].[0].author").toObject(), "Nigel Rees");
        assertEquals(JsonPath.read(itemsInStore, "$.[0][0].author").toObject(), "Nigel Rees");
    }

    @Test
    public void all_prices_in_store() throws Exception {

        assertThat(this.<Double>toList(JsonPath.read(DOCUMENT, "$.store..price")), hasItems(8.95D, 12.99D, 8.99D, 19.95D));

    }

    @Test
    public void access_array_by_index_from_tail() throws Exception {

        assertThat((String)JsonPath.read(DOCUMENT, "$..book[(@.length-1)].author").toObject(), equalTo("J. R. R. Tolkien"));
        assertThat((String)JsonPath.read(DOCUMENT, "$..book[-1:].author").toObject(), equalTo("J. R. R. Tolkien"));
    }

    @Test
    public void read_store_book_index_0_and_1() throws Exception {

        assertThat(this.<String>toList(JsonPath.read(DOCUMENT, "$.store.book[0,1].author")), hasItems("Nigel Rees", "Evelyn Waugh"));
        assertTrue(this.<String>toList(JsonPath.read(DOCUMENT, "$.store.book[0,1].author")).size() == 2);
    }

    @Test
    public void read_store_book_pull_first_2() throws Exception {

        assertThat(this.<String>toList(JsonPath.read(DOCUMENT, "$.store.book[:2].author")), hasItems("Nigel Rees", "Evelyn Waugh"));
        assertTrue(this.<String>toList(JsonPath.read(DOCUMENT, "$.store.book[:2].author")).size() == 2);
    }


    @Test
    public void read_store_book_filter_by_isbn() throws Exception {

        assertThat(this.<String>toList(JsonPath.read(DOCUMENT, "$.store.book[?(@.isbn)].isbn")), hasItems("0-553-21311-3", "0-395-19395-8"));
        assertTrue(this.<String>toList(JsonPath.read(DOCUMENT, "$.store.book[?(@.isbn)].isbn")).size() == 2);
    }

    @Test
    public void all_books_cheaper_than_10() throws Exception {
    	Object o = JsonPath.read(DOCUMENT, "$.store.book[?(@.price < 10)].title");
        assertThat(this.<String>toList(JsonPath.read(DOCUMENT, "$.store.book[?(@.price < 10)].title")), hasItems("Sayings of the Century", "Moby Dick"));

    }

    @Test
    public void all_books_with_category_reference() throws Exception {

    	Object o = JsonPath.read(DOCUMENT, "$..book[?(@.category = 'reference')].title");
        assertEquals(JsonPath.read(DOCUMENT, "$..book[?(@.category = 'reference')].title").toObject(), "Sayings of the Century");

    }

    @Test
    public void all_members_of_all_documents() throws Exception {
        JsonPath.read(DOCUMENT, "$..*");
    }

    @Test
    public void access_index_out_of_bounds_does_not_throw_exception() throws Exception {

        Object res = JsonPath.read(DOCUMENT, "$.store.book[100].author");

        assertNull(res);

        JsonElement res2 = JsonPath.read(DOCUMENT, "$.store.book[1, 200].author");


        assertEquals(res2.toObject(), "Evelyn Waugh");
        //assertNull(();
    }

    /*
    @Test(expected = InvalidPathException.class)
    public void invalid_space_path_throws_exception() throws Exception {
        JsonPath.read(DOCUMENT, "space is not good");
    }
    */

    @Test(expected = InvalidPathException.class)
    public void invalid_new_path_throws_exception() throws Exception {
        JsonPath.read(DOCUMENT, "new ");
    }
}
