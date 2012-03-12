package com.jayway.jsonpath;

import org.junit.Test;

import java.util.Collections;
import java.util.Map;

import static junit.framework.Assert.assertEquals;

/**
 * Created by IntelliJ IDEA.
 * User: kallestenflo
 * Date: 3/11/12
 * Time: 5:07 PM
 */
public class JsonModelSubModelTest {


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
                    "      \"dot.notation\": \"new\",\n" +
                    "      \"book\": { \"category\": \"fiction\",\n" +
                    "                 \"author\": \"J. R. R. Tolkien\",\n" +
                    "                 \"title\": \"The Lord of the Rings\",\n" +
                    "                 \"isbn\": \"0-395-19395-8\",\n" +
                    "                 \"price\": 22.99\n" +
                    "                }\n" +
                    "      }\n" +
                    "    }\n" +
                    "  }\n" +
                    "}";


    @Test(expected = IllegalArgumentException.class)
    public void a_sub_model_path_must_be_definite() throws Exception {
        JsonModel.model(DOCUMENT).getSubModel("$store.book[*]");
    }

    @Test
    public void test_a_sub_model_can_be_fetched_and_read() throws Exception {
        JsonModel model = JsonModel.model(DOCUMENT);
        assertEquals("Nigel Rees", model.getSubModel("$store.book[0]").get("author"));
        assertEquals("Nigel Rees", model.getSubModel(JsonPath.compile("$store.book[0]")).get("author"));
    }


    @Test
    public void when_a_sub_model_is_updated_the_master_model_is_updated() throws Exception {

        JsonModel model = JsonModel.model(DOCUMENT);

        JsonModel subModel = model.getSubModel("store.book[0]");
        subModel.opsForObject().put("author", "kalle");

        assertEquals("kalle", model.get("store.book[0].author"));
    }
    
    @Test
    public void when_a_sub_model_root_is_transformed_the_master_model_is_updated() throws Exception {
            
        JsonModel model = JsonModel.model(DOCUMENT);

        JsonModel subModel = model.getSubModel("store.book[0]");
        subModel.opsForObject().transform(new Transformer<Map<String, Object>>() {
            @Override
            public Object transform(Map<String, Object> obj) {
                return Collections.singletonMap("prop", "new");
            }
        });
        assertEquals("new", model.get("store.book[0].prop"));
    }

    @Test
    public void when_a_sub_model_child_is_transformed_the_master_model_is_updated() throws Exception {

        JsonModel model = JsonModel.model(DOCUMENT);

        JsonModel subModel = model.getSubModel("store.bicycle.book");
        subModel.opsForObject().transform(new Transformer<Map<String, Object>>() {
            @Override
            public Object transform(Map<String, Object> obj) {
                return Collections.singletonMap("prop", "new");
            }
        });
        assertEquals("new", model.get("store.bicycle.book.prop"));
    }

}
