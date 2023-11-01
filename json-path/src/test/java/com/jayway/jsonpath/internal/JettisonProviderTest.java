//package com.jayway.jsonpath.internal;
//
//import com.jayway.jsonpath.spi.json.JettisonProvider;
//import org.junit.Before;
//import org.junit.Test;
//
//import static org.junit.Assert.assertEquals;
//import static org.junit.Assert.assertTrue;
//
//public class JettisonProviderTest {
//
//    private JettisonProvider jsonProvider;
//
//    @Before
//    public void setUp() {
//        jsonProvider = new JettisonProvider();
//    }
//
//    @Test
//    public void parseValidJsonString() {
//        //Input json
//        String jsonString = "{\"name\":\"Vishesh\",\"age\":21}";
//
//        // When parsing a valid JSON string
//        Object result = jsonProvider.parse(jsonString);
//
//        assertTrue(jsonProvider.isMap(result));
//
//        // expected values
//        assertEquals("Vishesh", jsonProvider.getMapValue(result, "name"));
//        assertEquals(21, jsonProvider.getMapValue(result, "age"));
//    }
//
//}
