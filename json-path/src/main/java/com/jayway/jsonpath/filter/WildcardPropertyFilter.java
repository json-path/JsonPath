package com.jayway.jsonpath.filter;


import com.jayway.jsonpath.json.JsonArray;
import com.jayway.jsonpath.json.JsonElement;
import com.jayway.jsonpath.json.JsonException;
import com.jayway.jsonpath.json.JsonFactory;


import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by IntelliJ IDEA.
 * User: kallestenflo
 * Date: 2/15/11
 * Time: 8:42 PM
 */
public class WildcardPropertyFilter extends JsonPathFilterBase {

    public final static Pattern PATTERN = Pattern.compile("\\*");
    private JsonFactory factory = com.jayway.jsonpath.json.minidev.MiniJsonFactory.getInstance();

    @Override
	public String getPathSegment() throws JsonException {
		return null;
	}

    @Override
	public List<JsonElement> apply(JsonElement element) throws JsonException {
		List<JsonElement> result = new ArrayList<JsonElement>();

        if (element.isJsonArray()) {
            for (JsonElement current : element.toJsonArray()) {
                for (JsonElement value : current.toJsonObject().getProperties()) {
                    result.add(value);
                }
            }
        } else {
            for (JsonElement value : element.toJsonObject().getProperties()){
                result.add(value);
            }
        }
        return result;

    }
}
