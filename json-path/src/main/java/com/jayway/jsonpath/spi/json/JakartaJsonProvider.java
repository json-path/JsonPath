package com.jayway.jsonpath.spi.json;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import com.jayway.jsonpath.InvalidJsonException;
import com.jayway.jsonpath.JsonPathException;

import jakarta.json.JsonArray;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonException;
import jakarta.json.JsonNumber;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;
import jakarta.json.JsonReader;
import jakarta.json.JsonString;
import jakarta.json.JsonValue;
import jakarta.json.spi.JsonProvider;
import jakarta.json.stream.JsonParsingException;

public class JakartaJsonProvider extends AbstractJsonProvider {

	private static final JsonProvider defaultJsonProvider = JsonProvider.provider();

    @Override
    public Object parse(String json) throws InvalidJsonException {
		Reader jsonInput = new StringReader(json);
		try (JsonReader jsonReader = defaultJsonProvider.createReader(jsonInput)) {
		    return jsonReader.readObject();
		} catch (JsonParsingException e) {
            throw new InvalidJsonException(e);
		}
		// do not catch JsonException as it should never happen
    }

    @Override
    public Object parse(InputStream jsonStream, String charset) throws InvalidJsonException {
    	Reader jsonInput;
    	try {
    		jsonInput = new InputStreamReader(jsonStream, charset);
    	} catch (UnsupportedEncodingException e) {
            throw new JsonPathException(e);
    	}
		try (JsonReader jsonReader = defaultJsonProvider.createReader(jsonInput)) {
		    return jsonReader.readObject();
		} catch (JsonParsingException e) {
            throw new InvalidJsonException(e);
		} catch (JsonException e) {
			throw new JsonPathException(e);
		}
    }

    @Override
    public String toJson(Object obj) {
    	if (obj instanceof JsonObjectBuilder) {
    		obj = ((JsonObjectBuilder) obj).build();
    	} else if (obj instanceof JsonArrayBuilder) {
    		obj = ((JsonArrayBuilder) obj).build();
    	}
        return obj.toString();
    }

    @Override
    public Object createArray() {
    	return defaultJsonProvider.createArrayBuilder();
    }

    @Override
    public Object createMap() {
    	return defaultJsonProvider.createObjectBuilder();
    }

    @Override
    public boolean isArray(Object obj) {
        return (obj instanceof JsonArray || obj instanceof JsonArrayBuilder || obj instanceof List);
    }

    @Override
    public Object getArrayIndex(Object obj, int idx) {
    	if (obj instanceof JsonArrayBuilder) {
    		obj = ((JsonArrayBuilder) obj).build();
    	}
    	if (obj instanceof JsonArray) {
    		return ((JsonArray) obj).get(idx);
    	} else if (obj instanceof List<?>) {
    		return ((List<?>) obj).get(idx);
    	} else {
            throw new UnsupportedOperationException();
    	}
    }

    @SuppressWarnings("unchecked")
	@Override
    public void setArrayIndex(Object array, int index, Object newValue) {
        if (!isArray(array)) {
            throw new UnsupportedOperationException();
        }
    	if (array instanceof JsonArrayBuilder) {
    		array = ((JsonArrayBuilder) array).set(index, createJsonElement(newValue));
    	} else if (array instanceof JsonArray) {
    		// in JSON-P, JsonArray is immutable by definition
            throw new UnsupportedOperationException();
    	} else if (array instanceof List) {
    		// wrap the value in a JSON entity
    		//newValue = createJsonElement(newValue);
    		((List<JsonValue>) array).set(index, createJsonElement(newValue));
    	}
    }

    @Override
    public Object getMapValue(Object obj, String key) {
    	if (obj instanceof JsonObjectBuilder) {
    		obj = ((JsonObjectBuilder) obj).build();
    	}
    	if (obj instanceof JsonObject) {
    		JsonValue o = ((JsonObject) obj).get(key);
            if (o == null) {
                return UNDEFINED;
            } else {
                return unwrap(o);
            }
    	} else {
            throw new UnsupportedOperationException();
    	}
    }

    @Override
    public void setProperty(Object obj, Object key, Object value) {
    	if (obj instanceof JsonObjectBuilder) {
    		((JsonObjectBuilder) obj).add(key.toString(), createJsonElement(value));
    	} else if (obj instanceof JsonObject) {
    		// in JSON-P, JsonObject is immutable by definition
            throw new UnsupportedOperationException();
    	} else if (!isArray(obj)) {
            throw new UnsupportedOperationException();
    	} else if (obj instanceof JsonArray) {
    		// in JSON-P, JsonArray is immutable
            throw new UnsupportedOperationException();
    	}

    	@SuppressWarnings("unchecked")
		List<JsonValue> array = (List<JsonValue>) obj;

    	try {
    		if (key != null) {
    			int index = key instanceof Integer ? (Integer) key : Integer.parseInt(key.toString());
    			array.add(index, createJsonElement(value));
    		} else {
    			array.add(createJsonElement(value));
    		}
    	} catch (NumberFormatException e) {
    		throw new JsonPathException(e);
    	}
    }

    @SuppressWarnings("rawtypes")
	public void removeProperty(Object obj, Object key) {
    	if (obj instanceof JsonObjectBuilder) {
    		((JsonObjectBuilder) obj).remove(key.toString());
    	} else if (obj instanceof JsonObject) {
    		// in JSON-P, JsonObject is immutable by definition
            throw new UnsupportedOperationException();
    	} else if (!isArray(obj)) {
            throw new UnsupportedOperationException();
    	}

    	int index;

    	try {
   			index = key instanceof Integer ? (Integer) key : Integer.parseInt(key.toString());
    	} catch (NumberFormatException e) {
    		throw new JsonPathException(e);
    	}

    	if (obj instanceof JsonArrayBuilder) {
    		((JsonArrayBuilder) obj).remove(index);
    	} else if (obj instanceof List) {
    		((List) obj).remove(index);
    	}
    }

    @Override
    public boolean isMap(Object obj) {
        return (obj instanceof JsonObject || obj instanceof JsonObjectBuilder);
    }

    @Override
    public Collection<String> getPropertyKeys(Object obj) {
    	Set<String> keys;
    	if (obj instanceof JsonObjectBuilder) {
    		keys = ((JsonObjectBuilder) obj).build().keySet();
    	} else if (obj instanceof JsonObject) {
    		keys = ((JsonObject) obj).keySet();
    	} else {
            throw new UnsupportedOperationException("Json object is expected");
    	}
        return new ArrayList<String>(keys);
    }

    @Override
    public int length(Object obj) {
        if (isArray(obj)) {
        	if (obj instanceof JsonArrayBuilder) {
        		return ((JsonArrayBuilder) obj).build().size();
        	} else {
        		return ((List<?>) obj).size();
        	}
        } else if (isMap(obj)) {
        	if (obj instanceof JsonObjectBuilder) {
        		obj = ((JsonObjectBuilder) obj).build();
        	}
            return ((JsonObject) obj).size();
        } else {
            if (obj instanceof CharSequence) {
                return ((CharSequence) obj).length();
            }
        }
        String className = obj != null ? obj.getClass().getName() : null;
        throw new JsonPathException("length operation can not applied to " + className);
    }

    @Override
    public Iterable<?> toIterable(Object obj) {
    	List<Object> values;
    	if (isArray(obj)) {
    		if (obj instanceof JsonArrayBuilder) {
        		obj = ((JsonArrayBuilder) obj).build();
    		}
    		values = new ArrayList<Object>(((List<?>) obj).size());
    		for (Object val : ((List<?>) obj)) {
    			values.add(unwrap(val));
    		}
    	} else if (isMap(obj)) {
    		if (obj instanceof JsonObjectBuilder) {
    			obj = ((JsonObjectBuilder) obj).build();
    		}
    		values = new ArrayList<Object>(((JsonObject) obj).size());
    		for (JsonValue val : ((JsonObject) obj).values()) {
    			values.add(unwrap(val));
    		}
    	} else {
    		throw new UnsupportedOperationException("an array or object instance is expected");
    	}
    	return values;
    }

    @Override
    public Object unwrap(Object obj) {
        if (obj == null) {
            return null;
        }
        if (!(obj instanceof JsonValue)) {
            return obj;
        }

        switch (((JsonValue) obj).getValueType()) {
        case STRING:
        	return ((JsonString) obj).getString();
        case NUMBER:
        	if (((JsonNumber) obj).isIntegral()) {
        		//return ((JsonNumber) obj).bigIntegerValueExact();
        		return ((JsonNumber) obj).longValueExact();
        	} else {
        		//return ((JsonNumber) obj).bigDecimalValue();
        		return ((JsonNumber) obj).doubleValue();
        	}
        case TRUE:
        	return Boolean.TRUE;
        case FALSE:
        	return Boolean.FALSE;
        case NULL:
        	return null;
        default:
        	return obj;
        }
    }

    private JsonValue createJsonElement(Object o) {
    	if (o == null) {
    		return JsonValue.NULL;
    	} else if (o instanceof JsonValue) {
    		return (JsonValue) o;
    	} else if (Boolean.TRUE.equals(o)) {
    		return JsonValue.TRUE;
    	} else if (Boolean.FALSE.equals(o)) {
    		return JsonValue.FALSE;
    	} else if (o instanceof CharSequence) {
    		return defaultJsonProvider.createValue(o.toString());
    	} else if (o instanceof Number) {
    		if ((o instanceof Integer) || (o instanceof Long)) {
    			long v = ((Number) o).longValue();
    			return defaultJsonProvider.createValue(v);
    		} else if ((o instanceof Float) || (o instanceof Double)) {
    			double v = ((Number) o).doubleValue();
    			return defaultJsonProvider.createValue(v);
    		} else if (o instanceof BigInteger) {
    			return defaultJsonProvider.createValue((BigInteger) o);
    		} else if (o instanceof BigDecimal) {
    			return defaultJsonProvider.createValue((BigDecimal) o);
    		} else {
    			// default to BigDecimal conversion for other numeric types
    			BigDecimal v = BigDecimal.valueOf(((Number) o).doubleValue());
    			return defaultJsonProvider.createValue(v);
    		}
    	} else if (o instanceof JsonObjectBuilder) {
    		return ((JsonObjectBuilder) o).build();
    	} else if (o instanceof JsonArrayBuilder) {
    		return ((JsonArrayBuilder) o).build();
    	} else {
    		throw new UnsupportedOperationException();
    	}
    }
}
