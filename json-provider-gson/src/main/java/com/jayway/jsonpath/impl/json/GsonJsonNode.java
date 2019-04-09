package com.jayway.jsonpath.impl.json;

import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.jayway.jsonpath.Predicate;
import com.jayway.jsonpath.internal.filter.ValueNodes.JsonNode;

public class GsonJsonNode extends JsonNode {

	public GsonJsonNode(CharSequence charSequence) {
		super(charSequence);
	}

	public GsonJsonNode(Object object) {
		super(object);
	}

	@Override
	public Object parse(Predicate.PredicateContext ctx) {			  
		if(!parsed){
		  try {
               JsonElement el = new JsonParser().parse(json.toString());
               json = el;
               parsed = true;
          } catch (JsonParseException e) {
               throw new IllegalArgumentException(e);
          }
		}
		if(JsonElement.class.isAssignableFrom(json.getClass())) {
			return GsonJsonProvider.fromJsonElement((JsonElement) json);
		}
		return json;
	}
}
