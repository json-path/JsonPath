package com.jayway.jsonpath.impl.json;


import org.apache.tapestry5.json.TapestryJsonTokener;

import com.jayway.jsonpath.internal.filter.JsonNodeFactory;
import com.jayway.jsonpath.internal.filter.ValueNodes.JsonNode;

public class TapestryJsonNodeFactory implements JsonNodeFactory {	
	
	@Override
	public boolean handle(String implementation) {
		return TapestryJsonProvider.IMPLEMENTATION.equals(implementation);
	}

	@Override
	public boolean isJson(Object o) {
		if(o == null || !(o instanceof String)){
            return false;
        }
        String str = o.toString().trim();
        if (str.length() <= 1) {
            return false;
        }
        char c0 = str.charAt(0);
        char c1 = str.charAt(str.length() - 1);
        if ((c0 == '[' && c1 == ']') || (c0 == '{' && c1 == '}')){
            try {
                new TapestryJsonTokener(str).nextValue();
                return true;
            } catch(Exception e){
                return false;
            }
        }
        return false;
	}

	@Override
	public JsonNode newInstance(CharSequence charSequence) {
		return new TapestryJsonNode(charSequence);
	}

	@Override
	public JsonNode newInstance(Object object) {
		return new TapestryJsonNode(object);
	}
}