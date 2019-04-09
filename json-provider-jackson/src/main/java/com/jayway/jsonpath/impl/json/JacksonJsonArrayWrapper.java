package com.jayway.jsonpath.impl.json;

import java.util.List;
import java.util.ListIterator;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import com.jayway.jsonpath.spi.json.JsonArrayWrapper;
import com.jayway.jsonpath.spi.json.JsonProvider;

public class JacksonJsonArrayWrapper extends JsonArrayWrapper<ArrayNode> {
	
	private ObjectMapper objectMapper;
		
	public JacksonJsonArrayWrapper(ArrayNode instance) {
		super(instance, ObjectNode.class, ArrayNode.class, JacksonJsonObjectWrapper.class, JacksonJsonArrayWrapper.class );
		this.objectMapper = new ObjectMapper();
    	this.objectMapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
    	this.objectMapper.configure(JsonParser.Feature.ALLOW_BACKSLASH_ESCAPING_ANY_CHARACTER, true);
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
		return JacksonJsonProvider.fromJsonNode(o);
	}

	@Override
	public Object doSet(int index, Object element) {
		Object o = null;
		try {
			o = get(index);
			if(element == null || element == JsonProvider.UNDEFINED) {
				super.instance.set(index, NullNode.instance);
			} else {
			    super.instance.set(index, JacksonJsonProvider.toJsonNode(element, objectMapper));
			}
		} catch(IndexOutOfBoundsException e) {
			while(size() < (index-1)) {
			    super.instance.add(NullNode.instance);
			}
			if(element == null || element == JsonProvider.UNDEFINED) {
				super.instance.add( NullNode.instance);
			} else {
			    super.instance.add(JacksonJsonProvider.toJsonNode(element, objectMapper));
			}
		}
		return o;
	}

	@Override
	public void doAdd(int index, Object element) {
		if(index >= size()) {
			while(size() < (index-1)) {
				super.instance.add(NullNode.instance);
			}
			if(element == null || element == JsonProvider.UNDEFINED) {
				super.instance.add( NullNode.instance);
			} else {
			    super.instance.add(JacksonJsonProvider.toJsonNode(element, objectMapper));
			}
		} else {
			if(element == null || element == JsonProvider.UNDEFINED) {
				super.instance.insert(index, NullNode.instance);
			} else {
			    super.instance.insert(index, JacksonJsonProvider.toJsonNode(element, objectMapper));
			}
		}
	}

	@Override
	public Object remove(int index) {
		Object o = super.instance.remove(index);
		return JacksonJsonProvider.fromJsonNode(o);
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
            Object effectiveValue = JacksonJsonProvider.fromJsonNode(o1); 
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
