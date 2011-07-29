package com.jayway.jsonpath.json.gson;


import javax.swing.text.Element;

import net.minidev.json.JSONValue;

import com.jayway.jsonpath.json.JsonArray;

import com.jayway.jsonpath.json.JsonElement;
import com.jayway.jsonpath.json.JsonException;
import com.jayway.jsonpath.json.JsonObject;
import com.jayway.jsonpath.json.JsonPrimitive;


public class GsonJsonPrimitive extends com.jayway.jsonpath.json.JsonPrimitive{

	
	
	private com.google.gson.JsonPrimitive value;

	public GsonJsonPrimitive( Object obj) {
		value = unwrapit(obj);
	}

	public GsonJsonPrimitive( com.google.gson.JsonPrimitive obj) {
		value = obj;
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

	public Object wrappit(com.google.gson.JsonPrimitive prim){
		if(prim == null ) return null;
		if(prim.isBoolean()){
			return new Boolean(prim.getAsBoolean());
		}
		if(prim.isString()){
			return new String(prim.getAsString());
		}
		if(prim.isNumber()){
			if(prim.getAsDouble()==prim.getAsInt())
				return new Integer(prim.getAsInt());
			else
				return new Double(prim.getAsDouble());
		}
		return null;
	}
	
	public com.google.gson.JsonPrimitive unwrapit(Object o){
		if(o == null ) return null;
		if(o instanceof Boolean ){
			return new com.google.gson.JsonPrimitive((Boolean)o);
		}	
		if(o instanceof String ){
			return new com.google.gson.JsonPrimitive((String)o);
		}
		if(o instanceof Number ){
			return new com.google.gson.JsonPrimitive((Number)o);
		}
		return null;
	}
	
	
	public Object getWrappedElement() {
		return value;
	}

	@Override
	public boolean equals(Object o1){
		if (o1==null) return false;
		if(o1 instanceof GsonJsonPrimitive){
			if(value == null)
				return ((GsonJsonPrimitive) o1).value == null;
			else
				return value.equals(((GsonJsonPrimitive) o1).value);
		}
		return (o1!=null) && o1.equals(wrappit(value));
	}
	@Override
	public int hashCode(){
		return wrappit(value).hashCode();
	}
	@Override
	public String toString(){
		if(value == null) return "null";
		return wrappit(value).toString();
	}

	@Override
	public boolean isJsonPrimitive() {
		return true;
	}

	@Override
	public Object toObject() throws JsonException {
		return wrappit(value);
	}

	@Override
	public boolean isJsonNull() {

		return false;
	}

	@Override
	public void merge(JsonElement o) throws JsonException {
		throw new JsonException("Can't merge a primitive");
	}



	
	
}
