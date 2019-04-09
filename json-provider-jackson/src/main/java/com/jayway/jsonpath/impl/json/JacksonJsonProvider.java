package com.jayway.jsonpath.impl.json;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.jayway.jsonpath.InvalidJsonException;
import com.jayway.jsonpath.spi.json.JsonArrayWrapper;
import com.jayway.jsonpath.spi.json.JsonObjectWrapper;
import com.jayway.jsonpath.spi.json.JsonWrapperJsonProvider;


public class JacksonJsonProvider extends JsonWrapperJsonProvider<ArrayNode,ObjectNode> {
    
	public static final String IMPLEMENTATION = "JACKSON";
	
	public static Object fromJsonNode(Object o) {
        if (o == null) {
            return null;
        }
        if (!(o instanceof JsonNode)) {
            return o;
        }
        JsonNode e = (JsonNode) o;

        if (e.isValueNode()) {
            if (e.isTextual()) {
                return e.asText();
            } else if (e.isBoolean()) {
                return e.asBoolean();
            } else if (e.isInt()) {
                return e.asInt();
            } else if (e.isLong()) {
                return e.asLong();
            } else if (e.isBigDecimal()) {
                return e.decimalValue();
            } else if (e.isDouble()) {
                return e.doubleValue();
            } else if (e.isFloat()) {
                return e.floatValue();
            } else if (e.isBigDecimal()) {
                return e.decimalValue();
            } else if (e.isNull()) {
                return null;
            }
        }
        return o;
    }
	
	public static JsonNode toJsonNode(Object o,  ObjectMapper objectMapper) {
    	if (o != null) {
    		if (o instanceof JsonNode) {
    			return (JsonNode) o;
    		} else {
    	        return objectMapper.valueToTree(o);
    		}
    	} else {
    		return null;
    	}
    }

    protected ObjectMapper objectMapper;

    /**
     * Initialize the JacksonTreeJsonProvider with the default ObjectMapper and ObjectReader
     */
    public JacksonJsonProvider() {
        this.objectMapper = new ObjectMapper();
    	this.objectMapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
    	this.objectMapper.configure(JsonParser.Feature.ALLOW_BACKSLASH_ESCAPING_ANY_CHARACTER, true);
    }

    @Override
    public Object parse(String json) throws InvalidJsonException {
        try {
            Object o = objectMapper.readTree(json);
    		if(ArrayNode.class.isAssignableFrom(o.getClass())) {
    			return new JacksonJsonArrayWrapper((ArrayNode)o);
    		}
            if(ObjectNode.class.isAssignableFrom(o.getClass())) {
    			return new JacksonJsonObjectWrapper((ObjectNode)o);
            }
    		return JacksonJsonProvider.fromJsonNode(o);
        } catch (IOException e) {
            throw new InvalidJsonException(e, json);
        }
    }

    @Override
    public Object parse(InputStream jsonStream, String charset) throws InvalidJsonException {
        try {
        	Object o = objectMapper.readTree(new InputStreamReader(jsonStream, charset));
    		if(ArrayNode.class.isAssignableFrom(o.getClass())) {
    			return new JacksonJsonArrayWrapper((ArrayNode)o);
    		}
            if(ObjectNode.class.isAssignableFrom(o.getClass())) {
    			return new JacksonJsonObjectWrapper((ObjectNode)o);
            }
    		return JacksonJsonProvider.fromJsonNode(o);
        } catch (IOException e) {
            throw new InvalidJsonException(e);
        }
    }

    
	@Override
	public JsonArrayWrapper<ArrayNode> createArrayWrapper(ArrayNode array) {
		if(array == null) {
			return new JacksonJsonArrayWrapper(JsonNodeFactory.instance.arrayNode());
		}
		return new JacksonJsonArrayWrapper(array);
	}

	@Override
	public JsonObjectWrapper<ObjectNode> createObjectWrapper(ObjectNode object) {
		if(object == null) {
			return new JacksonJsonObjectWrapper(JsonNodeFactory.instance.objectNode());
		}
		return new JacksonJsonObjectWrapper(object);
	}

	@Override
	public Object createArray() {
		return createArrayWrapper(JsonNodeFactory.instance.arrayNode());
	}
	
	@Override
	public Object createMap() {
		return createObjectWrapper(JsonNodeFactory.instance.objectNode());
	}
	
	@Override
	protected boolean instanceOfArray(Class clazz) {
		return clazz == ArrayNode.class;
	}

	@Override
	protected boolean instanceOfObject(Class clazz) {
		return clazz == ObjectNode.class;
	}

	@Override
	public Object unwrap(Object obj) {
		return fromJsonNode(obj);
	}

    @Override
	public String getCurrentImplementation() {
		return IMPLEMENTATION;
	}
}
