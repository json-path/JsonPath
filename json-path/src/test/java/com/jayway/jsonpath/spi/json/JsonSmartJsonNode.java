package com.jayway.jsonpath.spi.json;


import com.jayway.jsonpath.Predicate;
import com.jayway.jsonpath.internal.filter.ValueNodes.JsonNode;

import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;

public class JsonSmartJsonNode extends JsonNode {

	public JsonSmartJsonNode(CharSequence charSequence) {
		super(charSequence);
	}

	public JsonSmartJsonNode(Object object) {
		super(object);
	}

	@Override
	public Object parse(Predicate.PredicateContext ctx) {
		if(!parsed) {
		  try {
               json = new JSONParser(JSONParser.MODE_PERMISSIVE).parse(json.toString());
               parsed = true;
          } catch (ParseException e) {
               throw new IllegalArgumentException(e);
          }
		}
		return json;
	}

}
