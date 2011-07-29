package com.jayway.jsonpath.json.minidev;

import java.io.IOException;

import com.jayway.jsonpath.json.JsonElement;
import com.jayway.jsonpath.json.JsonNull;
import com.jayway.jsonpath.json.JsonObject;
import com.jayway.jsonpath.json.JsonPrimitive;
import com.jayway.jsonpath.json.ParseException;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;



public class MiniJsonFactory extends com.jayway.jsonpath.json.JsonFactory{


	
	public MiniJsonArray createJsonArray() {
		return new MiniJsonArray(new JSONArray());
	}

	@Override
	public JsonElement parse(String json) throws ParseException, IOException {
		return new MiniJsonParser().parse(json);
	}

	@Override
	public JsonObject createJsonObject() {
		return new com.jayway.jsonpath.json.minidev.MiniJsonObject(new JSONObject());
	}



	@Override
	public JsonPrimitive createJsonPrimitive(Object obj) {
		return new MiniJsonPrimitive(obj);
		
	}



}
