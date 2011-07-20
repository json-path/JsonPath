package com.jayway.jsonpath.filter;


import com.jayway.jsonpath.eval.ExpressionEvaluator;
import com.jayway.jsonpath.json.JsonArray;
import com.jayway.jsonpath.json.JsonElement;
import com.jayway.jsonpath.json.JsonException;
import com.jayway.jsonpath.json.JsonFactory;
import com.jayway.jsonpath.json.JsonObject;



import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by IntelliJ IDEA.
 * User: kallestenflo
 * Date: 2/15/11
 * Time: 8:27 PM
 */
public class ListEvalFilter extends JsonPathFilterBase {

	//@Autowired
	public JsonFactory factory = com.jayway.jsonpath.json.minidev.MiniJsonFactory.getInstance();

    public static final Pattern PATTERN = Pattern.compile("\\[\\s?\\?\\s?\\(\\s?@.(\\w+)\\s?([=<>]+)\\s?(.*)\\s?\\)\\s?\\]");    //[?( @.title< 'ko')]

    private final String pathFragment;

    public ListEvalFilter(String pathFragment) {
        this.pathFragment = pathFragment;
    }


    @Override
    public List<JsonElement> apply(JsonElement element) throws JsonException {
    	List<JsonElement> result = new ArrayList<JsonElement>();
    	
    	if(element.isJsonArray()){
    		for(JsonElement subElement: element.toJsonArray()){
    			if (isMatch(subElement)) {
    				result.add(subElement);
    			}
    		}
    	}
    	
    	return result;
        
    }

    private boolean isMatch(JsonElement check) throws JsonException {
        Matcher matcher = PATTERN.matcher(pathFragment);

        if (matcher.matches()) {
            String property = matcher.group(1);
            String operator = matcher.group(2);
            String expected = matcher.group(3);

            if (!check.isJsonObject()) {
                return false;
            }
            
            JsonObject obj = check.toJsonObject();

            if (!obj.hasProperty(property)) {
                return false;
            }

            JsonElement propertyValue = obj.getProperty(property);

            if (propertyValue.isContainer()) {
                return false;
            }

            String expression = propertyValue.toObject() + " " + operator + " " + expected;

            return ExpressionEvaluator.eval(propertyValue.toObject(), operator, expected);

        }
        return false;
    }


    @Override
	public String getPathSegment() throws JsonException {
		return pathFragment;
	}
}
