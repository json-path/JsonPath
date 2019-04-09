package com.jayway.jsonpath.impl.json;

import java.lang.reflect.Field;
import java.util.List;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import com.jayway.jsonpath.spi.json.JsonArrayWrapper;
import com.jayway.jsonpath.spi.json.JsonProvider;

public class JettisonJsonArrayWrapper extends JsonArrayWrapper<JSONArray> {

	public JettisonJsonArrayWrapper(JSONArray instance) {
		super(instance, JSONObject.class, JSONArray.class, JettisonJsonObjectWrapper.class, JettisonJsonArrayWrapper.class );
	}

	@Override
	public int size() {
		return super.instance.length();
	}

	@Override
	public Object get(int index) {		
		try {
			return super.instance.get(index);
		} catch (JSONException e) {
			return JsonProvider.UNDEFINED;
		}
	}

	@Override
	public Object doSet(int index, Object element) {
		Object o = super.instance.opt(index);
		try {
			if(element == null || element == JsonProvider.UNDEFINED) {
				super.instance.put(index, JSONObject.NULL);
			} else {
			    super.instance.put(index, element) ;
			}
		}catch(JSONException e) {	
			e.printStackTrace();
		}
		return o;
	}

	@Override
	public void doAdd(int index, Object element) {
		try{
			if(element == null || element == JsonProvider.UNDEFINED) {					
				super.instance.put(index, JSONObject.NULL);					
			} else {
			    super.instance.put(index, element) ;
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	@Override
	public Object remove(int index) {
		//jettison JSONArray's remove method implementation is really weird !!
		try {
			Field f = JSONArray.class.getDeclaredField("myArrayList");
			f.setAccessible(true);
			List list = (List) f.get(super.instance);
			return list.remove(index);
		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
			 Object o = super.instance.opt(index);
			 if(o!=null) {
				 super.instance.remove(o);
			 }
			 return o;
		}
	}

}
