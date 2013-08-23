package com.jayway.jsonpath;

import junit.framework.Assert;

import com.jayway.jsonpath.spi.JsonProvider;
import com.jayway.jsonpath.spi.JsonProviderFactory;

import net.minidev.json.JSONArray;

import org.junit.Test;


/**
 * Created by IntelliJ IDEA.
 * User: kallestenflo
 * Date: 11/8/11
 * Time: 10:40 PM
 */
public class JsonProviderFactoryTest {

    public final static String ARRAY = "[{\"value\": 1},{\"value\": 2}, {\"value\": 3},{\"value\": 4}]";

    @Test
    public void exchange_provider_factory() throws Exception {

        JsonModel m = JsonModel.create(ARRAY);
        Object o = m.getJsonObject();
        System.out.println("Orginal thread: " + o);
        Assert.assertEquals(JSONArray.class, o.getClass());
        
        JsonProviderFactory.setProviderFactory(new JsonProviderFactory() {
          
          @Override
          public JsonProvider createProvider() {
            throw new IllegalStateException();
          }
        });

        try {
          JsonModel.create(ARRAY);
          Assert.fail("The current factory should have thrown an exception");
        } catch(IllegalStateException e){
          
        }

        Thread t = new Thread(new Runnable() {
          
          @Override
          public void run() {
            JsonModel m = JsonModel.create(ARRAY);
            Object o = m.getJsonObject();
            System.out.println("Second thread: " + o);
            Assert.assertEquals(JSONArray.class, o.getClass());
          }
        });
        t.start();
        t.join();
        
        JsonProviderFactory.reset();
        
        JsonModel.create(ARRAY);
        
    }
}
