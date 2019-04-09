package com.jayway.jsonpath.impl.json;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.json.JSONArray;
import org.json.JSONObject;

import com.jayway.jsonpath.spi.json.JsonObjectWrapper;
import com.jayway.jsonpath.spi.json.JsonProvider;

public class JsonOrgJsonObjectWrapper extends JsonObjectWrapper<JSONObject> {

	public JsonOrgJsonObjectWrapper(JSONObject instance) {
		super(instance, JSONObject.class, JSONArray.class, JsonOrgJsonObjectWrapper.class, JsonOrgJsonArrayWrapper.class );
	}

	@Override
	public int size() {
		return super.instance.length();
	}

	@Override
	public Iterator iterator() {
		return entrySet().iterator();
	}

	@Override
	public Object get(Object key) {
		Object o = this.instance.opt(key.toString());
		return o;
	}

	@Override
	public Object remove(Object key) {
		return super.instance.remove(key.toString());
	}
	
	@Override
	public Set keySet() {
		Set keys = new HashSet();
		for(Iterator iterator = super.instance.keys();iterator.hasNext();) {
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
		for(Iterator iterator = super.instance.keys();iterator.hasNext();) {
			String key = (String) iterator.next();
			entryMap.put(key, super.instance.opt(key));
		}
		return entryMap;
	}

	@Override
	protected Object doPut(Object key, Object value) {
		Object o = this.instance.opt(key.toString());
		if(value == null ||  value == JsonProvider.UNDEFINED) {
			super.instance.put(key.toString(), JSONObject.NULL);
		} else {
			super.instance.put(key.toString(), value);
		}
		return o;
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
                Object value = e.getValue();
                
                if (value == null) {
                    if (!(map.get(key)==null && map.containsKey(key))) {
                        return false;
                    }
                    continue;
                }
                Object effectiveValue = value; 
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
