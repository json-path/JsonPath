package com.jayway.jsonpath.filter;


import com.jayway.jsonpath.json.JsonArray;
import com.jayway.jsonpath.json.JsonElement;
import com.jayway.jsonpath.json.JsonException;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by IntelliJ IDEA.
 * User: kallestenflo
 * Date: 2/15/11
 * Time: 8:23 PM
 */
public class ListPropertyFilter extends JsonPathFilterBase {

	//@Autowired
	public com.jayway.jsonpath.json.JsonFactory factory = com.jayway.jsonpath.json.minidev.MiniJsonFactory.getInstance();	
	
    public static final Pattern PATTERN = Pattern.compile("\\[\\s?\\?\\s?\\(\\s?@\\.(\\w+)\\s?\\)\\s?\\]");  //[?(@.title)]

    private final String pathFragment;

    public ListPropertyFilter(String pathFragment) {
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

        if(element.isJsonArray()){
        	for(JsonElement subElement : element.toJsonArray()){
        		if (subElement.isJsonObject()) {
        			if (subElement.toJsonObject().hasProperty(prop)) {
        				result.add(subElement);
        			}
        		}
        	}
        }
        
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
