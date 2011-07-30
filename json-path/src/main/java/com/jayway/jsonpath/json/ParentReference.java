package com.jayway.jsonpath.json;

public class ParentReference {
	protected JsonElement parent;
	protected Integer index;
	protected String field;
	
	public ParentReference(JsonElement parent, String field) {
		this.parent = parent;
		this.field = field;
	}

	public ParentReference(JsonElement parent, int index) {
		this.index = index;
		this.parent = parent;
	}

	public void setParentField(String field) throws JsonException {
		if(parent == null || !parent.isJsonObject())
			throw new JsonException("Parent is null or not an object");

		this.field = field;
	}
	
	public void setReference(JsonElement element) throws JsonException{
		if(parent == null){
			throw new JsonException("Parent is null");
		}
		else if(parent.isJsonArray()){
			if( index == null || index == -1)
				parent.toJsonArray().add(element);
			parent.toJsonArray().set(index, element);
		}
		else if(parent.isJsonObject()){
			parent.toJsonObject().put(field, element);
		}
		else{
			throw new RuntimeException("Unexpected error: Parent is not a container");
		}
		element.setParentReference(this);
	}
	
	public String getField() {
		return field;
	}

	public void setField(String s) {
		this.field= s;
	}

	public void setParent(JsonElement parent) {

		this.parent = parent;
	}
	
	public Integer getIndex() throws JsonException {
		if(parent == null || !parent.isJsonArray())
			throw new JsonException("Parent is null or not an object");
		return this.index;
	}

	public void setIndex(Integer index) {
		this.index = index;
	}
		

	public JsonElement getParent() {
		return parent;
	}
}
