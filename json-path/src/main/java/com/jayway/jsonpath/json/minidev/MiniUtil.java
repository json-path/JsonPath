package com.jayway.jsonpath.json.minidev;

import com.jayway.jsonpath.json.JsonElement;
import com.jayway.jsonpath.json.JsonException;
import com.jayway.jsonpath.json.ParentReference;

public class MiniUtil {

	public   static JsonElement convertUp(Object object) throws JsonException {
		if(object instanceof net.minidev.json.JSONArray)
			return new MiniJsonArray( (net.minidev.json.JSONArray)  object );
		else if(object instanceof net.minidev.json.JSONObject)
			return new MiniJsonObject( (net.minidev.json.JSONObject)  object );
		else 
			return new MiniJsonPrimitive(   object );
		

	}
	public   static JsonElement convertUp(Object o,ParentReference pr) throws JsonException {
		JsonElement j = convertUp(o);
		j.setParentReference(pr);
		return j;
	}	
	


	
}
