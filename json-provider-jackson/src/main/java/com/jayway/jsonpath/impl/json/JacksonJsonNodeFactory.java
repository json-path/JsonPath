package com.jayway.jsonpath.impl.json;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.internal.filter.JsonNodeFactory;
import com.jayway.jsonpath.internal.filter.ValueNodes.JsonNode;

public class JacksonJsonNodeFactory implements JsonNodeFactory {

	private final ObjectMapper mapper;
	
	public JacksonJsonNodeFactory() {
		this.mapper = new ObjectMapper();
		this.mapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
    	this.mapper.configure(JsonParser.Feature.ALLOW_BACKSLASH_ESCAPING_ANY_CHARACTER, true);
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
    	  try{ 
    	        mapper.reader().readTree(str);
    	        return true;
    	    } catch(IOException e){
    	        return false;
    	    }
      }
      return false;
	}

	@Override
	public JsonNode newInstance(CharSequence charSequence) {
		return new JacksonJsonNode(charSequence);
	}

	@Override
	public JsonNode newInstance(Object object) {
		return new JacksonJsonNode(object);
	}

	@Override
	public boolean handle(String implementation) {
		return JacksonJsonProvider.IMPLEMENTATION.equals(implementation);
	}

}
