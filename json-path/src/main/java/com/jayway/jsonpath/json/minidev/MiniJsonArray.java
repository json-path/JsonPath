package com.jayway.jsonpath.json.minidev;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONStyle;


import com.jayway.jsonpath.json.JsonArray;
import com.jayway.jsonpath.json.JsonElement;
import com.jayway.jsonpath.json.JsonException;
import com.jayway.jsonpath.json.JsonObject;
import com.jayway.jsonpath.json.JsonPrimitive;
import com.jayway.jsonpath.json.ParentReference;



public class MiniJsonArray extends com.jayway.jsonpath.json.JsonArray {

	
	public JSONArray element;
	
	



	public Object clone() {
		return element.clone();
	}



	public void ensureCapacity(int minCapacity) {
		element.ensureCapacity(minCapacity);
	}



	public boolean equals(Object o) {
		return element.equals(o);
	}



	public int hashCode() {
		return element.hashCode();
	}



	public void merge(Object o2) {
		element.merge(o2);
	}





	


	public MiniJsonArray(JSONArray elem){
		this.element = elem;
	}
	
	
	
	public boolean add(com.jayway.jsonpath.json.JsonElement item) {
		return element.add(item.getWrappedElement());
		
	}

	public void add(int index, com.jayway.jsonpath.json.JsonElement element) {
		throw new UnsupportedOperationException();
		
	}
	public boolean addAll(
			Collection<? extends com.jayway.jsonpath.json.JsonElement> c) {
		boolean ok = true;
		for(com.jayway.jsonpath.json.JsonElement e : c ){
			ok &= element.add(e.getWrappedElement());
		}
		return ok;
	}
	public boolean addAll(int index,
			Collection<? extends com.jayway.jsonpath.json.JsonElement> c) {
		throw new UnsupportedOperationException();
	}
	public void clear() {
		element.clear();
	}
	public boolean contains(Object o) {
		return element.contains(o);
	}
	public boolean containsAll(Collection<?> c) {
		return element.containsAll(c);
	}
	public com.jayway.jsonpath.json.JsonElement get(int index) {
		try {
			return MiniUtil.convertUp(element.get(index), new ParentReference(this,index));
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
		final MiniJsonArray ja = this;
		return new Iterator<com.jayway.jsonpath.json.JsonElement>() {
			Iterator<Object> inner;
			int count;
			{
				count = 0;
				inner = element.iterator();
			}
			public boolean hasNext() {
				return inner.hasNext();
			}

			public com.jayway.jsonpath.json.JsonElement next() {
				Object next = inner.next();

				
				try {
					return MiniUtil.convertUp(next,	 new ParentReference(ja,count++));
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
		return element.lastIndexOf(o);
		
	}
	public ListIterator<com.jayway.jsonpath.json.JsonElement> listIterator() {
		throw new UnsupportedOperationException();

	}
	public ListIterator<com.jayway.jsonpath.json.JsonElement> listIterator(
			int index) {
		throw new UnsupportedOperationException();

	}
	public boolean remove(Object o) {
		return element.remove(o);
		
	}
	public com.jayway.jsonpath.json.JsonElement remove(int index) {
		throw new UnsupportedOperationException();
		
	}
	public boolean removeAll(Collection<?> c) {
		throw new UnsupportedOperationException();
		
	}
	public boolean retainAll(Collection<?> c) {
		throw new UnsupportedOperationException();
		
	}

	public int size() {
		return element.size();
		
	}
	public List<com.jayway.jsonpath.json.JsonElement> subList(int fromIndex,
			int toIndex) {
		throw new UnsupportedOperationException();
		
	}
	public Object[] toArray() {
		
		return element.toArray();
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
		// TODO Auto-generated method stub
		return false;
	}



	public JsonElement set(int index, JsonElement element) {
		this.element.set(index, element);
		return element;
	}



	@Override
	public void merge(JsonElement o) throws JsonException {
		// TODO Auto-generated method stub
		
	}






	
}
