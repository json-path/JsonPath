package com.jayway.jsonpath.impl.json;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;
import org.codehaus.jettison.json.JSONTokener;

import com.jayway.jsonpath.InvalidJsonException;
import com.jayway.jsonpath.spi.json.JsonArrayWrapper;
import com.jayway.jsonpath.spi.json.JsonObjectWrapper;
import com.jayway.jsonpath.spi.json.JsonWrapperJsonProvider;


public class JettisonJsonProvider extends JsonWrapperJsonProvider<JSONArray,JSONObject> {
    
	public static final String IMPLEMENTATION = "JETTISON";
	
	private static String readStringFromInputStream(InputStream s, String charset) {
		final int bufferSize = 1024;
		final char[] buffer = new char[bufferSize];
		final StringBuilder out = new StringBuilder();
		try {
			Reader in = new InputStreamReader(s, charset);			
			for (; ; ) {
			    int rsz = in.read(buffer, 0, buffer.length);
			    if (rsz < 0) {
			        break;
			    }
			    out.append(buffer, 0, rsz);
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return out.toString();
	}
    @Override
    public Object parse(String json) throws InvalidJsonException {
    	try{
    		Object o = new JSONTokener(json).nextValue();
    		if(JSONArray.class.isAssignableFrom(o.getClass())) {
    			return new JettisonJsonArrayWrapper((JSONArray)o);
    		}
            if(JSONObject.class.isAssignableFrom(o.getClass())) {
    			return new JettisonJsonObjectWrapper((JSONObject)o);
            }
    		return o;
    	}catch(Exception e){
    		throw new InvalidJsonException(e);
    	}
    }

    @Override
    public Object parse(InputStream jsonStream, String charset) throws InvalidJsonException {
    	try{
    		Object o = new JSONTokener(readStringFromInputStream(jsonStream,charset)).nextValue();
    		if(JSONArray.class.isAssignableFrom(o.getClass())) {
    			return new JettisonJsonArrayWrapper((JSONArray)o);
    		}
            if(JSONObject.class.isAssignableFrom(o.getClass())) {
    			return new JettisonJsonObjectWrapper((JSONObject)o);
            }
    		return o;    		  		
    	 }catch (Exception e) {
    	     throw new InvalidJsonException(e);
         }
    }
    
	@Override
	public JsonArrayWrapper<JSONArray> createArrayWrapper(JSONArray array) {
		return new JettisonJsonArrayWrapper(array);
	}

	@Override
	public JsonObjectWrapper<JSONObject> createObjectWrapper(JSONObject object) {
		return new JettisonJsonObjectWrapper(object);
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
