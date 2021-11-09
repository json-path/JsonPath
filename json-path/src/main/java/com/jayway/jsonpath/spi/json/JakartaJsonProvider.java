package com.jayway.jsonpath.spi.json;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

import com.jayway.jsonpath.InvalidJsonException;
import com.jayway.jsonpath.JsonPathException;

import jakarta.json.JsonArray;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonBuilderFactory;
import jakarta.json.JsonException;
import jakarta.json.JsonNumber;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;
import jakarta.json.JsonReader;
import jakarta.json.JsonString;
import jakarta.json.JsonStructure;
import jakarta.json.JsonValue;
import jakarta.json.spi.JsonProvider;
import jakarta.json.stream.JsonParsingException;

public class JakartaJsonProvider extends AbstractJsonProvider {

    private static final JsonProvider defaultJsonProvider = JsonProvider.provider();
    private static final JsonBuilderFactory jsonBuilderFactory = defaultJsonProvider.createBuilderFactory(null);

    private final boolean mutableJson;

    /**
     * Constructs new instance of parsing and serialization adapter for Jakarta EE 9
     * JSON-P default provider. JSON files, strings, and streams can be loaded, parsed,
     * and navigated with JsonPath expressions, and values retrieved - but no changes
     * to the loaded JSON document are permitted, and will yield exceptions.
     */
    public JakartaJsonProvider() {
    	this.mutableJson = false;
    }

    /**
     * Constructs new instance of parsing and serialization adapter for Jakarta EE 9
     * JSON-P default provider, and optionally enables proxying of {@code JsonObject}
     * and {@code JsonArray} entities to implement mutable JSON structures. By default,
     * all structures and values produced and consumed by JSON-P are immutable. This
     * comes at an extra cost to perfomance and memory consumption, so enable only if
     * expected use cases include add/put/replace/delete operations on JSON document. 
     * 
     * @param mutableJson enable dynamic proxies for JSON structures
     */
    public JakartaJsonProvider(boolean mutableJson) {
    	this.mutableJson = mutableJson;
    }

    @Override
    public Object parse(String json) throws InvalidJsonException {
        Reader jsonInput = new StringReader(json);
        try (JsonReader jsonReader = defaultJsonProvider.createReader(jsonInput)) {
        	JsonStructure jsonStruct = jsonReader.read();
        	return mutableJson ? proxyAll(jsonStruct) : jsonStruct;
        } catch (JsonParsingException e) {
            throw new InvalidJsonException(e);
        }
        // not catching a JsonException as it never happens here
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
        	JsonStructure jsonStruct = jsonReader.read();
        	return mutableJson ? proxyAll(jsonStruct) : jsonStruct;
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
        } else if (obj instanceof List) {
            obj = jsonBuilderFactory.createArrayBuilder((Collection<?>) obj).build();
        }
        return obj.toString();
    }

    @Override
    public Object createArray() {
    	if (mutableJson) {
    		return new JsonArrayProxy(jsonBuilderFactory.createArrayBuilder().build());
    	} else {
    		return new LinkedList<Object>();
    	}
    }

    @Override
    public Object createMap() {
    	if (mutableJson) {
    		return new JsonObjectProxy(jsonBuilderFactory.createObjectBuilder().build());
    	} else {
    		return jsonBuilderFactory.createObjectBuilder();
    	}
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
        	return super.getArrayIndex(obj, idx);
        } else {
            throw new UnsupportedOperationException();
        }
    }

    @Override
    public void setArrayIndex(Object array, int index, Object newValue) {
        if (array instanceof JsonArrayBuilder) {
            // next line is not optimal, but ArrayBuilder has no size() method
            if (index == ((JsonArrayBuilder) array).build().size()) {
                array = ((JsonArrayBuilder) array).add(wrap(newValue));
            } else {
                array = ((JsonArrayBuilder) array).set(index, wrap(newValue));
            }
        } else if (array instanceof JsonArray) {
        	if (mutableJson && array instanceof JsonArrayProxy) {
        		((JsonArrayProxy) array).set(index, wrap(newValue));
        	} else {
        		throw new UnsupportedOperationException("JsonArray is immutable in JSON-P");
        	}
        } else {
            super.setArrayIndex(array, index, wrap(newValue));
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
            ((JsonObjectBuilder) obj).add(key.toString(), wrap(value));
        } else if (mutableJson && obj instanceof JsonObjectProxy) {
    		((JsonObjectProxy) obj).put(key.toString(), wrap(value));
    	} else if (obj instanceof JsonObject) {
    		throw new UnsupportedOperationException("JsonObject is immutable in JSON-P");
    	} else if (obj instanceof JsonArrayBuilder) {
    		if (key == null) {
    			((JsonArrayBuilder) obj).add(wrap(value));
    		} else {
				((JsonArrayBuilder) obj).set(toArrayIndex(key), wrap(value));
    		}
        } else if (mutableJson && obj instanceof JsonArrayProxy) {
        	if (key == null) {
    			((JsonArrayProxy) obj).add(wrap(value));
        	} else {
        		((JsonArrayProxy) obj).set(toArrayIndex(key), wrap(value));
    		}
    	} else if (obj instanceof JsonArray) {
    		throw new UnsupportedOperationException("JsonArray is immutable in JSON-P");
        } else if (obj instanceof List) {
        	@SuppressWarnings("unchecked")
        	List<JsonValue> array = (List<JsonValue>) obj;
        	if (key == null) {
        		array.add(wrap(value));
        	} else {
        		array.add(toArrayIndex(key), wrap(value));
        	}
        } else {
            throw new UnsupportedOperationException();
        }
    }

    @SuppressWarnings("rawtypes")
    public void removeProperty(Object obj, Object key) {
        if (obj instanceof JsonObjectBuilder) {
            ((JsonObjectBuilder) obj).remove(key.toString());
        } else if (obj instanceof JsonObject) {
        	if (mutableJson && obj instanceof JsonObjectProxy) {
        		((JsonObjectProxy) obj).remove(key);
        	} else {
        		throw new UnsupportedOperationException("JsonObject is immutable in JSON-P");
        	}
        } else if (isArray(obj)) {
            int index = toArrayIndex(key).intValue();
            if (obj instanceof JsonArrayBuilder) {
                ((JsonArrayBuilder) obj).remove(index);
            } else if (obj instanceof List) {
            	// this also covers JsonArray as it implements List<>
                ((List) obj).remove(index);
            }
        } else {
            throw new UnsupportedOperationException();
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
        case ARRAY:
        	if (mutableJson && obj instanceof JsonArrayProxy) {
        		return (JsonArray) obj;
        	} else {
        		return ((JsonArray) obj).getValuesAs((JsonValue v) -> unwrap(v));
        	}
        case STRING:
            return ((JsonString) obj).getString();
        case NUMBER:
            if (((JsonNumber) obj).isIntegral()) {
                //return ((JsonNumber) obj).bigIntegerValueExact();
                try {
                    return ((JsonNumber) obj).intValueExact();
                } catch (ArithmeticException e) {
                    return ((JsonNumber) obj).longValueExact();
                }
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

    private Integer toArrayIndex(Object index) {
        try {
        	if (index instanceof Integer) {
        		return (Integer) index;
        	} else if (index instanceof Long) {
        		return Integer.valueOf(((Long) index).intValue());
        	} else if (index != null) {
        		return Integer.valueOf(index.toString());
        	} else {
        		//return null;
				throw new IllegalArgumentException("Invalid array index");
            }
        } catch (NumberFormatException e) {
            throw new JsonPathException(e);
        }
    }

    private JsonValue wrap(Object obj) {
        if (obj == null) {
            return JsonValue.NULL;
        } else if (obj instanceof JsonArray) {
        	if (!mutableJson || obj instanceof JsonArrayProxy) {
        		return (JsonArray) obj;
        	} else {
        		return proxyAll((JsonArray) obj);
        	}
        } else if (obj instanceof JsonObject) {
        	if (!mutableJson || obj instanceof JsonObjectProxy) {
        		return (JsonObject) obj;
        	} else {
        		return proxyAll((JsonObject) obj);
        	}
        } else if (obj instanceof JsonValue) {
            return (JsonValue) obj;
        } else if (Boolean.TRUE.equals(obj)) {
            return JsonValue.TRUE;
        } else if (Boolean.FALSE.equals(obj)) {
            return JsonValue.FALSE;
        } else if (obj instanceof CharSequence) {
            return defaultJsonProvider.createValue(obj.toString());
        } else if (obj instanceof Number) {
            if (obj instanceof Integer) {
                int v = ((Number) obj).intValue();
                return defaultJsonProvider.createValue(v);
            } else if (obj instanceof Long) {
            	long v = ((Number) obj).longValue();
            	return defaultJsonProvider.createValue(v);
            } else if ((obj instanceof Float) || (obj instanceof Double)) {
                double v = ((Number) obj).doubleValue();
                return defaultJsonProvider.createValue(v);
            } else if (obj instanceof BigInteger) {
                return defaultJsonProvider.createValue((BigInteger) obj);
            } else if (obj instanceof BigDecimal) {
                return defaultJsonProvider.createValue((BigDecimal) obj);
            } else {
                // default to BigDecimal conversion for other numeric types
                BigDecimal v = BigDecimal.valueOf(((Number) obj).doubleValue());
                return defaultJsonProvider.createValue(v);
            }
        } else if (obj instanceof Collection) {
    		JsonArray result = jsonBuilderFactory.createArrayBuilder((Collection<?>) obj).build();
    		return mutableJson ? proxyAll(result) : result;
        } else if (obj instanceof Map) {
    		@SuppressWarnings("unchecked")
    		Map<String, Object> map = (Map<String, Object>) obj;
    		JsonObject result = jsonBuilderFactory.createObjectBuilder(map).build();
    		return mutableJson ? proxyAll(result) : result;
        } else if (obj instanceof JsonArrayBuilder) {
        	JsonArray result = ((JsonArrayBuilder) obj).build();
    		return mutableJson ? proxyAll(result) : result;
        } else if (obj instanceof JsonObjectBuilder) {
        	JsonObject result = ((JsonObjectBuilder) obj).build();
    		return mutableJson ? proxyAll(result) : result;
        } else {
            String className = obj.getClass().getSimpleName();
            throw new UnsupportedOperationException("Cannot create JSON element from " + className);
        }
    }

    private JsonStructure proxyAll(JsonStructure jsonStruct) {
    	if (jsonStruct == null) {
    		return null;
    	} else if (jsonStruct instanceof JsonArrayProxy) {
    		return (JsonArray) jsonStruct;
    	} else if (jsonStruct instanceof JsonArray) {
    		List<Object> array = new ArrayList<>();
    		for (JsonValue v : (JsonArray) jsonStruct) {
    			if (v instanceof JsonStructure) {
    				v = proxyAll((JsonStructure) v);
    			}
    			array.add(v);
    		}
    		return new JsonArrayProxy(jsonBuilderFactory.createArrayBuilder(array).build());
    	} else if (jsonStruct instanceof JsonObjectProxy) {
    		return (JsonObject) jsonStruct;
    	} else if (jsonStruct instanceof JsonObject) {
    		Map<String, Object> map = new LinkedHashMap<>();
    		for (Map.Entry<String, JsonValue> e : ((JsonObject) jsonStruct).entrySet()) {
    			JsonValue v = e.getValue();
    			if (v instanceof JsonStructure) {
    				v = proxyAll((JsonStructure) v);
    			}
    			map.put(e.getKey(), v);
    		}
    		return new JsonObjectProxy(jsonBuilderFactory.createObjectBuilder(map).build());
    	} else {
    		throw new IllegalArgumentException();
    	}
    }

    private static class JsonArrayProxy implements JsonArray {

    	private JsonArray arr;

    	JsonArrayProxy(JsonArray arr) {
    		this.arr = arr;
    	}

        @Override
        public JsonObject getJsonObject(int index) {
        	return arr.getJsonObject(index);
        }

        @Override
        public JsonArray getJsonArray(int index) {
        	return arr.getJsonArray(index);
        }

        @Override
        public JsonNumber getJsonNumber(int index) {
        	return arr.getJsonNumber(index);
        }

        @Override
        public JsonString getJsonString(int index) {
        	return arr.getJsonString(index);
        }

		@Override
		public <T extends JsonValue> List<T> getValuesAs(Class<T> clazz) {
        	return arr.getValuesAs(clazz);
		}

		@Override
		public String getString(int index) {
        	return arr.getString(index);
		}

		@Override
		public String getString(int index, String defaultValue) {
        	return arr.getString(index, defaultValue);
		}

		@Override
		public int getInt(int index) {
        	return arr.getInt(index);
		}

		@Override
		public int getInt(int index, int defaultValue) {
        	return arr.getInt(index, defaultValue);
		}

		@Override
		public boolean getBoolean(int index) {
        	return arr.getBoolean(index);
		}

		@Override
		public boolean getBoolean(int index, boolean defaultValue) {
        	return arr.getBoolean(index, defaultValue);
		}

		@Override
		public boolean isNull(int index) {
        	return arr.isNull(index);
		}

		@Override
		public ValueType getValueType() {
			return arr.getValueType();
		}

		@Override
		public int size() {
			return arr.size();
		}

		@Override
		public boolean isEmpty() {
			return arr.isEmpty();
		}

		@Override
		public boolean contains(Object o) {
			return arr.contains(o);
		}

		@Override
		public Iterator<JsonValue> iterator() {
			return new Iterator<JsonValue>() {

				final JsonArray refArr = arr;
				final Iterator<JsonValue> it = arr.iterator();

				@Override
				public boolean hasNext() {
					if (refArr == arr) {
						return it.hasNext();
					} else {
						throw new ConcurrentModificationException();
					}
				}

				@Override
				public JsonValue next() {
					if (refArr == arr) {
						return it.next();
					} else {
						throw new ConcurrentModificationException();
					}
				}
			};
		}

		@Override
		public Object[] toArray() {
			return arr.toArray();
		}

		@Override
		public <T> T[] toArray(T[] a) {
			return arr.toArray(a);
		}

		@Override
		public boolean add(JsonValue e) {
			arr = jsonBuilderFactory.createArrayBuilder(arr).add(e).build();
			return true;
		}

		@Override
		public boolean remove(Object o) {
			int i = arr.indexOf(o);
			if (i != -1) {
				arr = jsonBuilderFactory.createArrayBuilder(arr).remove(i).build();
				return true;
			} else {
				return false;
			}
		}

		@Override
		public boolean containsAll(Collection<?> c) {
			return arr.containsAll(c);
		}

		@Override
		public boolean addAll(Collection<? extends JsonValue> c) {
			if (!c.isEmpty()) {
				JsonArrayBuilder builder = jsonBuilderFactory.createArrayBuilder(arr);
				for (JsonValue v : c) {
					builder.add(v);
				}
				arr = builder.build();
				return true;
			} else { 
				return false;
			}
		}

		@Override
		public boolean addAll(int index, Collection<? extends JsonValue> c) {
			if (c.isEmpty()) {
				return false;
			}
			if (index < 0 || index >= arr.size()) {
				throw new IndexOutOfBoundsException();
			}
			JsonArrayBuilder builder = jsonBuilderFactory.createArrayBuilder(arr);
			for (int i = 0; i < arr.size(); i++) {
				if (index == i) {
					for (JsonValue v : c) {
						builder.add(v);
					}
				}
				builder.add(arr.get(i));
			}
			arr = builder.build();
			return true;
		}

		@Override
		public boolean removeAll(Collection<?> c) {
			if (c.isEmpty()) {
				return false;
			}
			JsonArrayBuilder builder = null;
			for (int i = 0, j = 0; i < arr.size(); i++, j++) {
				if (c.contains(arr.get(i))) {
					if (builder == null) {
						builder = jsonBuilderFactory.createArrayBuilder(arr);
					}
					builder.remove(j--);
				}
			}
			if (builder != null) {
				arr = builder.build();
				return true;
			} else {
				return false;
			}
		}

		@Override
		public boolean retainAll(Collection<?> c) {
			if (c.isEmpty()) {
				arr = jsonBuilderFactory.createArrayBuilder().build();
				return true;
			}
			JsonArrayBuilder builder = null;
			for (int i = 0, j = 0; i < arr.size(); i++, j++) {
				if (!c.contains(arr.get(i))) {
					if (builder == null) {
						builder = jsonBuilderFactory.createArrayBuilder(arr);
					}
					builder.remove(j--);
				}
			}
			if (builder != null) {
				arr = builder.build();
				return true;
			} else {
				return false;
			}
		}

		@Override
		public void clear() {
			arr = jsonBuilderFactory.createArrayBuilder().build();
		}

		@Override
		public JsonValue get(int index) {
			return arr.get(index);
		}

		@Override
		public JsonValue set(int index, JsonValue element) {
			if (index == arr.size()) {
				arr = jsonBuilderFactory.createArrayBuilder(arr).add(index, element).build();
				return null;
			} else {
				JsonValue oldValue = arr.get(index);
				arr = jsonBuilderFactory.createArrayBuilder(arr).set(index, element).build();
				return oldValue;
			}
		}

		@Override
		public void add(int index, JsonValue element) {
			arr = jsonBuilderFactory.createArrayBuilder(arr).add(index, element).build();
		}

		@Override
		public JsonValue remove(int index) {
			JsonValue oldValue = arr.get(index);
			arr = jsonBuilderFactory.createArrayBuilder(arr).remove(index).build();
			return oldValue;
		}

		@Override
		public int indexOf(Object o) {
			return arr.indexOf(o);
		}

		@Override
		public int lastIndexOf(Object o) {
			return arr.lastIndexOf(o);
		}

		@Override
		public ListIterator<JsonValue> listIterator() {
			return listIterator(0);
		}

		@Override
		public ListIterator<JsonValue> listIterator(int index) {
			return new ListIterator<JsonValue>() {

				final JsonArray refArr = arr;
				final ListIterator<JsonValue> it = arr.listIterator(index);

				@Override
				public boolean hasNext() {
					if (refArr == arr) {
						return it.hasNext();
					} else {
						throw new ConcurrentModificationException();
					}
				}

				@Override
				public JsonValue next() {
					if (refArr == arr) {
						return it.next();
					} else {
						throw new ConcurrentModificationException();
					}
				}

				@Override
				public boolean hasPrevious() {
					if (refArr == arr) {
						return it.hasPrevious();
					} else {
						throw new ConcurrentModificationException();
					}
				}

				@Override
				public JsonValue previous() {
					if (refArr == arr) {
						return it.previous();
					} else {
						throw new ConcurrentModificationException();
					}
				}

				@Override
				public int nextIndex() {
					if (refArr == arr) {
						return it.nextIndex();
					} else {
						throw new ConcurrentModificationException();
					}
				}

				@Override
				public int previousIndex() {
					if (refArr == arr) {
						return it.previousIndex();
					} else {
						throw new ConcurrentModificationException();
					}
				}

				@Override
				public void remove() {
					it.remove(); // will throw exception
				}

				@Override
				public void set(JsonValue e) {
					it.set(e); // will throw exception
				}

				@Override
				public void add(JsonValue e) {
					it.add(e); // will throw exception
				}
			};
		}

		@Override
		public List<JsonValue> subList(int fromIndex, int toIndex) {
			return arr.subList(fromIndex, toIndex);
		}

		@Override
		public int hashCode() {
			return arr.hashCode();
		}

		@Override
	    public boolean equals(Object obj) {
			return arr.equals(obj);
	    }

		@Override
	    public String toString() {
			return arr.toString();
	    }
    }

    private static class JsonObjectProxy implements JsonObject {

    	private JsonObject obj;

    	JsonObjectProxy(JsonObject obj) {
    		this.obj = obj;
    	}

		@Override
		public JsonArray getJsonArray(String name) {
			return obj.getJsonArray(name);
		}

		@Override
		public JsonObject getJsonObject(String name) {
			return obj.getJsonObject(name);
		}

		@Override
		public JsonNumber getJsonNumber(String name) {
			return obj.getJsonNumber(name);
		}

		@Override
		public JsonString getJsonString(String name) {
			return obj.getJsonString(name);
		}

		@Override
		public String getString(String name) {
			return obj.getString(name);
		}

		@Override
		public String getString(String name, String defaultValue) {
			return obj.getString(name, defaultValue);
		}

		@Override
		public int getInt(String name) {
			return obj.getInt(name);
		}

		@Override
		public int getInt(String name, int defaultValue) {
			return obj.getInt(name, defaultValue);
		}

		@Override
		public boolean getBoolean(String name) {
			return obj.getBoolean(name);
		}

		@Override
		public boolean getBoolean(String name, boolean defaultValue) {
			return obj.getBoolean(name, defaultValue);
		}

		@Override
		public boolean isNull(String name) {
			return obj.isNull(name);
		}

		@Override
		public ValueType getValueType() {
			return obj.getValueType();
		}

		@Override
		public int size() {
			return obj.size();
		}

		@Override
		public boolean isEmpty() {
			return obj.isEmpty();
		}

		@Override
		public boolean containsKey(Object key) {
			return obj.containsKey(key);
		}

		@Override
		public boolean containsValue(Object value) {
			return obj.containsValue(value);
		}

		@Override
		public JsonValue get(Object key) {
			return obj.get(key);
		}

		@Override
		public JsonValue put(String key, JsonValue value) {
			JsonValue oldValue = obj.get(key);
			obj = jsonBuilderFactory.createObjectBuilder(obj).add(key, value).build();
			return oldValue;
		}

		@Override
		public JsonValue remove(Object key) {
			JsonValue oldValue = obj.get(key);
			if (oldValue != null) {
				obj = jsonBuilderFactory.createObjectBuilder(obj).remove(key.toString()).build();
				return oldValue;
			} else {
				return null;
			}
		}

		@Override
		public void putAll(Map<? extends String, ? extends JsonValue> m) {
			if (m.isEmpty()) {
				return;
			}
			JsonObjectBuilder builder = jsonBuilderFactory.createObjectBuilder(obj);
			for (Map.Entry<? extends String, ? extends JsonValue> e : m.entrySet()) {
				builder.add(e.getKey(), e.getValue());
			}
			obj = builder.build();
		}

		@Override
		public void clear() {
			obj = jsonBuilderFactory.createObjectBuilder().build();
		}

		@Override
		public Set<String> keySet() {
			return obj.keySet();
		}

		@Override
		public Collection<JsonValue> values() {
			return obj.values();
		}

		@Override
		public Set<Map.Entry<String, JsonValue>> entrySet() {
			return new AbstractSet<Map.Entry<String,JsonValue>>() {

				final JsonObject refObj = obj;

				@Override
				public Iterator<Map.Entry<String, JsonValue>> iterator() {
					if (refObj != obj) {
						throw new ConcurrentModificationException();
					}
					return new Iterator<Map.Entry<String, JsonValue>>() {

						final Iterator<Map.Entry<String, JsonValue>> it = obj.entrySet().iterator();

						@Override
						public boolean hasNext() {
							if (refObj == obj) {
								return it.hasNext();
							} else {
								throw new ConcurrentModificationException();
							}
						}

						@Override
						public Map.Entry<String, JsonValue> next() {
							if (refObj == obj) {
								return it.next();
							} else {
								throw new ConcurrentModificationException();
							}
						}
					};
				}

				@Override
				public int size() {
					if (refObj == obj) {
						return obj.size();
					} else {
						throw new ConcurrentModificationException();
					}
				}
			};
		}

		@Override
		public int hashCode() {
			return obj.hashCode();
		}

		@Override
	    public boolean equals(Object obj) {
			return obj.equals(obj);
	    }

		@Override
	    public String toString() {
			return obj.toString();
	    }
    }
}
