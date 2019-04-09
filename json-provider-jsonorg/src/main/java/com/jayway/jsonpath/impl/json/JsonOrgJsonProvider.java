package com.jayway.jsonpath.impl.json;

import java.io.InputStream;
import java.io.InputStreamReader;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.jayway.jsonpath.InvalidJsonException;
import com.jayway.jsonpath.spi.json.JsonArrayWrapper;
import com.jayway.jsonpath.spi.json.JsonObjectWrapper;
import com.jayway.jsonpath.spi.json.JsonWrapperJsonProvider;


public class JsonOrgJsonProvider extends JsonWrapperJsonProvider<JSONArray,JSONObject> {
    
	public static final String IMPLEMENTATION = "ORG";
	
    @Override
    public Object parse(String json) throws InvalidJsonException {
    	try{
    		Object o = new JSONTokener(json).nextValue();
    		if(JSONArray.class.isAssignableFrom(o.getClass())) {
    			return new JsonOrgJsonArrayWrapper((JSONArray)o);
    		}
            if(JSONObject.class.isAssignableFrom(o.getClass())) {
    			return new JsonOrgJsonObjectWrapper((JSONObject)o);
            }
    		return o;
    	}catch(Exception e){
    		throw new InvalidJsonException(e);
    	}
    }

    @Override
    public Object parse(InputStream jsonStream, String charset) throws InvalidJsonException {
    	try{
    		Object o = new JSONTokener(new InputStreamReader(jsonStream,charset)).nextValue();
    		if(JSONArray.class.isAssignableFrom(o.getClass())) {
    			return new JsonOrgJsonArrayWrapper((JSONArray)o);
    		}
            if(JSONObject.class.isAssignableFrom(o.getClass())) {
    			return new JsonOrgJsonObjectWrapper((JSONObject)o);
            }
    		return o;    		  		
    	 }catch (Exception e) {
    	     throw new InvalidJsonException(e);
         }
    }
    
	@Override
	public JsonArrayWrapper<JSONArray> createArrayWrapper(JSONArray array) {
		return new JsonOrgJsonArrayWrapper(array);
	}

	@Override
	public JsonObjectWrapper<JSONObject> createObjectWrapper(JSONObject object) {
		return new JsonOrgJsonObjectWrapper(object);
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
