//package com.jayway.jsonpath.internal;
//
//import com.jayway.jsonpath.Configuration;
//import com.jayway.jsonpath.TypeRef;
//import com.jayway.jsonpath.spi.mapper.TapestryMappingProvider;
//import org.junit.Before;
//import org.junit.Test;
//
//import java.util.List;
//
//import static org.junit.Assert.assertNull;
//import static org.junit.Assert.fail;
//
//public class TapestryMappingProviderTest {
//    private TapestryMappingProvider mappingProvider;
//    private Configuration configuration;
//    @Before
//    public void setUp() {
//        mappingProvider = new TapestryMappingProvider();
//        configuration = Configuration.defaultConfiguration();
//    }
//    @Test
//    public void mapNullSourceToNullTarget() {
//        Object source = null;
//
//        Object result = mappingProvider.map(source, List.class, configuration);
//
//        //result should be null
//        assertNull(result);
//    }
//    @Test
//    public void mapArrayToJsonRef() {
//        String[] source = new String[]{"Red", "Green", "Blue"};
//
//        try {
//            Object result = mappingProvider.map(source, new TypeRef<List<String>>() {}, configuration);
//            fail("Expected UnsupportedOperationException");
//        } catch (UnsupportedOperationException e) {
//            // UnsupportedOperationException should be thrown
//        }
//    }
//}
