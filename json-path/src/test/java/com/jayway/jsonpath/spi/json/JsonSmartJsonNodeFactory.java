package com.jayway.jsonpath.spi.json;

import com.jayway.jsonpath.internal.filter.JsonNodeFactory;
import com.jayway.jsonpath.internal.filter.ValueNodes.JsonNode;

import net.minidev.json.parser.JSONParser;

public class JsonSmartJsonNodeFactory implements JsonNodeFactory {

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
              new JSONParser(JSONParser.MODE_PERMISSIVE).parse(str);
              return true;
          } catch(Exception e){
              return false;
          }
      }
      return false;
	}

	@Override
	public JsonNode newInstance(CharSequence charSequence) {
		return new JsonSmartJsonNode(charSequence);
	}

	@Override
	public JsonNode newInstance(Object object) {
		return new JsonSmartJsonNode(object);
	}

	@Override
	public boolean handle(String implementation) {
		return "SMART".equals(implementation);
	}

}
