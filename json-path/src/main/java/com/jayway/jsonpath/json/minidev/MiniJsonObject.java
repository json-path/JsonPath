package com.jayway.jsonpath.json.minidev;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;

import com.jayway.jsonpath.json.JsonArray;
import com.jayway.jsonpath.json.JsonElement;
import com.jayway.jsonpath.json.JsonException;
import com.jayway.jsonpath.json.JsonObject;
import com.jayway.jsonpath.json.JsonPrimitive;
import com.jayway.jsonpath.json.ParentReference;


public class MiniJsonObject extends com.jayway.jsonpath.json.JsonObject{


	
	public JSONObject element;
	public MiniJsonObject(JSONObject elem){
		element = elem;
	}
	

	public List<JsonElement> getProperties() throws JsonException {
		List<JsonElement> out = new ArrayList<JsonElement>();
		for(String s:  element.keySet()){
			out.add(MiniUtil.convertUp(element.get(s), new ParentReference(this,s)));
		}
		return out;
	}

	public boolean hasProperty(String pathFragment) {
		return element.containsKey(pathFragment);
	}

	public JsonElement getProperty(String pathFragment) throws JsonException {
		return MiniUtil.convertUp(element.get(pathFragment), new ParentReference(this,pathFragment));
	}

	public boolean isContainer() {
		return true;
	}

	public boolean isJsonArray() {

		return false;
	}

	public boolean isJsonObject() {

		return true;
	}

	public JsonArray toJsonArray() throws JsonException {
		throw new JsonException();
	}

	public com.jayway.jsonpath.json.JsonObject toJsonObject() {
		return this;
	}

	public void clear() {
		throw new UnsupportedOperationException();
		
	}

	public boolean containsKey(Object arg0) {
		throw new UnsupportedOperationException();
	}

	public boolean containsValue(Object arg0) {
		throw new UnsupportedOperationException();
		
	}

	public Set<java.util.Map.Entry<String, JsonElement>> entrySet() {
		throw new UnsupportedOperationException();
		
	}

	public JsonElement get(Object arg0) {
		try {
			return MiniUtil.convertUp(element.get(arg0), new ParentReference(this,arg0.toString()));
		} catch (JsonException e) {
			throw new RuntimeException();
		}
		
	}

	public boolean isEmpty() {
		throw new UnsupportedOperationException();
		
	}

	public Set<String> keySet() {
		throw new UnsupportedOperationException();
		
	}

	public JsonElement put(String arg0, JsonElement arg1) {
		this.element.put(arg0, arg1.getWrappedElement());
		return arg1;
	}

	public void putAll(Map<? extends String, ? extends JsonElement> arg0) {
		throw new UnsupportedOperationException();
		
	}

	public JsonElement remove(Object arg0) {
		throw new UnsupportedOperationException();

	}

	public int size() {
		throw new UnsupportedOperationException();
		
	}

	public Collection<JsonElement> values() {
		throw new UnsupportedOperationException();
		
	}

	public JsonPrimitive toPrimitive() throws JsonException {
		throw new JsonException();
	}



	public Object getWrappedElement() {
		return element;
	}

	public String toString(){
		return element.toString();
	}


	@Override
	public void setProperty(String name,JsonElement element) throws JsonException {
		this.put(name, element);
	}


	@Override
	public boolean isJsonPrimitive() {
		return true;
	}


	@Override
	public Object toObject() throws JsonException {
		return element;
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


	




}
