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
 * Date: 2/2/11
 * Time: 2:32 PM
 */
public class PropertyFilter extends JsonPathFilterBase {

    public final static Pattern PATTERN = Pattern.compile("(.*)|\\['(.*?)'\\]");


    private final String pathFragment;


    private JsonFactory factory = com.jayway.jsonpath.json.minidev.MiniJsonFactory.getInstance();

    public PropertyFilter(String pathFragment) {
        this.pathFragment = pathFragment;
    }
    
    @Override
	public String getPathSegment() throws JsonException {
		return null;
	}

	@Override
	public List<JsonElement> apply(JsonElement element) throws JsonException {
	    
    	List<JsonElement> result = new ArrayList<JsonElement>();
    	
    	if (element.isJsonObject() && element.toJsonObject().hasProperty(pathFragment)) {
    		JsonElement o = element.toJsonObject().getProperty(pathFragment);
    		if(o != null){
    			result.add(o);
    		}
    		else{
    			result.add(factory.createJsonNull(pathFragment,element));
    		}
    	}
    	else if(element.isJsonObject()){
    		result.add(factory.createJsonNull(pathFragment,element));
    	}

        return result;
    }
}
