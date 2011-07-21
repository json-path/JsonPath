package com.jayway.jsonpath.filter;

import java.util.ArrayList;
import java.util.List;

import com.jayway.jsonpath.json.JsonArray;
import com.jayway.jsonpath.json.JsonElement;
import com.jayway.jsonpath.json.JsonException;
import com.jayway.jsonpath.json.JsonFactory;
import com.jayway.jsonpath.json.JsonPathResultList;

import static java.lang.String.format;

/**
 * User: kalle stenflo
 * Date: 2/9/11
 * Time: 12:28 PM
 */
public class FilterOutput extends ArrayList<JsonElement> {

    
    public FilterOutput(JsonElement root) {
    	super();
    	this.add(root);
    }
    public FilterOutput(){
    	super();
    }

	
	public JsonArray getResultAsJsonArray() throws JsonException {
		return this.get(0).toJsonArray();
	}
	
	public JsonElement getResultAsJson() throws JsonException {
		if(this.size()>1){
			JsonArray ja = JsonFactory.getInstance().createJsonArray();
			for(JsonElement je:this){
				ja.add(je);
			}
			return ja;
		}
		else if(this.size()==1){
			return this.get(0);
		}
		else{
			return JsonFactory.getInstance().createJsonNull(null, null);
		}
	}
		
	



}
