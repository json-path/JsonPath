package com.jayway.jsonpath.json;

public  class JsonNull extends JsonElement {
	public boolean isJsonNull() {
		return true;
	}

	@Override
	public Object getWrappedElement() {
		return null;
	}

	@Override
	public boolean isContainer() {
		return false;
	}

	@Override
	public boolean isJsonArray() {
		return false;
	}

	@Override
	public boolean isJsonObject() {
		return false;
	}

	@Override
	public boolean isJsonPrimitive() {
		return false;
	}

	@Override
	public JsonArray toJsonArray() throws JsonException {
		throw new JsonException();
	}

	@Override
	public String toString(){
		return null;
	}
	@Override
	public JsonObject toJsonObject() throws JsonException {
		throw new JsonException();
	}

	@Override
	public Object toObject() throws JsonException {
		throw new JsonException();
	}

	@Override
	public JsonPrimitive toPrimitive() throws JsonException {
		throw new JsonException();		
	}

	@Override
	public void merge(JsonElement inputObjectb) throws JsonException {
		throw new JsonException("Cannot merge a null");	// TODO Auto-generated method stub
	}


	@Override
	public boolean equals(Object o1){
		if(o1==null) return false;
		return o1 instanceof JsonNull;
	}
	
}
