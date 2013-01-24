package com.jayway.jsonpath;

import org.junit.Test;

import java.util.*;

import static com.jayway.jsonpath.JsonModel.model;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;

/**
 * Created by IntelliJ IDEA.
 * User: kallestenflo
 * Date: 3/4/12
 * Time: 4:55 PM
 */
@SuppressWarnings("unchecked")
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

        assertThat(19.95D, equalTo(ops.getDouble("price")));
        assertThat(12L, equalTo(ops.getLong("number")));
        assertThat(12, equalTo(ops.getInteger("number")));

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

        assertThat("Kalle", equalTo(model.get("store.book[0].author")));
        assertThat(12.30D, equalTo(model.get("store.book[0].price")));
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

        assertThat("reference", equalTo(subModel.get("category")));
        assertThat("Kalle", equalTo(subModel.get("author")));
        assertThat("JSONPath book", equalTo(subModel.get("title")));
        assertThat("0-553-21311-34", equalTo(subModel.get("isbn")));
        assertThat(12.10D, equalTo(subModel.get("price")));
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
            public Object transform(Map<String, Object> obj) {
                obj.put("name", "kalle");
                return obj;
            }
        });
        assertThat("kalle", equalTo(model.get("name")));
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
            public Object transform(List<Object> obj) {
                return Collections.singletonMap("root", "new");
            }
        });
        assertThat("new", equalTo(model.get("root")));
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
            public Object transform(Map<String, Object> obj) {
                obj.put("name", "kalle");
                return obj;
            }
        });

        assertThat("kalle", equalTo(model.get("child.name")));
    }

    @Test
    public void arrays_can_be_mapped() throws Exception {
        JsonModel model = JsonModel.model(DOCUMENT);

        List<Book> books1 = model.opsForArray("store.book").toList().of(Book.class);
        List<Book> books2 = model.opsForArray("store.book").toListOf(Book.class);
        Set<Book> books3 = model.opsForArray("store.book").toSetOf(Book.class);

        assertThat(books1, hasSize(4));
        assertThat(books2, hasSize(4));
        assertThat(books3, hasSize(4));
    }

    @Test
    public void objects_can_be_mapped() throws Exception {
        JsonModel model = JsonModel.model(DOCUMENT);

        Book book = model.opsForObject("store.book[1]").to(Book.class);

        assertThat("fiction", equalTo(book.category));
        assertThat("Evelyn Waugh", equalTo(book.author));
        assertThat("Sword of Honour", equalTo(book.title));
        assertThat(12.99D, equalTo(book.price));

    }


    @Test
    public void object_can_be_transformed() throws Exception {

        Transformer transformer = new Transformer<Map<String, Object>>() {
            @Override
            public Map<String, Object> transform(Map<String, Object> model) {
                model.put("newProp", "newProp");
                return model;
            }
        };

        JsonModel model = JsonModel.model(DOCUMENT);

        model.opsForObject("store.book[1]").transform(transformer);

        assertThat("newProp", equalTo(model.get("store.book[1].newProp")));
    }

    @Test
    public void arrays_can_be_transformed() throws Exception {
        Transformer transformer = new Transformer<List<Object>>() {
            @Override
            public Object transform(List<Object> model) {

                for (Object o : model) {
                    Map<String, Object> map = (Map<String, Object>) o;
                    map.put("newProp", "newProp");
                }

                return model;
            }
        };

        JsonModel model = JsonModel.model(DOCUMENT);

        model.opsForArray("store.book").transform(transformer);

        assertThat("newProp", equalTo(model.get("store.book[1].newProp")));
    }

    @Test
    public void array_can_be_transformed_to_primitives() throws Exception {
        Transformer positionTransformer = new Transformer<List<Object>>() {
            private int i = 0;

            @Override
            public Object transform(List<Object> model) {
                List<Object> newList = new ArrayList<Object>();

                for (Object o : model) {
                    newList.add(i++);
                }

                return newList;
            }
        };

        Transformer multiplyingTransformer = new Transformer<List<Object>>() {
            @Override
            public Object transform(List<Object> model) {

                for (int i = 0; i < model.size(); i++) {
                    int curr = (Integer) model.get(i);
                    model.set(i, curr * 2);
                }
                return model;
            }
        };


        JsonModel model = JsonModel.model(DOCUMENT);

        model.opsForArray("store.book").transform(positionTransformer).transform(multiplyingTransformer);

        assertThat(2, equalTo(model.get("store.book[1]")));
    }

    public static class Book {
        public String category;
        public String author;
        public String title;
        public String isbn;
        public Double price;
    }
}
