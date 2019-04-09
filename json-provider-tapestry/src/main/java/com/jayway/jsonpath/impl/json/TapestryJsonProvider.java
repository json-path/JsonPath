package com.jayway.jsonpath.impl.json;

import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.tapestry5.json.JSONArray;
import org.apache.tapestry5.json.JSONObject;
import org.apache.tapestry5.json.TapestryJsonTokener;

import com.jayway.jsonpath.InvalidJsonException;
import com.jayway.jsonpath.spi.json.JsonArrayWrapper;
import com.jayway.jsonpath.spi.json.JsonObjectWrapper;
import com.jayway.jsonpath.spi.json.JsonWrapperJsonProvider;


public class TapestryJsonProvider extends JsonWrapperJsonProvider<JSONArray,JSONObject> {
    
	public static final String IMPLEMENTATION = "TAPESTRY";
	
    @Override
    public Object parse(String json) throws InvalidJsonException {
    	try{
    		Object o = new TapestryJsonTokener(json).nextValue();
    		if(JSONArray.class.isAssignableFrom(o.getClass())) {
    			return new TapestryJsonArrayWrapper((JSONArray)o);
    		}
            if(JSONObject.class.isAssignableFrom(o.getClass())) {
    			return new TapestryJsonObjectWrapper((JSONObject)o);
            }
    		return o;
    	}catch(Exception e){
    		throw new InvalidJsonException(e);
    	}
    }

    @Override
    public Object parse(InputStream jsonStream, String charset) throws InvalidJsonException {
    	try{
    		Object o = new TapestryJsonTokener(new InputStreamReader(jsonStream,charset)).nextValue();
    		if(JSONArray.class.isAssignableFrom(o.getClass())) {
    			return new TapestryJsonArrayWrapper((JSONArray)o);
    		}
            if(JSONObject.class.isAssignableFrom(o.getClass())) {
    			return new TapestryJsonObjectWrapper((JSONObject)o);
            }
    		return o;    		  		
    	 }catch (Exception e) {
    	     throw new InvalidJsonException(e);
         }
    }
    
	@Override
	public JsonArrayWrapper<JSONArray> createArrayWrapper(JSONArray array) {
		return new TapestryJsonArrayWrapper(array);
	}

	@Override
	public JsonObjectWrapper<JSONObject> createObjectWrapper(JSONObject object) {
		return new TapestryJsonObjectWrapper(object);
	}
	
	@Override
	protected boolean instanceOfArray(Class clazz) {
		return clazz == JSONArray.class;
	}

	@Override
	protected boolean instanceOfObject(Class clazz) {
		return clazz == JSONObject.class;
	}

	@Override
	public Object unwrap(Object obj) {
		if(JSONObject.NULL.equals(obj)) {
			return null;
		}
		return obj;
	}

    @Override
	public String getCurrentImplementation() {
		return IMPLEMENTATION;
	}
}
