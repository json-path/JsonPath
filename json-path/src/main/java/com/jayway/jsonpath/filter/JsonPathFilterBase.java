package com.jayway.jsonpath.filter;

import java.util.ArrayList;
import java.util.List;

import com.jayway.jsonpath.json.JsonElement;
import com.jayway.jsonpath.json.JsonException;

/**
 * Created by IntelliJ IDEA.
 * User: kallestenflo
 * Date: 2/2/11
 * Time: 2:01 PM
 */
public abstract class JsonPathFilterBase {
	public FilterOutput apply(FilterOutput element) throws JsonException{
		List<JsonElement> result = new ArrayList<JsonElement>();
		for(JsonElement el : element.getList()){
    		List<JsonElement> out = apply(el);
    		if(out != null)
    			result.addAll(out);
    	}
		return new FilterOutput(result);
    }
	public abstract List<JsonElement> apply(JsonElement element) throws JsonException;
    public abstract String getPathSegment() throws JsonException;;

}
