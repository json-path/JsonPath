package com.jayway.jsonpath.json;


import com.jayway.jsonpath.filter.JsonType;


public abstract class JsonElement  {

	public abstract JsonObject toJsonObject() throws JsonException;
	public abstract JsonArray toJsonArray() throws JsonException;
	public abstract JsonPrimitive toPrimitive() throws JsonException;
	public abstract Object toObject() throws JsonException;
	
	public abstract Object getWrappedElement();


	public abstract boolean isJsonObject();
	public abstract boolean isJsonArray();
	public abstract boolean isJsonPrimitive();
	public abstract boolean isContainer();
	public boolean isJsonType(JsonType t){
		return ((t.equals(JsonType.JsonArray) && isJsonArray())
		     || (t.equals(JsonType.JsonObject) && isJsonObject())
		     || (t.equals(JsonType.JsonPrimitive) && isJsonPrimitive()));

	}
	
	private ParentReference parentRef;

	public void setParentReference(ParentReference pr){
		this.parentRef = pr;
	}

	public ParentReference getParentReference(){
		return parentRef;
	}

	public void setParentReference(JsonElement parent, String path) {
		this.parentRef = new ParentReference(parent,path);
	}
	public void setParentReference(JsonElement parent, int index) {
		this.parentRef = new ParentReference(parent,index);
	}
	
	
	public abstract boolean isJsonNull();


	public abstract void merge(JsonElement o) throws JsonException;



	
}
