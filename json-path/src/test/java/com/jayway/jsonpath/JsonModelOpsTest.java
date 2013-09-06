package com.jayway.jsonpath;

import org.junit.Test;

import java.util.*;

import static com.jayway.jsonpath.JsonModel.model;
import static junit.framework.Assert.assertEquals;

/**
 * Created by IntelliJ IDEA.
 * User: kallestenflo
 * Date: 3/4/12
 * Time: 4:55 PM
 */
public class JsonModelOpsTest {

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
                    "      \"number\": 12,\n" +
                    "      \"dot.notation\": \"new\"\n" +
                    "    }\n" +
                    "  }\n" +
                    "}";

    @Test
    public void convert_values() throws Exception {

        JsonModel model = model(DOCUMENT).getSubModel("store.bicycle");

        JsonModel.ObjectOps ops = model.opsForObject();

        assertEquals(19.95D, ops.getDouble("price"));
        assertEquals(new Long(12), ops.getLong("number"));
        assertEquals(new Integer(12), ops.getInteger("number"));

        int i = ops.getInteger("number");
        long l = ops.getLong("number");
        double d = ops.getDouble("price");
    }

    @Test
    public void object_ops_can_update() throws Exception {

        JsonModel model = JsonModel.model(DOCUMENT);

        model.opsForObject("store.book[0]")
                .put("author", "Kalle")
                .put("price", 12.30D);

        assertEquals("Kalle", model.get("store.book[0].author"));
        assertEquals(12.30D, model.get("store.book[0].price"));
    }


    @Test
    public void array_ops_can_add_element() throws Exception {
        JsonModel model = JsonModel.model(DOCUMENT);

        Map<String, Object> newBook = new HashMap<String, Object>();
        newBook.put("category", "reference");
        newBook.put("author", "Kalle");
        newBook.put("title", "JSONPath book");
        newBook.put("isbn", "0-553-21311-34");
        newBook.put("price", 12.10D);

        model.opsForArray("store.book").add(newBook);

        JsonModel subModel = model.getSubModel("store.book[4]");

        assertEquals("reference", subModel.get("category"));
        assertEquals("Kalle", subModel.get("author"));
        assertEquals("JSONPath book", subModel.get("title"));
        assertEquals("0-553-21311-34", subModel.get("isbn"));
        assertEquals(12.10D, subModel.get("price"));
    }

    @Test
    public void ops_can_transform_object_root() throws Exception {

        Map<String, Object> rootDocument = new HashMap<String, Object>();
        rootDocument.put("category", "reference");
        rootDocument.put("author", "Kalle");
        rootDocument.put("title", "JSONPath book");
        rootDocument.put("isbn", "0-553-21311-34");
        rootDocument.put("price", 12.10D);

        JsonModel model = JsonModel.model(rootDocument);

        model.opsForObject().transform(new Transformer<Map<String, Object>>() {
            @Override
            public Object transform(Map<String, Object> obj, Configuration configuration) {
                obj.put("name", "kalle");
                return obj;
            }
        });
        assertEquals("kalle", model.get("name"));
    }
    
    
    @Test
    public void ops_can_transform_array_root() throws Exception {

        List<Object> rootDocument = new ArrayList<Object>();
        rootDocument.add(Collections.singletonMap("name", "kalle"));
        rootDocument.add(Collections.singletonMap("name", "bob"));
        rootDocument.add(Collections.singletonMap("name", "zak"));

        JsonModel model = JsonModel.model(rootDocument);

        model.opsForArray().transform(new Transformer<List<Object>>() {
            @Override
            public Object transform(List<Object> obj, Configuration configuration) {
                return Collections.singletonMap("root", "new");
            }
        });
        assertEquals("new", model.get("root"));
    }

    @Test
    public void ops_can_transform_nested_document() throws Exception {

        Map<String, Object> childDocument = new HashMap<String, Object>();
        childDocument.put("level", 1);

        Map<String, Object> rootDocument = new HashMap<String, Object>();
        rootDocument.put("category", "reference");
        rootDocument.put("author", "Kalle");
        rootDocument.put("title", "JSONPath book");
        rootDocument.put("isbn", "0-553-21311-34");
        rootDocument.put("price", 12.10D);
        rootDocument.put("child", childDocument);

        JsonModel model = JsonModel.model(rootDocument);

        model.opsForObject("child").transform(new Transformer<Map<String, Object>>() {
            @Override
            public Object transform(Map<String, Object> obj, Configuration configuration) {
                obj.put("name", "kalle");
                return obj;
            }
        });

        assertEquals("kalle", model.get("child.name"));
    }

    @Test
    public void arrays_can_be_mapped() throws Exception {
        JsonModel model = JsonModel.model(DOCUMENT);

        List<Book> books1 = model.opsForArray("store.book").toList().of(Book.class);
        List<Book> books2 = model.opsForArray("store.book").toListOf(Book.class);
        Set<Book> books3 = model.opsForArray("store.book").toSetOf(Book.class);

        assertEquals(4, books1.size());
        assertEquals(4, books2.size());
        assertEquals(4, books3.size());
    }

    @Test
    public void objects_can_be_mapped() throws Exception {
        JsonModel model = JsonModel.model(DOCUMENT);

        Book book = model.opsForObject("store.book[1]").to(Book.class);

        assertEquals("fiction", book.category);
        assertEquals("Evelyn Waugh", book.author);
        assertEquals("Sword of Honour", book.title);
        assertEquals(12.99D, book.price);

    }


    @Test
    public void object_can_be_transformed() throws Exception {

        Transformer transformer = new Transformer<Map<String, Object>>() {
            @Override
            public Map<String, Object> transform(Map<String, Object> model, Configuration configuration) {
                model.put("newProp", "newProp");
                return model;
            }
        };

        JsonModel model = JsonModel.model(DOCUMENT);

        model.opsForObject("store.book[1]").transform(transformer);

        assertEquals("newProp", model.get("store.book[1].newProp"));
    }

    @Test
    public void arrays_can_be_transformed() throws Exception {
        Transformer transformer = new Transformer<List<Object>>() {
            @Override
            public Object transform(List<Object> model, Configuration configuration) {

                for (Object o : model) {
                    Map<String, Object> map = (Map<String, Object>) o;
                    map.put("newProp", "newProp");
                }

                return model;
            }
        };

        JsonModel model = JsonModel.model(DOCUMENT);

        model.opsForArray("store.book").transform(transformer);

        assertEquals("newProp", model.get("store.book[1].newProp"));
    }

    @Test
    public void array_can_be_transformed_to_primitives() throws Exception {
        Transformer positionTransformer = new Transformer<List<Object>>() {
            private int i = 0;

            @Override
            public Object transform(List<Object> model, Configuration configuration) {
                List<Object> newList = new ArrayList<Object>();

                for (Object o : model) {
                    newList.add(new Integer(i++));
                }

                return newList;
            }
        };

        Transformer multiplyingTransformer = new Transformer<List<Object>>() {
            @Override
            public Object transform(List<Object> model, Configuration configuration) {

                for (int i = 0; i < model.size(); i++) {
                    int curr = (Integer) model.get(i);
                    model.set(i, curr * 2);
                }
                return model;
            }
        };


        JsonModel model = JsonModel.model(DOCUMENT);

        model.opsForArray("store.book").transform(positionTransformer).transform(multiplyingTransformer);

        assertEquals(2, model.get("store.book[1]"));
    }

    public static class Book {
        public String category;
        public String author;
        public String title;
        public String isbn;
        public Double price;
    }
}
