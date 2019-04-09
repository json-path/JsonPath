package com.jayway.jsonpath.spi.json;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.jayway.jsonpath.JsonPathException;

public abstract class JsonWrapperJsonProvider<A,O> implements JsonProvider {

	public abstract JsonArrayWrapper<A> createArrayWrapper(A array);

	public abstract JsonObjectWrapper<O> createObjectWrapper(O object);

	protected abstract boolean instanceOfArray(Class clazz);

	protected abstract boolean instanceOfObject(Class clazz);
	 	 
	@Override
	public Object createArray() {
		return createArrayWrapper((A)null);
	}
	
	@Override
	public Object createMap() {
		return createObjectWrapper((O)null);
	}

	public JsonArrayWrapper<A> createArrayWrapper(List array){
		if(JsonArrayWrapper.class.isAssignableFrom(array.getClass())) {
			return (JsonArrayWrapper<A>) array;
		}
		JsonArrayWrapper<A> wrapper = createArrayWrapper((A)null);
		Iterator iterator = array.iterator();
		for(;iterator.hasNext();) {
			Object o = iterator.next();
			if(o!=null && List.class.isAssignableFrom(o.getClass())){
				wrapper.add(createArrayWrapper((List)o));
				continue;
			}
			if(o!=null && Map.class.isAssignableFrom(o.getClass())){
				wrapper.add(createObjectWrapper((Map)o));
				continue;
			}
			wrapper.add(o);
		}
		return wrapper;		
	}

	public JsonObjectWrapper<O> createObjectWrapper(Map object){
		if(JsonObjectWrapper.class.isAssignableFrom(object.getClass())) {
			return (JsonObjectWrapper<O>) object;
		}
		JsonObjectWrapper<O> wrapper = createObjectWrapper((O) null);
		Iterator<Entry> iterator = object.entrySet().iterator();
		for(;iterator.hasNext();) {
			Entry e = iterator.next();
			Object key = e.getKey();
			Object value = e.getValue();
			if(List.class.isAssignableFrom(value.getClass())){
				wrapper.put(key.toString(),createArrayWrapper((List)value));
				continue;
			}
			if(Map.class.isAssignableFrom(value.getClass())){
				wrapper.put(key.toString(),createObjectWrapper((Map)value));
				continue;
			}
			wrapper.put(key.toString(),value);
		}
		return wrapper;		
	}
	
	@Override
	public boolean isArray(Object obj) {
		return obj!=null && (obj instanceof List || instanceOfArray(obj.getClass()));
	}

	@Override
	public int length(Object obj) {
		if(instanceOfObject(obj.getClass())) {
			 return length(createObjectWrapper((O)obj));
		}
		if(instanceOfArray(obj.getClass())) {
			 return length(createArrayWrapper((A)obj));
		}
		if(JsonWrapper.class.isAssignableFrom(obj.getClass())) {
			return ((JsonWrapper)obj).size();
		}
		if(Map.class.isAssignableFrom(obj.getClass())) {
			return ((Map)obj).size();
		}
		if(List.class.isAssignableFrom(obj.getClass())) {
			return ((List)obj).size();
		}
		if(String.class == obj.getClass()) {
			return ((String)obj).length();
		}
        throw new JsonPathException("length operation cannot be applied to " + obj!=null?obj.getClass().getName():"null");
	}

    @Override
    public String toJson(Object obj) {    	
    	return obj.toString();
    }

	@Override
	public Iterable<?> toIterable(Object obj) {
		if(instanceOfObject(obj.getClass())) {
			 return createObjectWrapper((O)obj);
		}
		if(instanceOfArray(obj.getClass())) {
			 return createArrayWrapper((A)obj);
		}
		if(Iterable.class.isAssignableFrom(obj.getClass())) {
			return ((Iterable)obj);
		} 
		throw new JsonPathException("Cannot iterate over " + obj!=null?obj.getClass().getName():"null");
	}

	@Override
	public Collection<String> getPropertyKeys(Object obj) {
		if(instanceOfObject(obj.getClass())) {
			return  getPropertyKeys(createObjectWrapper((O)obj));
		}
		if(JsonObjectWrapper.class.isAssignableFrom(obj.getClass()) || Map.class.isAssignableFrom(obj.getClass())) {
			return ((Map)obj).keySet();
		}
		if(isArray(obj)) {
			throw new UnsupportedOperationException();
	    }
		throw new JsonPathException("Cannot get property keys using '" + obj!=null?obj.getClass().getName()+"'":"null'");
	}

	@Override
	public Object getArrayIndex(Object obj, int idx, boolean unwrap) {
		return unwrap(getArrayIndex(obj,idx));
	}
	
	@Override
	public Object getArrayIndex(Object obj, int idx) {
		if(instanceOfArray(obj.getClass())) {
			return getArrayIndex(createArrayWrapper((A) obj), idx);
		}
		if(JsonArrayWrapper.class.isAssignableFrom(obj.getClass()) || List.class.isAssignableFrom(obj.getClass())) {
			if(idx >= ((List)obj).size()) { 
				throw new IndexOutOfBoundsException(String.format("index %s - size %s", idx, ((List)obj).size())); 
			}
			Object o = ((List)obj).get(idx);
			
			if(o!=null && instanceOfObject(o.getClass())) {
				return createObjectWrapper((O)o);
			}
			if(o!=null && instanceOfArray(o.getClass())) {
				return createArrayWrapper((A)o);
			}
			if(o != null && o.equals(null)) {
				return null;
			}
			return o;
		}
		throw new JsonPathException(obj!=null?obj.getClass().getName():"null");
	}

	@SuppressWarnings("unchecked")
	@Override
	public void setArrayIndex(Object array, int idx, Object newValue) {
		if(instanceOfArray(array.getClass())) {
			 setArrayIndex(createArrayWrapper((A)array), idx, newValue);
			 return;
		}
		if(JsonArrayWrapper.class.isAssignableFrom(array.getClass()) || List.class.isAssignableFrom(array.getClass())) {
			if(newValue != null &&  Map.class.isAssignableFrom(newValue.getClass())){
				((List)array).set(idx, createObjectWrapper((Map)newValue));
				return;
			}
			if(newValue != null && List.class.isAssignableFrom(newValue.getClass())){
				((List)array).set(idx, createArrayWrapper((List)newValue));
				return;
			}
			if(idx >= ((List)array).size()) {
				while(idx > ((List)array).size()) {
					((List)array).add(null);
				}
				((List)array).add(newValue);
				
			} else {
				((List)array).set(idx, newValue);
			}
			return;
		}
		throw new JsonPathException(array!=null?array.getClass().getName():"null");
	}

	@Override
	public Object getMapValue(Object obj, String key) {
		if(instanceOfObject(obj.getClass())) {
			 return getMapValue(createObjectWrapper((O)obj), key);
		}
		if(JsonObjectWrapper.class.isAssignableFrom(obj.getClass()) || Map.class.isAssignableFrom(obj.getClass())) {
			if(!((Map)obj).containsKey(key)) {
				return JsonProvider.UNDEFINED;
			}
			Object o = ((Map)obj).get(key);
			if(o!=null && instanceOfObject(o.getClass())) {
				return createObjectWrapper((O)o);
			}
			if(o!=null && instanceOfArray(o.getClass())) {
				return createArrayWrapper((A)o);
			}
			if(o != null && o.equals(null)) {
				o = null;
			}
			return o;
		}
		throw new JsonPathException(obj!=null?obj.getClass().getName():"null");
	}

	@Override
	public void setProperty(Object obj, Object key, Object value) {
		if(instanceOfObject(obj.getClass())) {
			setProperty(createObjectWrapper((O)obj), key, value);
			return;
		}
		if(JsonObjectWrapper.class.isAssignableFrom(obj.getClass()) ||Map.class.isAssignableFrom(obj.getClass())) {
			if(value != null &&  Map.class.isAssignableFrom(value.getClass())){
				((Map)obj).put(key, createObjectWrapper((Map)value));
				return;
			}
			if(value != null && List.class.isAssignableFrom(value.getClass())){
				((Map)obj).put(key, createArrayWrapper((List)value));
				return;
			}
			((Map)obj).put(key, value);
			return;
		}
		throw new JsonPathException(obj!=null?obj.getClass().getName():"null");
	}

	@Override
	public void removeProperty(Object obj, Object key) {
		if(instanceOfObject(obj.getClass())) {
			 removeProperty(createObjectWrapper((O)obj), key);
			 return;
		}
		if(instanceOfArray(obj.getClass())) {
			 removeProperty(createArrayWrapper((A)obj), key);
			 return;
		}
		if(JsonObjectWrapper.class.isAssignableFrom(obj.getClass())|| Map.class.isAssignableFrom(obj.getClass())) {
			((Map)obj).remove(key);
			 return;
		}
		if(JsonArrayWrapper.class.isAssignableFrom(obj.getClass()) || List.class.isAssignableFrom(obj.getClass())) {
            int index = key instanceof Integer ? ((Integer) key).intValue() : Integer.parseInt(key.toString());
			((List)obj).remove(index);
		    return;
		}
		throw new JsonPathException(obj!=null?obj.getClass().getName():"null");
	}

	@Override
	public boolean isMap(Object obj) {
		return obj!=null && (obj instanceof Map || instanceOfObject(obj.getClass()));
	}
}
