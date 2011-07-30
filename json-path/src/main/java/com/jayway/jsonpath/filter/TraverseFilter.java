package com.jayway.jsonpath.filter;

import com.jayway.jsonpath.json.JsonArray;
import com.jayway.jsonpath.json.JsonElement;
import com.jayway.jsonpath.json.JsonException;
import com.jayway.jsonpath.json.JsonFactory;
import com.jayway.jsonpath.json.JsonObject;


import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by IntelliJ IDEA.
 * User: kallestenflo
 * Date: 2/2/11
 * Time: 2:33 PM
 */
public class TraverseFilter extends JsonPathFilterBase {

    public final static Pattern PATTERN = Pattern.compile("\\.\\.");
	

	public List<JsonElement> apply(JsonElement element) throws JsonException {
    	List<JsonElement> result = new ArrayList<JsonElement>();
        traverse(element, result);
        return result;
    }

    private void traverse(JsonElement container, List<JsonElement> result) throws JsonException {

        if (container.isJsonObject()) {
            result.add(container);

            for (JsonElement value : container.toJsonObject().getProperties()) {
                if (value.isContainer()) {
                    traverse(value, result);
                }
            }
        } else if (container.isJsonArray()) {

            for (JsonElement value : container.toJsonArray()) {
                if ( value.isContainer()) {
                    traverse(value, result);
                }
            }
        }
    }

	@Override
	public String getPathSegment() throws JsonException {

		return null;
	}

}