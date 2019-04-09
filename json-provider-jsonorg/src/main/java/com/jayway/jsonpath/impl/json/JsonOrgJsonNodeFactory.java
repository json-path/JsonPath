package com.jayway.jsonpath.impl.json;

import org.json.JSONTokener;

import com.jayway.jsonpath.internal.filter.JsonNodeFactory;
import com.jayway.jsonpath.internal.filter.ValueNodes.JsonNode;

public class JsonOrgJsonNodeFactory implements JsonNodeFactory {	
	
	@Override
	public boolean handle(String implementation) {
		return JsonOrgJsonProvider.IMPLEMENTATION.equals(implementation);
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
                new JSONTokener(str).nextValue();
                return true;
            } catch(Exception e){
                return false;
            }
        }
        return false;
	}

	@Override
	public JsonNode newInstance(CharSequence charSequence) {
		return new JsonOrgJsonNode(charSequence);
	}

	@Override
	public JsonNode newInstance(Object object) {
		return new JsonOrgJsonNode(object);
	}
}