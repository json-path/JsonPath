package com.jayway.jsonpath.filter;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import com.jayway.jsonpath.json.JsonElement;
import com.jayway.jsonpath.json.JsonException;

/**
 * Created by IntelliJ IDEA.
 * User: kallestenflo
 * Date: 2/15/11
 * Time: 8:09 PM
 */
public class ListWildcardFilter extends JsonPathFilterBase{

    public static final Pattern PATTERN = Pattern.compile("\\[\\*\\]");

    @Override
	public List<JsonElement> apply(JsonElement element) throws JsonException {
		List<JsonElement> result = new ArrayList<JsonElement>();
		if(element.isJsonArray()){
			for(JsonElement ele : element.toJsonArray()){
				result.add(ele);
			}
		}

		
		return result;
	}

	@Override
	public String getPathSegment() throws JsonException {
		return null;
	}
}
