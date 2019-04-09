package com.jayway.jsonpath.impl.json;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.codehaus.jettison.json.JSONTokener;

import com.jayway.jsonpath.Predicate;
import com.jayway.jsonpath.internal.filter.ValueNodes.JsonNode;

public class JettisonJsonNode extends JsonNode{
	
	/**
	 * @param charSequence
	 */
	JettisonJsonNode(CharSequence charSequence){
		super(charSequence);
	}

	/**
	 * @param parsedJson
	 */
	JettisonJsonNode(Object parsedJson){
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
			return new JettisonJsonArrayWrapper((JSONArray)json);
		}
        if(JSONObject.class.isAssignableFrom(json.getClass())) {
			return new JettisonJsonObjectWrapper((JSONObject)json);
        }
		return json;
	}
}