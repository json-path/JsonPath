package com.jayway.jsonpath.filter;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import com.jayway.jsonpath.json.JsonElement;
import com.jayway.jsonpath.json.JsonException;

/**
 * Created by IntelliJ IDEA.
 * User: kallestenflo
 * Date: 2/2/11
 * Time: 2:31 PM
 */
public class RootFilter extends JsonPathFilterBase{

    public final static Pattern PATTERN = Pattern.compile("\\$");

	@Override
	public List<JsonElement> apply(JsonElement element) throws JsonException {
	    
    	List<JsonElement> result = new ArrayList<JsonElement>();
    	result.add(element);
    	return result;
    }
    
    @Override
	public String getPathSegment() throws JsonException {
		return null;
	}
}
