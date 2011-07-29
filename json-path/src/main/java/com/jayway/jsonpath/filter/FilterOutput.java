package com.jayway.jsonpath.filter;

import java.util.ArrayList;
import java.util.List;

import com.jayway.jsonpath.json.JsonArray;
import com.jayway.jsonpath.json.JsonElement;
import com.jayway.jsonpath.json.JsonException;
import com.jayway.jsonpath.json.JsonFactory;

import static java.lang.String.format;

/**
 * User: kalle stenflo
 * Date: 2/9/11
 * Time: 12:28 PM
 */
public class FilterOutput {

    private final List<JsonElement> result;
    
    public FilterOutput(JsonElement root) {
    	this.result = new ArrayList<JsonElement>();
        result.add(root);
    }


    public FilterOutput(List<JsonElement> result) {
    	this.result = result;
	}


	public FilterOutput() {
		this.result = new ArrayList<JsonElement>();
	}


	public JsonElement getResult() throws JsonException {
		if(result.size()==0){
			return null;
		}
		else if(result.size()==1){
			return result.get(0);
    	}
		else{
    		JsonFactory fact = JsonFactory.getInstance();
    		JsonArray ja = fact.createJsonArray();
    		for(JsonElement ele:result)
    			ja.add(ele);
    		return ja;
		}
    }
	
	public JsonArray getResultAsList() throws JsonException {
		return getResult().toJsonArray();
	}
	
   

    public List<JsonElement> getList() throws JsonException {
    	return result;
    }



}
