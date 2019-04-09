package com.jayway.jsonpath.impl.json;

import org.apache.tapestry5.json.JSONArray;
import org.apache.tapestry5.json.JSONObject;

import com.jayway.jsonpath.spi.json.JsonArrayWrapper;
import com.jayway.jsonpath.spi.json.JsonProvider;

public class TapestryJsonArrayWrapper extends JsonArrayWrapper<JSONArray> {

	public TapestryJsonArrayWrapper(JSONArray instance) {
		super(instance, JSONObject.class, JSONArray.class, TapestryJsonObjectWrapper.class, TapestryJsonArrayWrapper.class );
	}

	@Override
	public int size() {
		return super.instance.length();
	}

	@Override
	public Object get(int index) {		
		return super.instance.get(index);
	}

	@Override
	public Object doSet(int index, Object element) {
		Object o = null;
		try {
			o = get(index);
		} catch(Exception e) {}
		if(element == null || element == JsonProvider.UNDEFINED) {
			super.instance.put(index, JSONObject.NULL);
		} else {
		    super.instance.put(index, element) ;
		}
		return o;
	}

	@Override
	public void doAdd(int index, Object element) {
		if(element == null || element == JsonProvider.UNDEFINED) {
			super.instance.put(index, JSONObject.NULL);
		} else {
		    super.instance.put(index, element) ;
		}
	}

	@Override
	public Object remove(int index) {
		 Object o = get(index);
		 super.instance.remove(index);
		 return o;
	}

}
