package com.jayway.jsonpath.json.gson;

import java.io.IOException;

import com.google.gson.JsonPrimitive;
import com.jayway.jsonpath.json.JsonElement;
import com.jayway.jsonpath.json.JsonNull;
import com.jayway.jsonpath.json.JsonObject;
import com.jayway.jsonpath.json.ParseException;

import net.minidev.json.JSONArray;



public class GsonJsonFactory extends com.jayway.jsonpath.json.JsonFactory{


	public GsonJsonArray createJsonArray() {
		return new GsonJsonArray(new com.google.gson.JsonArray());
	}

	@Override
	public JsonElement parse(String json) throws ParseException, IOException {
		return new GsonJsonParser().parse(json);
	}

	@Override
	public JsonObject createJsonObject() {
		return new com.jayway.jsonpath.json.gson.GsonJsonObject(new com.google.gson.JsonObject());
	}



	@Override
	public com.jayway.jsonpath.json.JsonPrimitive createJsonPrimitive(Object obj) {
		return new GsonJsonPrimitive(obj);
	}



	


}
