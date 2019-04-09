package com.jayway.jsonpath.impl.json;

import java.util.List;
import java.util.ListIterator;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.jayway.jsonpath.spi.json.JsonArrayWrapper;
import com.jayway.jsonpath.spi.json.JsonProvider;

public class GsonJsonArrayWrapper extends JsonArrayWrapper<JsonArray> {

	private final Gson gson;
	
	public GsonJsonArrayWrapper(JsonArray instance) {
		super(instance, JsonObject.class, JsonArray.class, GsonJsonObjectWrapper.class, GsonJsonArrayWrapper.class );
		this.gson = new Gson();
	}

	@Override
	public int size() {
		return super.instance.size();
	}

	@Override
	public Object get(int index) {		
		if(index >= size()) {
			throw new IndexOutOfBoundsException();
		}
		Object o = super.instance.get(index);
		return GsonJsonProvider.fromJsonElement((JsonElement)o);
	}

	@Override
	public Object doSet(int index, Object element) {
		Object o = null;
		try {
			o = get(index);
			super.instance.set(index, GsonJsonProvider.toJsonElement(element, gson));
		}catch(IndexOutOfBoundsException e) {
			super.instance.add(GsonJsonProvider.toJsonElement(element, gson));
			o = JsonProvider.UNDEFINED;
		}
		return o;
	}

	@Override
	public void doAdd(int index, Object element) {
		if(index >= size()) {
			while(size() < (index-1)) {
				super.instance.add(JsonNull.INSTANCE);
			}
			super.instance.add(GsonJsonProvider.toJsonElement(element, gson));
		} else {
			super.instance.set(index, GsonJsonProvider.toJsonElement(element, gson));
		}
	}

	@Override
	public Object remove(int index) {
		 Object o = super.instance.remove(index);
		 return GsonJsonProvider.fromJsonElement((JsonElement)o);
	}
	
	public boolean equals(Object o){
		if(o == null) {
			return false;
		}
		if (o == this) {
            return true;
		}
		if(arrayClass.isAssignableFrom(o.getClass())) {
			try {
				return this.equals(arrayWrapperClass.getConstructor(arrayClass).newInstance(o));
			} catch(Exception e) {
				return false;
			}
		}
        if (!(o instanceof List))
            return false;

        ListIterator e1 = listIterator();
        ListIterator e2 = ((List) o).listIterator();
        while (e1.hasNext() && e2.hasNext()) {
            Object o1 = e1.next();
            Object o2 = e2.next();            
            Object effectiveValue = o1; 
            if(o1 != null && JsonElement.class.isAssignableFrom(o1.getClass())) {
        	    effectiveValue = GsonJsonProvider.fromJsonElement((JsonElement)o1);
        	}
            if (!(effectiveValue==null ? o2==null : effectiveValue.equals(o2)))
                return false;
        }
        return !(e1.hasNext() || e2.hasNext());
	}
}
