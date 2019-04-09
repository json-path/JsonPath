package com.jayway.jsonpath.impl.json;


import org.apache.tapestry5.json.JSONArray;
import org.apache.tapestry5.json.JSONObject;
import org.apache.tapestry5.json.TapestryJsonTokener;

import com.jayway.jsonpath.Predicate;
import com.jayway.jsonpath.internal.filter.ValueNodes.JsonNode;


public class TapestryJsonNode extends JsonNode{
	
	/**
	 * @param charSequence
	 */
	TapestryJsonNode(CharSequence charSequence){
		super(charSequence);
	}

	/**
	 * @param parsedJson
	 */
	TapestryJsonNode(Object parsedJson){
		super(parsedJson);
	}

	/**
	 * @inheritDoc
	 *
	 * @see com.jayway.jsonpath.internal.filter.JsonNode#parse(com.jayway.jsonpath.Predicate.PredicateContext)
	 */
	@Override
	public Object parse(Predicate.PredicateContext ctx){
		if(!parsed){
	        try	{
	        	json = new TapestryJsonTokener(json.toString().trim()).nextValue();
	        	parsed = true;
			}
			catch (Exception e){
				throw new IllegalArgumentException(e);
			}
		}
		if(JSONArray.class.isAssignableFrom(json.getClass())) {
			return new TapestryJsonArrayWrapper((JSONArray)json);
		}
        if(JSONObject.class.isAssignableFrom(json.getClass())) {
			return new TapestryJsonObjectWrapper((JSONObject)json);
        }
		return json;
	}
}