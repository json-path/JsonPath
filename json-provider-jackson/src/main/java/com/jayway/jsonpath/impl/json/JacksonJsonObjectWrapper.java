package com.jayway.jsonpath.impl.json;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.jayway.jsonpath.spi.json.JsonObjectWrapper;
import com.jayway.jsonpath.spi.json.JsonProvider;

public class JacksonJsonObjectWrapper extends JsonObjectWrapper<ObjectNode> {

	private ObjectMapper objectMapper;
	
	public JacksonJsonObjectWrapper(ObjectNode instance) {
		super(instance, ObjectNode.class, ArrayNode.class, JacksonJsonObjectWrapper.class, JacksonJsonArrayWrapper.class );
		this.objectMapper = new ObjectMapper();
    	this.objectMapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
    	this.objectMapper.configure(JsonParser.Feature.ALLOW_BACKSLASH_ESCAPING_ANY_CHARACTER, true);
	}

	@Override
	public int size() {
		return super.instance.size();
	}

	@Override
	public Iterator iterator() {
		return new Iterator() {

			Iterator iterator = entrySet().iterator();
			
			@Override
			public boolean hasNext() {
				return iterator.hasNext();
			}

			@Override
			public Object next() {
				final Entry entry = (Entry) iterator.next();						
				return new Entry() {

					@Override
					public Object getKey() {
						return entry.getKey();
					}

					@Override
					public Object getValue() {
						return JacksonJsonProvider.fromJsonNode(entry.getValue()); 
					}

					@Override
					public Object setValue(Object value) {
						entry.setValue(JacksonJsonProvider.toJsonNode(value, objectMapper));
						return null;
					}
					
				};
			}
			
		};
	}

	@Override
	public Object get(Object key) {
		if(!super.instance.has(key.toString())){
			return JsonProvider.UNDEFINED;
		}		
		Object o = this.instance.get(key.toString());
		return JacksonJsonProvider.fromJsonNode(o);
	}

	@Override
	public Object remove(Object key) {
		Object o = super.instance.remove(key.toString());
		return JacksonJsonProvider.fromJsonNode(o);
	}
	
	@Override
	public Set keySet() {		
		Set keys = new HashSet();
		for(Iterator iterator = super.instance.fieldNames();iterator.hasNext();) {
			keys.add(iterator.next());
		}
		return keys;
	}

	@Override
	public Collection values() {
		return toMap().values();
	}

	@Override
	public Set entrySet() {
		return toMap().entrySet();
	}
	
	private Map toMap() {
		Map entryMap = new HashMap();
		for(Iterator iterator = super.instance.fieldNames();iterator.hasNext();) {
			String key = (String) iterator.next();
			entryMap.put(key, super.instance.get(key));
		}
		return entryMap;
	}

	@Override
	protected Object doPut(Object key, Object value) {
		Object o = this.instance.get(key.toString());
		setValueInObjectNode(key, value);
		return JacksonJsonProvider.fromJsonNode(o);
	}
	
	private void setValueInObjectNode(Object key, Object value) {
    	if (value instanceof JsonNode) {
            super.instance.set(key.toString(), (JsonNode) value);
    	} else if (value instanceof String) {
    		super.instance.put(key.toString(), (String) value);
    	} else if (value instanceof Integer) {
    		super.instance.put(key.toString(), (Integer) value);
    	} else if (value instanceof Long) {
    		super.instance.put(key.toString(), (Long) value);
    	} else if (value instanceof Short) {
    		super.instance.put(key.toString(), (Short) value);
    	} else if (value instanceof Double) {
    		super.instance.put(key.toString(), (Double) value);
    	} else if (value instanceof Float) {
    		super.instance.put(key.toString(), (Float) value);
    	} else if (value instanceof BigDecimal) {
    		super.instance.put(key.toString(), (BigDecimal) value);
    	} else if (value instanceof Boolean) {
    		super.instance.put(key.toString(), (Boolean) value);
    	} else if (value instanceof byte[]) {
    		super.instance.put(key.toString(), (byte[]) value);
    	} else if (value == null) {
    		super.instance.set(key.toString(), null); // this will create a null-node
    	} else {
    		setValueInObjectNode(key.toString(),objectMapper.valueToTree(value));
    	}
	}

	public boolean equals(Object o) {	
		if(o == null) {
			return false;
		}
		if (o == this) {
            return true;
		}
		if(objectClass.isAssignableFrom(o.getClass())) {
			try {
				return this.equals(objectWrapperClass.getConstructor(objectClass).newInstance(o));
			}catch(Exception e) {
				return false;
			}
		}
		if(!(o instanceof Map)) {
			return false;
		}
		Map map = (Map) o;
        try {
            Iterator<Entry> i = entrySet().iterator();
            while (i.hasNext()) {
                Entry e = i.next();
                Object key = e.getKey();
                Object value =  JacksonJsonProvider.fromJsonNode(e.getValue());
                
                if (value == null) {
                    if (!(map.get(key)==null && map.containsKey(key))) {
                        return false;
                    }
                    continue;
                }
                Object effectiveValue =value; 
            	if (objectClass.isAssignableFrom(value.getClass())) {
    				effectiveValue =  objectWrapperClass.getConstructor(objectClass).newInstance(value);        				
        		} else if (arrayClass.isAssignableFrom(value.getClass())) {
        			effectiveValue =  arrayWrapperClass.getConstructor(arrayClass).newInstance(value);        		
        		}
                if (!effectiveValue.equals(map.get(key))){
                    return false;
                }
            }
        } catch (Exception unused) {
            return false;
        }
        return true;
	}
}
