package com.jayway.jsonpath.impl.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.jayway.jsonpath.InvalidJsonException;
import com.jayway.jsonpath.Predicate;
import com.jayway.jsonpath.internal.filter.ValueNodes.JsonNode;


public class JacksonJsonNode extends JsonNode {

	private ObjectMapper objectMapper;
	
	public JacksonJsonNode(CharSequence charSequence) {
		super(charSequence);
		initMapper();
	}

	public JacksonJsonNode(Object object) {
		super(object);
		initMapper();
	}

	private void initMapper() {
		this.objectMapper = new ObjectMapper();
    	this.objectMapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
    	this.objectMapper.configure(JsonParser.Feature.ALLOW_BACKSLASH_ESCAPING_ANY_CHARACTER, true);
	}
	
	@Override
	public Object parse(Predicate.PredicateContext ctx) {
		if(!parsed) {
		  try {
			  	json = json==null?null:objectMapper.readTree((String)json);
	            parsed = true;
	        } catch (Exception e) {
	            throw new InvalidJsonException(e, (String)json);
	        }
		}
		if(json!=null && ArrayNode.class.isAssignableFrom(json.getClass())) {
			return new JacksonJsonArrayWrapper((ArrayNode)json);
		}
        if(json!=null && ObjectNode.class.isAssignableFrom(json.getClass())) {
			return new JacksonJsonObjectWrapper((ObjectNode)json);
        }
		return JacksonJsonProvider.fromJsonNode(json);
	}
}
