package com.jayway.jsonpath;

import com.jayway.jsonpath.spi.JsonProvider;
import com.jayway.jsonpath.spi.JsonProviderFactory;
import com.jayway.jsonpath.util.ScriptEngineJsonPath;
import org.junit.Test;

/**
 * Created by IntelliJ IDEA.
 * User: kallestenflo
 * Date: 11/8/11
 * Time: 7:52 PM
 */
public class JsonModelTest {

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
                    "      \"dot.notation\": \"new\"\n" +
                    "    }\n" +
                    "  }\n" +
                    "}";

    public final static JsonModel MODEL = new JsonModel(DOCUMENT);

    @Test
    public void a_path_can_be_read() throws Exception {

        JsonPath path = JsonPath.compile("$.store.book[*].title");

        JsonModel model = new JsonModel(DOCUMENT);

        Object result = model.get(path);

        System.out.println(JsonProviderFactory.getInstance().toJson(result));


    }

    @Test
    public void test2() throws Exception {
        JsonPath path = JsonPath.compile("$..");

        System.out.println(ScriptEngineJsonPath.eval(DOCUMENT, path.getPath()));

        JsonModel model = new JsonModel(DOCUMENT);

        System.out.println(model.getJson(path));
    }


    @Test
    public void test3() throws Exception {
        JsonPath path = JsonPath.compile("$..[0]");

        //System.out.println(ScriptEngineJsonPath.eval(DOCUMENT, path.getPath()));

        System.out.println(MODEL.getJson(path));
    }

    @Test
    public void test4() throws Exception {
        JsonPath path = JsonPath.compile("$..*");

        System.out.println(ScriptEngineJsonPath.eval(DOCUMENT, path.getPath()));
        System.out.println("--------------------------------");

        JsonModel model = new JsonModel(DOCUMENT);
        System.out.println(model.getJson(path));
    }

    @Test
    public void test5() throws Exception {

        JsonModel model = new JsonModel(DOCUMENT);

        JsonModel model2 = model.getSubModel("store.book[0]");


        System.out.println(model2.getJson());

    }

}
