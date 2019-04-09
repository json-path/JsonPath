package com.jayway.jsonpath.impl.json;

import java.util.List;
import java.util.ListIterator;

import org.json.JSONArray;
import org.json.JSONObject;

import com.jayway.jsonpath.spi.json.JsonArrayWrapper;
import com.jayway.jsonpath.spi.json.JsonProvider;

public class JsonOrgJsonArrayWrapper extends JsonArrayWrapper<JSONArray> {

	public JsonOrgJsonArrayWrapper(JSONArray instance) {
		super(instance, JSONObject.class, JSONArray.class, JsonOrgJsonObjectWrapper.class, JsonOrgJsonArrayWrapper.class );
	}

	@Override
	public int size() {
		return super.instance.length();
	}

	@Override
	public Object get(int index) {		
		return super.instance.get(index);
	}

	@Override
	public Object doSet(int index, Object element) {
		Object o = null;
		try {
			o = get(index);
		} catch(Exception e) {}
		if(element == null || element == JsonProvider.UNDEFINED) {
			super.instance.put(index, JSONObject.NULL);
		} else {
		    super.instance.put(index, element) ;
		}
		return o;
	}

	@Override
	public void doAdd(int index, Object element) {
		if(element == null || element == JsonProvider.UNDEFINED) {
			super.instance.put(index, JSONObject.NULL);
		} else {
		    super.instance.put(index, element) ;
		}
	}

	@Override
	public Object remove(int index) {
		 Object o = super.instance.opt(index);
		 if(o!=null) {
			 super.instance.remove(index);
		 }
		 return o;
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
            try {
	        	if (o1!=null && objectClass.isAssignableFrom(o1.getClass())) {
					effectiveValue =  objectWrapperClass.getConstructor(objectClass).newInstance(o1);        				
	    		} else if (o1!=null &&  arrayClass.isAssignableFrom(o1.getClass())) {
	    			effectiveValue =  arrayWrapperClass.getConstructor(arrayClass).newInstance(o1);        		
	    		}
			} catch(Exception ex) {
				return false;
			}
            if (!(effectiveValue==null ? o2==null : effectiveValue.equals(o2)))
                return false;
        }
        return !(e1.hasNext() || e2.hasNext());
	}
}
