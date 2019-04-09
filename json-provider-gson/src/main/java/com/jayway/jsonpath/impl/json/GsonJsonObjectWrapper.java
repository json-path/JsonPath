package com.jayway.jsonpath.impl.json;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.jayway.jsonpath.spi.json.JsonObjectWrapper;

public class GsonJsonObjectWrapper extends JsonObjectWrapper<JsonObject> {

	private Gson gson;

	public GsonJsonObjectWrapper(JsonObject instance) {
		super(instance, JsonObject.class, JsonArray.class,  GsonJsonObjectWrapper.class, GsonJsonArrayWrapper.class );
		this.gson = new Gson();
	}

	@Override
	public int size() {
		return entrySet().size();
	}

	@Override
	public Iterator iterator() {
		return new Iterator() {

			Iterator iterator = entrySet().iterator();
			
			@Override
			public boolean hasNext() {
				return iterator.hasNext();
			}

			@Override
			public Object next() {
				final Entry entry = (Entry) iterator.next();						
				return new Entry() {

					@Override
					public Object getKey() {
						return entry.getKey();
					}

					@Override
					public Object getValue() {
						return GsonJsonProvider.fromJsonElement((JsonElement) entry.getValue()); 
					}

					@Override
					public Object setValue(Object value) {
						Object o = entry.getValue();
						entry.setValue(GsonJsonProvider.toJsonElement(value, gson));
						return o;
					}
					
				};
			}
			
		};
	}

	@Override
	public Object get(Object key) {
		Object o = super.instance.get(key.toString());
		return GsonJsonProvider.fromJsonElement((JsonElement) o);
	}

	@Override
	public Object remove(Object key) {
		Object o = super.instance.remove(key.toString());
		return GsonJsonProvider.fromJsonElement((JsonElement) o);
	}
	
	@Override
	public Set keySet() {
		Set keys = new HashSet();
		for(Iterator iterator = super.instance.entrySet().iterator();iterator.hasNext();) {
			keys.add(((Entry)iterator.next()).getKey());
		}
		return keys;
	}

	@Override
	public Collection values() {
		Set values = new HashSet();
		for(Iterator iterator = super.instance.entrySet().iterator();iterator.hasNext();) {
			values.add(((Entry)iterator.next()).getValue());
		}
		return values;
	}

	@Override
	public Set entrySet() {
		return super.instance.entrySet();
	}

	@Override
	protected Object doPut(Object key, Object value) {
		Object o = this.instance.get(key.toString());
		super.instance.add(key.toString(), GsonJsonProvider.toJsonElement(value, gson));
		return GsonJsonProvider.fromJsonElement((JsonElement) o);
	}

	public boolean equals(Object o) {	
		if(o == null) {
			return false;
		}
		if (o == this) {
            return true;
		}
		if(objectClass.isAssignableFrom(o.getClass())) {
			try {
				return this.equals(objectWrapperClass.getConstructor(objectClass).newInstance(o));
			}catch(Exception e) {
				return false;
			}
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
            	if(value!=null && JsonElement.class.isAssignableFrom(value.getClass())) {
        			effectiveValue = GsonJsonProvider.fromJsonElement((JsonElement)value);
        		}
            	Object compare = map.get(key);
                if ((effectiveValue == null && compare!=null) || (effectiveValue!=null && !effectiveValue.equals(compare))){
                    return false;
                }
            }
        } catch (Exception unused) {
            return false;
        }
        return true;
	}
}
