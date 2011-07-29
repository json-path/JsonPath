package com.jayway.jsonpath.json.gson;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
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


public class GsonJsonObject extends com.jayway.jsonpath.json.JsonObject{



	
	
	public com.google.gson.JsonObject element;
	public GsonJsonObject(com.google.gson.JsonObject elem){
		element = elem;
	}
	

	public List<JsonElement> getProperties() throws JsonException {
		List<JsonElement> out = new ArrayList<JsonElement>();
		for(Entry o:  element.entrySet()){
			out.add(GsonUtil.convertUp((com.google.gson.JsonElement)o.getValue(),new ParentReference(this,o.getKey().toString())));
		}
		return out;
	}

	public boolean hasProperty(String pathFragment) {
		return element.has(pathFragment);
	}

	public JsonElement getProperty(String pathFragment) throws JsonException {
		return GsonUtil.convertUp(element.get(pathFragment),new ParentReference(this,pathFragment));
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
		Set<Entry<String,JsonElement>> newSet = new HashSet<Entry<String,JsonElement>>();
		for(Entry o:  element.entrySet()){
			JsonElement e;
			try {
				e = GsonUtil.convertUp((com.google.gson.JsonElement)o.getValue(),new ParentReference(this,o.getKey().toString()));
			} catch (JsonException e1) {
				throw new RuntimeException(); 
			}
			newSet.add(new java.util.AbstractMap.SimpleEntry( o.getKey(),e));
		}
		return newSet;
		
	}

	public JsonElement get(Object arg0) {
		try {
			return GsonUtil.convertUp(element.get(arg0.toString()), new ParentReference(this,arg0.toString()));
		} catch (JsonException e) {
			throw new RuntimeException();
		}
		
	}

	public boolean isEmpty() {
		throw new UnsupportedOperationException();
		
	}

	public Set<String> keySet() {
		Set<String> newSet = new HashSet<String>();
		for(Entry o:  element.entrySet()){
			newSet.add(o.getKey().toString());
		}
		return newSet;
		
	}

	public JsonElement put(String arg0, JsonElement arg1) {
		this.element.add(arg0,(com.google.gson.JsonElement)arg1.getWrappedElement());
		return arg1;
	}

	public void putAll(Map<? extends String, ? extends JsonElement> arg0) {
		throw new UnsupportedOperationException();
		
	}

	public JsonElement remove(Object arg0) {
		try {
			return GsonUtil.convertUp(this.element.remove(arg0.toString()));
		} catch (JsonException e) {
			throw new RuntimeException(); 
			
		}
		

	}

	public int size() {
		throw new UnsupportedOperationException();
		
	}

	public Collection<JsonElement> values() {
		ArrayList<JsonElement> newSet = new ArrayList<JsonElement>();
		for(Entry o:  element.entrySet()){
			JsonElement e;
			try {
				e = GsonUtil.convertUp((com.google.gson.JsonElement)o.getValue(),new ParentReference(this,o.getKey().toString()));
			} catch (JsonException e1) {
				throw new RuntimeException(); 
			}
			newSet.add(e);
		}
		return newSet;
		
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
	public void setProperty(String name, JsonElement element)
			throws JsonException {
		this.put(name, element);
		
	}


	@Override
	public boolean isJsonPrimitive() {
		// TODO Auto-generated method stub
		return false;
	}


	@Override
	public Object toObject() throws JsonException {
				return element;
	}


	@Override
	public boolean isJsonNull() {

		return false;
	}


	public void merge(JsonObject inputObjectb) throws JsonException {
		for(Entry<String,JsonElement> j :inputObjectb.entrySet()){
			this.setProperty(j.getKey(), j.getValue());
		}
	}


	@Override
	public void merge(JsonElement inputObjectb) throws JsonException {
		if(inputObjectb.isJsonObject()){
			this.merge(inputObjectb.toJsonObject());
		}
	}


}


