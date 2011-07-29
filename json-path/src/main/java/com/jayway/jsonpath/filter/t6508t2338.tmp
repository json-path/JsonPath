package com.jayway.jsonpath.filter;


import com.jayway.jsonpath.json.JsonArray;
import com.jayway.jsonpath.json.JsonElement;
import com.jayway.jsonpath.json.JsonException;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.minidev.json.JSONValue;

/**
 * Created by IntelliJ IDEA.
 * User: kallestenflo
 * Date: 2/15/11
 * Time: 8:23 PM
 */
public class TypeFilter extends JsonPathFilterBase {

	//@Autowired
	public com.jayway.jsonpath.json.JsonFactory factory = com.jayway.jsonpath.json.minidev.MiniJsonFactory.getInstance();	
	
    public static final Pattern PATTERN = Pattern.compile("\\((collection|object|value)\\)");
    		
    private final String pathFragment;

        
    public TypeFilter(String pathFragment) {
        this.pathFragment = pathFragment;
    }
    @Override
	public String getPathSegment() throws JsonException {
		return pathFragment;
	}
    @Override
    public List<JsonElement> apply(JsonElement element) throws JsonException {
    	List<JsonElement> result = new ArrayList<JsonElement>();

        String prop = getFilterProperty();
        JsonType v = JsonType.fromString(pathFragment.substring(1,pathFragment.length()-1));

    	if(element.isJsonType(v))
    		result.add(element);
    		
        
        return result;
    }


    private String getFilterProperty() {
        Matcher matcher = PATTERN.matcher(pathFragment);
        if (matcher.matches()) {
            return matcher.group(1);
        }
        throw new IllegalArgumentException("invalid list filter property");
    }
}
