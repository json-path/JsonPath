package com.jayway.jsonpath.old;

public class JsonPathFilterTest {
    
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

    /*
    
    @Test
    public void arrays_of_maps_can_be_filtered() throws Exception {


        Map<String, Object> rootGrandChild_A = new HashMap<String, Object>();
        rootGrandChild_A.put("name", "rootGrandChild_A");

        Map<String, Object> rootGrandChild_B = new HashMap<String, Object>();
        rootGrandChild_B.put("name", "rootGrandChild_B");

        Map<String, Object> rootGrandChild_C = new HashMap<String, Object>();
        rootGrandChild_C.put("name", "rootGrandChild_C");


        Map<String, Object> rootChild_A = new HashMap<String, Object>();
        rootChild_A.put("name", "rootChild_A");
        rootChild_A.put("children", asList(rootGrandChild_A, rootGrandChild_B, rootGrandChild_C));

        Map<String, Object> rootChild_B = new HashMap<String, Object>();
        rootChild_B.put("name", "rootChild_B");
        rootChild_B.put("children", asList(rootGrandChild_A, rootGrandChild_B, rootGrandChild_C));

        Map<String, Object> rootChild_C = new HashMap<String, Object>();
        rootChild_C.put("name", "rootChild_C");
        rootChild_C.put("children", asList(rootGrandChild_A, rootGrandChild_B, rootGrandChild_C));

        Map<String, Object> root = new HashMap<String, Object>();
        root.put("children", asList(rootChild_A, rootChild_B, rootChild_C));



        Filter customFilter = new Filter.FilterAdapter<Map<String, Object>>() {
            @Override
            public boolean accept(Map<String, Object> map) {
                if(map.getValue("name").equals("rootGrandChild_A")){
                    return true;
                }
                return false;
            }
        };
        
        Filter rootChildFilter = filter(where("name").regex(Pattern.compile("rootChild_[A|B]")));
        Filter rootGrandChildFilter = filter(where("name").regex(Pattern.compile("rootGrandChild_[A|B]")));




        //TODO: breaking v2 solved by [?,?]
        //List read = JsonPath.read(root, "children[?].children[?][?]", rootChildFilter, rootGrandChildFilter, customFilter);
        List read = JsonPath.read(root, "children[?].children[?, ?]", rootChildFilter, rootGrandChildFilter, customFilter);


        System.out.println(read.size());
    }
    
    
    @Test
    public void arrays_of_objects_can_be_filtered() throws Exception {
        Map<String, Object> doc = new HashMap<String, Object>();
        doc.put("items", asList(1, 2, 3));
        
        Filter customFilter = new Filter.FilterAdapter<Integer>(){
            @Override
            public boolean accept(Integer o) {
                return 1 == o;
            }
        };
        
        List<Integer> res = JsonPath.read(doc, "$.items[?]", customFilter);

        assertEquals(1, res.getValue(0).intValue());
    }

    */
}
