/**
 * 
 */
package com.jayway.jsonpath.spi.json;

import java.util.Iterator;
import java.util.Map;

/**
 *
 */
public abstract class JsonObjectWrapper<O> implements JsonWrapper<O>, Map {
	
	protected abstract Object doPut(Object key, Object value);
	
	protected final O instance;
	protected final Class<?> objectClass; 
	protected final Class<?> arrayClass;
	protected final Class<?> objectWrapperClass; 
	protected final Class<?> arrayWrapperClass;

	public JsonObjectWrapper(O instance, 
			Class<O> objectClass, 
			Class<?> arrayClass, 
			Class<?> objectWrapperClass, 
			Class<?> arrayWrapperClass ) {
		this.objectClass = objectClass;
		this.arrayClass = arrayClass;
		this.objectWrapperClass = objectWrapperClass;
		this.arrayWrapperClass = arrayWrapperClass;
		
		O objectInstance = instance;		
		if(objectInstance == null) {
			try {
				objectInstance = (O) this.objectClass.newInstance();
			} catch (InstantiationException | IllegalAccessException e) {
				throw new NullPointerException("No attached instance");
			}
		} else {
			objectInstance = instance;
		}
		this.instance = objectInstance;
	}
	
	@Override
	public boolean isEmpty() {
		return size() == 0;
	}

	@Override
	public boolean containsKey(Object key) {
		return keySet().contains(key);
	}

	@Override
	public boolean containsValue(Object value) {
		return values().contains(value);
	}

	@Override
	public Object put(Object key, Object value) {		
		if(value!= null && JsonWrapper.class.isAssignableFrom(value.getClass())) {
			return doPut(key, ((JsonWrapper)value).unwrap());		
		}
		return doPut(key, value);
	}

	@Override
	public void putAll(Map m) {
		Iterator<Entry> iterator =  m.entrySet().iterator();
		for(;iterator.hasNext();) {
			Entry entry = iterator.next();
			put(entry.getKey(),entry.getValue());
		}
	}

	@Override
	public void clear() {
		Object[] keys = keySet().toArray();
		for(Object key:keys) {
			remove(key);
		}
	}

	@Override
	public O unwrap() {
		return this.instance;
	}

	public String toString() {
		return this.instance.toString();
	}

	public boolean equals(Object o) {	
		if(o == null) {
			return false;
		}
		if (o == this) {
            return true;
		}
		if(objectClass.isAssignableFrom(o.getClass())) {
			return this.instance.equals(o);
		}
		if(!(o instanceof Map)) {
			return false;
		}
		Map map = (Map) o;
        try {
            Iterator<Entry> i = entrySet().iterator();
            while (i.hasNext()) {
                Entry e = i.next();
                Object key = e.getKey();
                Object value = e.getValue();
                
                if (value == null) {
                    if (!(map.get(key)==null && map.containsKey(key))) {
                        return false;
                    }
                    continue;
                }
                Object effectiveValue = value; 
            	if (objectClass.isAssignableFrom(value.getClass())) {
    				effectiveValue =  objectWrapperClass.getConstructor(objectClass).newInstance(value);        				
        		} else if (arrayClass.isAssignableFrom(value.getClass())) {
        			effectiveValue =  arrayWrapperClass.getConstructor(arrayClass).newInstance(value);        		
        		}
                if (!effectiveValue.equals(map.get(key))){
                    return false;
                }
            }
        } catch (Exception unused) {
            return false;
        }
        return true;
	}
}
