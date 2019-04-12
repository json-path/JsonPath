package com.jayway.jsonpath.impl.json;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.jayway.jsonpath.InvalidJsonException;
import com.jayway.jsonpath.JsonPathException;
import com.jayway.jsonpath.spi.json.JsonArrayWrapper;
import com.jayway.jsonpath.spi.json.JsonObjectWrapper;
import com.jayway.jsonpath.spi.json.JsonProvider;
import com.jayway.jsonpath.spi.json.JsonWrapper;
import com.jayway.jsonpath.spi.json.JsonWrapperJsonProvider;


public class GsonJsonProvider extends JsonWrapperJsonProvider<JsonArray,JsonObject> {
    
	public static final String IMPLEMENTATION = "GSON";
	
	public static JsonElement toJsonElement(final Object o, Gson gson) {
		if(o == null || o == JsonProvider.UNDEFINED) {
    		return JsonNull.INSTANCE;
    	}
		if(JsonElement.class.isAssignableFrom(o.getClass())){
			return (JsonElement)o;
		}
    	if(Number.class.isAssignableFrom(o.getClass())) {
    		return new JsonPrimitive((Number)o);
    	}
    	if(Boolean.class.isAssignableFrom(o.getClass())) {
    		return new JsonPrimitive((Boolean)o);
    	}
    	if(o.getClass().isPrimitive()) {
    		if(o.getClass() == boolean.class) {
    			return new JsonPrimitive(new Boolean((Boolean)o));
    		}
    		if(o.getClass() == char.class) {
    			return new JsonPrimitive(new String(new char[] {(Character)o}));
    		}
    		if(o.getClass() == byte.class) {
    			return new JsonPrimitive(new Byte((Byte)o));
    		}
    		if(o.getClass() == short.class) {
    			return new JsonPrimitive(new Short((Short)o));
    		}
    		if(o.getClass() == int.class) {
    			return new JsonPrimitive(new Integer((Integer)o));
    		}
    		if(o.getClass() == long.class) {
    			return new JsonPrimitive(new Long((Long)o));
    		}
    		if(o.getClass() == float.class) {
    			return new JsonPrimitive(new Float((Float)o));
    		}
    		if(o.getClass() == double.class) {
    			return new JsonPrimitive(new Double((Double)o));
    		}
    	} 
    	if(JsonWrapper.class.isAssignableFrom(o.getClass())){
    		return (JsonElement) ((JsonWrapper)o).unwrap();
    	} 
    	if(List.class.isAssignableFrom(o.getClass())) {
    		return gson.toJsonTree(o,List.class);
    	}
    	if(Map.class.isAssignableFrom(o.getClass())) {
    		return gson.toJsonTree(o,Map.class);
    	}
    	return gson.toJsonTree(o);
    }
	
	public static Object fromJsonElement(JsonElement element) {
		if(element == null || element == JsonNull.INSTANCE) {
			return null;
		}
        if(element.isJsonPrimitive()) {
    		JsonPrimitive primitive = element.getAsJsonPrimitive();
    		Object json = null;
         	if(primitive.isNumber()) {
         		String s = primitive.getAsString();
         		try {
	         		char b = s.charAt(0);                
	         		if ((b >= '0' && b <= '9') || b == '.' || b == '-' || b == '+') {
	                    if (b == '0') {
	                    	try {
		                        if (s.length() > 2 && (s.charAt(1) == 'x' || s.charAt(1) == 'X')) {                            
		                                json =  Integer.parseInt(s.substring(2), 16);
		                        } else {
		                                json =  Integer.parseInt(s, 8);
		                        }
	                    	} catch(Exception ignore ){}
	                    }
	                    if(s.indexOf('.') < 0) {
	                    	json = new BigInteger(s);
	                    	if(((BigInteger)json).compareTo(new BigInteger(String.valueOf(Long.MAX_VALUE))) <= 0) {
		                    	Long myLong = new Long(s);
		                        if (myLong.longValue() == myLong.intValue()) {
		                            json = myLong.intValue();
		                        } else {
		                            json = myLong;
		                        }
	                    	}
	                    } else {
		                    json = new BigDecimal(s);
		                    if(((BigDecimal)json).compareTo(new BigDecimal(Double.MAX_VALUE)) <= 0
		                    	&& (((BigDecimal)json).compareTo(new BigDecimal(Double.MIN_VALUE)) >= 0
		                    	||  ((BigDecimal)json).intValue() == 0)) {
			                    json = Double.valueOf(s);
		                    }
	                    }
	                }
         		} catch(Exception e) {
         			json = primitive.getAsNumber();
         		}
         	}
         	if(primitive.isBoolean()) {
         		json = primitive.getAsBoolean();
         	}
         	if(primitive.isString()) {
         		json = primitive.getAsString();
         	}
         	return json;
         } 
         if(element.isJsonArray()) {
        	return new GsonJsonArrayWrapper((JsonArray)element);
         } 
         if(element.isJsonObject()) {
         	return new GsonJsonObjectWrapper((JsonObject)element);
        }
        return null;
	}

    private final JsonParser parser = new JsonParser();
    private final Gson gson;
    
	 /**
     * Initializes the {@code GsonJsonProvider} using the default {@link Gson} object.
     */
    public GsonJsonProvider() {
        this(new Gson());
    }

    /**
     * Initializes the {@code GsonJsonProvider} using a customized {@link Gson} object.
     *
     * @param  gson  the customized Gson object.
     */
    public GsonJsonProvider(final Gson gson) {
        this.gson = gson;
    }

    public Object unwrap(final Object o) {
        if (o == null) {
            return null;
        }
        if (!(o instanceof JsonElement)) {
            return o;
        }
        JsonElement e = (JsonElement) o;
        if (e.isJsonNull()) {
            return null;
        } else if (e.isJsonPrimitive()) {
        	return fromJsonElement(e);
        }
        return o;
    }
	
    @Override
    public Object parse(final String json) throws InvalidJsonException {
        JsonElement element =  parser.parse(json);
        if(element.isJsonPrimitive()) {
    	 	return fromJsonElement(element);
        }
        if(element.isJsonArray()) {
        	return createArrayWrapper((JsonArray)element);
        }
        if(element.isJsonObject()) {
         	return createObjectWrapper((JsonObject)element);
        }
        return element;
    }

    @Override
    public Object parse(final InputStream jsonStream, final String charset) throws InvalidJsonException {
        try {
        	 JsonElement element = parser.parse(new InputStreamReader(jsonStream, charset));
             if(element.isJsonPrimitive()) {
        	 	return fromJsonElement(element);
             }
             if(element.isJsonArray()) {
             	return createArrayWrapper((JsonArray)element);
             }
             if(element.isJsonObject()) {
              	return createObjectWrapper((JsonObject)element);
             }
             return element;
        } catch (UnsupportedEncodingException e) {
            throw new JsonPathException(e);
        }
    }
    
	@Override
	public JsonArrayWrapper<JsonArray> createArrayWrapper(JsonArray array) {
		return new GsonJsonArrayWrapper(array);
	}

	@Override
	public JsonObjectWrapper<JsonObject> createObjectWrapper(JsonObject object) {
		return new GsonJsonObjectWrapper(object);
	}
	
	@Override
	protected boolean instanceOfArray(Class clazz) {
		return clazz == JsonArray.class;
	}

	@Override
	protected boolean instanceOfObject(Class clazz) {
		return clazz == JsonObject.class;
	}

    @Override
	public String getCurrentImplementation() {
		return IMPLEMENTATION;
	}
}
