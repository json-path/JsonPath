package com.jayway.jsonpath.json.minidev;


import javax.swing.text.Element;

import net.minidev.json.JSONValue;

import com.jayway.jsonpath.json.JsonArray;

import com.jayway.jsonpath.json.JsonElement;
import com.jayway.jsonpath.json.JsonException;
import com.jayway.jsonpath.json.JsonObject;
import com.jayway.jsonpath.json.JsonPrimitive;


public class MiniJsonPrimitive extends com.jayway.jsonpath.json.JsonPrimitive{

	
	private Object value;

	public MiniJsonPrimitive(Object object) {
		value = object;
	}

	public boolean isContainer() {

		return false;
	}

	public boolean isJsonArray() {

		return false;
	}

	public boolean isJsonObject() {

		return false;
	}

	public JsonArray toJsonArray() throws JsonException {
		throw new JsonException();
	}

	public JsonObject toJsonObject() throws JsonException {
		throw new JsonException();
		
	}

	public JsonPrimitive toPrimitive() {
		return this;
	}

	public Object getWrappedElement() {
		return value;
	}

	@Override
	public boolean isJsonPrimitive() {
		return true;
	}

	@Override
	public Object toObject() throws JsonException {
		return this.value;
	}
	@Override
	public String toString() {
		return this.value.toString();
	}

	@Override
	public boolean isJsonNull() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void merge(JsonElement o) throws JsonException {
		// TODO Auto-generated method stub
		
	}

	public boolean equals(Object o1){
		if(o1== null)return false;
		return (this.getWrappedElement().toString().equals(o1.toString()));
		
	}


}
