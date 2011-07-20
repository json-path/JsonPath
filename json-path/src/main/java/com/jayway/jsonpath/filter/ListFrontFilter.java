package com.jayway.jsonpath.filter;

import com.jayway.jsonpath.json.JsonArray;
import com.jayway.jsonpath.json.JsonElement;
import com.jayway.jsonpath.json.JsonException;


import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;



/**
 * Created by IntelliJ IDEA.
 * User: kallestenflo
 * Date: 2/15/11
 * Time: 8:20 PM
 */
public class ListFrontFilter extends JsonPathFilterBase {

	//@Autowired
	public com.jayway.jsonpath.json.JsonFactory factory =  com.jayway.jsonpath.json.minidev.MiniJsonFactory.getInstance();

    public static final Pattern PATTERN = Pattern.compile("\\[\\s?:(\\d+)\\s?\\]");               //[ :2 ]

    private final String pathFragment;

    
    @Override
	public String getPathSegment() throws JsonException {
		return pathFragment;
	}
    public ListFrontFilter(String pathFragment) {
        this.pathFragment = pathFragment;
    }

    @Override
    public List<JsonElement> apply(JsonElement element) throws JsonException {
    	List<JsonElement> result =  new ArrayList<JsonElement>();
    	if(element.isJsonArray()){
    		Integer[] index = getListPullIndex();
    		for (int i : index) {
    			if (indexIsInRange(element.toJsonArray(), i)) {
    				result.add(element.toJsonArray().get(i));
    			}
    		}
    	}
        return result;
    }


    private Integer[] getListPullIndex() {
        Matcher matcher = PATTERN.matcher(pathFragment);
        if (matcher.matches()) {

            int pullCount = Integer.parseInt(matcher.group(1));

            List<Integer> result = new LinkedList<Integer>();

            for (int y = 0; y < pullCount; y++) {
                result.add(y);
            }
            return result.toArray(new Integer[0]);
        }
        throw new IllegalArgumentException("invalid list index");
    }

    private boolean indexIsInRange(List list, int index) {
        if (index < 0) {
            return false;
        } else if (index > list.size() - 1) {
            return false;
        } else {
            return true;
        }
    }

}
