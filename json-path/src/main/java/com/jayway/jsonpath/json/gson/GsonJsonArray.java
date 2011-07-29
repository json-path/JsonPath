package com.jayway.jsonpath.json.gson;


import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import com.google.gson.JsonArray;
import com.jayway.jsonpath.json.JsonElement;
import com.jayway.jsonpath.json.JsonException;
import com.jayway.jsonpath.json.JsonObject;
import com.jayway.jsonpath.json.JsonPrimitive;
import com.jayway.jsonpath.json.ParentReference;



public class GsonJsonArray extends com.jayway.jsonpath.json.JsonArray {
	public com.google.gson.JsonArray element;
	public GsonJsonArray(com.google.gson.JsonArray elem){
		element = elem;
	}
	
	
	public boolean add(com.jayway.jsonpath.json.JsonElement item) {
		element.add((com.google.gson.JsonElement)item.getWrappedElement());
		return true;
	}

	public void add(int index, com.jayway.jsonpath.json.JsonElement element) {
		throw new UnsupportedOperationException();
		
	}
	public boolean addAll(
			Collection<? extends com.jayway.jsonpath.json.JsonElement> c) {
		boolean ok = true;
		for(com.jayway.jsonpath.json.JsonElement e : c ){
			element.add((com.google.gson.JsonElement)e.getWrappedElement());
		}
		return ok;
	}
	public boolean addAll(int index,
			Collection<? extends com.jayway.jsonpath.json.JsonElement> c) {
		throw new UnsupportedOperationException();
	}
	public void clear() {
		element = new com.google.gson.JsonArray();
	}
	public boolean contains(Object o) {
		throw new UnsupportedOperationException();
	}
	public boolean containsAll(Collection<?> c) {
		throw new UnsupportedOperationException();
	}
	public com.jayway.jsonpath.json.JsonElement get(int index) {
		try {
			return GsonUtil.convertUp(element.get(index),new ParentReference(this,index));
		} catch (JsonException e) {
			return null;

		}
	}
	public int indexOf(Object o) {
		throw new UnsupportedOperationException();
	}
	public boolean isEmpty() {
		throw new UnsupportedOperationException();

	}
	public Iterator<com.jayway.jsonpath.json.JsonElement> iterator() {
		final GsonJsonArray me = this;
		return new Iterator<com.jayway.jsonpath.json.JsonElement>() {
			Iterator<com.google.gson.JsonElement> inner;
			int count;
			{
				inner = element.iterator();
				count =0 ;
			}
			public boolean hasNext() {
				return inner.hasNext();
			}

			public com.jayway.jsonpath.json.JsonElement next() {
				com.google.gson.JsonElement next = inner.next();
				

				
				try {
					return GsonUtil.convertUp(next, new ParentReference(me,count++));
				} catch (JsonException e) {
					return null;
				}
				
			}

			public void remove() {
				inner.remove();
				
			}
		};
		
	}
	public int lastIndexOf(Object o) {
		throw new UnsupportedOperationException();
		
	}
	public ListIterator<com.jayway.jsonpath.json.JsonElement> listIterator() {
		throw new UnsupportedOperationException();

	}
	public ListIterator<com.jayway.jsonpath.json.JsonElement> listIterator(
			int index) {
		throw new UnsupportedOperationException();

	}
	public boolean remove(Object o) {
		throw new UnsupportedOperationException();
	}
	public com.jayway.jsonpath.json.JsonElement remove(int index) {
	
		try {
			com.jayway.jsonpath.json.JsonElement je = this.get(index);
			this.element = (JsonArray) GsonUtil.removeGsonArrayElement(index,this).getWrappedElement();
			return je;
		} catch (JsonException e) {	
			e.printStackTrace();
			return null;
		}
	}
	public boolean removeAll(Collection<?> c) {
		throw new UnsupportedOperationException();
		
	}
	public boolean retainAll(Collection<?> c) {
		throw new UnsupportedOperationException();

	}
	public com.jayway.jsonpath.json.JsonElement set(int index,
			com.jayway.jsonpath.json.JsonElement element) {
		
		try {
			this.element = (JsonArray)GsonUtil.setGsonArrayElement(index,element,this).getWrappedElement();
			return element;
		} catch (JsonException e) {
			e.printStackTrace();
			return null;
		}
		
	}
	
	
	public int size() {
		return element.size();
		
	}
	public List<com.jayway.jsonpath.json.JsonElement> subList(int fromIndex,
			int toIndex) {
		throw new UnsupportedOperationException();
		
	}
	public Object[] toArray() {
		throw new UnsupportedOperationException();
	}
	public <T> T[] toArray(T[] a) {
		throw new UnsupportedOperationException();
		
	}
	public boolean isContainer() {
		return true;
	}
	public boolean isJsonArray() {
		return true;
	}
	public boolean isJsonObject() {
		return false;
	}

	public com.jayway.jsonpath.json.JsonArray toJsonArray() {
		return this;
	}
	public JsonObject toJsonObject() throws JsonException {
		throw new JsonException();
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
	public boolean isJsonPrimitive() {
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


	@Override
	public void merge(JsonElement inputObjectb) throws JsonException {
		if(inputObjectb.isJsonArray()){
			this.merge(inputObjectb.toJsonArray());
		}
	}


	
	public void merge(com.jayway.jsonpath.json.JsonArray ja)
			throws JsonException {
		for(JsonElement j : ja){
			this.add(j);
		}
	}

	
}
