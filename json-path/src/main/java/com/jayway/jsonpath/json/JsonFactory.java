package com.jayway.jsonpath.json;

import java.io.IOException;



public abstract class JsonFactory {
	
	private static JsonFactory instance;

	public static JsonFactory getInstance(){
		if(instance == null)
			instance = new com.jayway.jsonpath.json.gson.GsonJsonFactory();
		return instance;
	}
	public static void setInstance(JsonFactory gsonJsonFactory) {
		instance = gsonJsonFactory;
	} 

	public abstract JsonObject createJsonObject();
	public abstract JsonArray createJsonArray();
	public abstract JsonElement parse(String json) throws ParseException, IOException;
	public JsonNull createJsonNull(String path,JsonElement parent) {
		JsonNull jn = new JsonNull();
		jn.setParentReference(parent,path); 
		return jn;
	}

	public JsonNull createJsonNull(Integer index,JsonElement parent) {
		JsonNull jn = new JsonNull();
		jn.setParentReference(parent,index); 
		return jn;
	}
	public JsonElement createJsonNull() {
		JsonNull jn = new JsonNull();
		return jn;
	}

	public abstract JsonPrimitive createJsonPrimitive(Object obj);




	




}
