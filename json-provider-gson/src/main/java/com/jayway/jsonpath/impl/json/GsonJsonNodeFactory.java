package com.jayway.jsonpath.impl.json;

import com.google.gson.JsonParser;
import com.jayway.jsonpath.internal.filter.JsonNodeFactory;
import com.jayway.jsonpath.internal.filter.ValueNodes.JsonNode;


public class GsonJsonNodeFactory implements JsonNodeFactory {

    private final JsonParser parser = new JsonParser();
    
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
              parser.parse(str);
              return true;
          } catch(Exception e){
              return false;
          }
      }
      return false;
	}

	@Override
	public JsonNode newInstance(CharSequence charSequence) {
		return new GsonJsonNode(charSequence);
	}

	@Override
	public JsonNode newInstance(Object object) {
		return new GsonJsonNode(object);
	}

	@Override
	public boolean handle(String implementation) {
		return GsonJsonProvider.IMPLEMENTATION.equals(implementation) ;
	}

}
