package com.jayway.jsonpath;

import com.jayway.jsonpath.internal.IOUtils;
import com.jayway.jsonpath.spi.HttpProviderFactory;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import static junit.framework.Assert.assertEquals;

/**
 * Created by IntelliJ IDEA.
 * User: kallestenflo
 * Date: 3/10/12
 * Time: 8:12 AM
 */
@Ignore
public class HttpProviderTest {


    private static final String EXPECTED = "{\n" +
            "   \"results\" : [],\n" +
            "   \"status\" : \"REQUEST_DENIED\"\n" +
            "}";
    
    @Test
    public void http_get() throws Exception {

        URL url = new URL("http://maps.googleapis.com/maps/api/geocode/json");

        InputStream inputStream = null;
        try {
            inputStream =  HttpProviderFactory.getProvider().get(url);

            byte[] bytes = org.apache.commons.io.IOUtils.toByteArray(inputStream);
            
            String json = new String(bytes).trim();

            assertEquals(EXPECTED, json);

        } catch (IOException e) {
             IOUtils.closeQuietly(inputStream);
        }

    }

}
