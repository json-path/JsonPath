package com.jayway.jsonpath.impl.json;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.jayway.jsonpath.Predicate;
import com.jayway.jsonpath.internal.filter.ValueNodes.JsonNode;


public class JsonOrgJsonNode extends JsonNode{
	
	/**
	 * @param charSequence
	 */
	JsonOrgJsonNode(CharSequence charSequence){
		super(charSequence);
	}

	/**
	 * @param parsedJson
	 */
	JsonOrgJsonNode(Object parsedJson){
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
	        	json = new JSONTokener(json.toString().trim()).nextValue();
	        	parsed = true;
			}
			catch (JSONException e){
				throw new IllegalArgumentException(e);
			}
		}
		if(JSONArray.class.isAssignableFrom(json.getClass())) {
			return new JsonOrgJsonArrayWrapper((JSONArray)json);
		}
        if(JSONObject.class.isAssignableFrom(json.getClass())) {
			return new JsonOrgJsonObjectWrapper((JSONObject)json);
        }
		return json;
	}
}