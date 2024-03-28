//package com.jayway.jsonpath.internal;
//
//import com.jayway.jsonpath.spi.json.JsonOrgJsonProvider;
//import org.junit.Before;
//import org.junit.Test;
//
//import java.io.ByteArrayInputStream;
//import java.io.InputStream;
//import java.nio.charset.StandardCharsets;
//
//import static org.junit.Assert.assertEquals;
//import static org.junit.Assert.assertTrue;
//
//public class JsonOrgJsonProviderTest {
//    private JsonOrgJsonProvider jsonProvider;
//    @Before
//    public void setUp() {
//        jsonProvider = new JsonOrgJsonProvider();
//    }
//    @Test
//    public void parseValidJsonString() {
//        //input json
//        String jsonString = "{\"name\":\"Raj\",\"age\":31}";
//
//        // When parsing a valid JSON string
//        Object result = jsonProvider.parse(jsonString);
//
//        assertTrue(jsonProvider.isMap(result));
//        assertEquals("Raj", jsonProvider.getMapValue(result, "name"));
//        assertEquals(31, jsonProvider.getMapValue(result, "age"));
//    }
//    @Test
//    public void parseValidJsonInputStream() {
//
//        String jsonString = "{\"name\":\"Raj\",\"age\":31}";
//        InputStream jsonStream = new ByteArrayInputStream(jsonString.getBytes(StandardCharsets.UTF_8));
//
//        // When parsing valid JSON from an InputStream
//        Object result = jsonProvider.parse(jsonStream, StandardCharsets.UTF_8.name());
//
//        assertTrue(jsonProvider.isMap(result));
//        // expected values
//        assertEquals("Raj", jsonProvider.getMapValue(result, "name"));
//        assertEquals(31, jsonProvider.getMapValue(result, "age"));
//    }
//}
