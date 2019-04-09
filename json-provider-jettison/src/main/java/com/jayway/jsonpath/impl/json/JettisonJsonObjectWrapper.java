package com.jayway.jsonpath.impl.json;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import com.jayway.jsonpath.spi.json.JsonObjectWrapper;
import com.jayway.jsonpath.spi.json.JsonProvider;

public class JettisonJsonObjectWrapper extends JsonObjectWrapper<JSONObject> {

	public JettisonJsonObjectWrapper(JSONObject instance) {
		super(instance, JSONObject.class,JSONArray.class, JettisonJsonObjectWrapper.class, JettisonJsonArrayWrapper.class );
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
		try {
			if(value == null ||  value == JsonProvider.UNDEFINED) {
				super.instance.put(key.toString(), JSONObject.NULL);
			} else {
				super.instance.put(key.toString(), value);
			}
		} catch(JSONException e) {
			e.printStackTrace();
		}
		return o;
	}

}
