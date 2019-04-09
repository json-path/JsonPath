package com.jayway.jsonpath.impl.json;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

import org.apache.tapestry5.json.JSONArray;
import org.apache.tapestry5.json.JSONObject;

import com.jayway.jsonpath.spi.json.JsonObjectWrapper;
import com.jayway.jsonpath.spi.json.JsonProvider;

public class TapestryJsonObjectWrapper extends JsonObjectWrapper<JSONObject> {

	public TapestryJsonObjectWrapper(JSONObject instance) {
		super(instance, JSONObject.class, JSONArray.class, TapestryJsonObjectWrapper.class, TapestryJsonArrayWrapper.class );
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
		return super.instance.keys();
	}

	@Override
	public Collection values() {
		return super.instance.toMap().values();
	}

	@Override
	public Set entrySet() {
		return super.instance.toMap().entrySet();
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

}
