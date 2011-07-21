package com.jayway.jsonpath.filter;



import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

import com.google.gson.JsonNull;
import com.jayway.jsonpath.json.JsonArray;
import com.jayway.jsonpath.json.JsonElement;
import com.jayway.jsonpath.json.JsonException;

/**
 * Created by IntelliJ IDEA.
 * User: kallestenflo
 * Date: 2/15/11
 * Time: 8:02 PM
 */
public class ListIndexFilter extends JsonPathFilterBase {

    public static final Pattern PATTERN = Pattern.compile("\\[(\\s?\\d+\\s?,?)+\\]");               //[1] OR [1,2,3]

    private final String pathFragment;
    @Override
	public String getPathSegment() throws JsonException {
		return pathFragment;
	}
	//@Autowired
	public com.jayway.jsonpath.json.JsonFactory factory = com.jayway.jsonpath.json.minidev.MiniJsonFactory.getInstance();

    public ListIndexFilter(String pathFragment) {
        this.pathFragment = pathFragment;
    }

    @Override
    public List<JsonElement> apply(JsonElement element) throws JsonException {
    	
        Integer[] index = getArrayIndex();
        

        ArrayList<JsonElement> result = new ArrayList<JsonElement>();
        filterRange(result,element,index);
        return result;
        
        
 
    }
    
    private <T extends Collection> void filterRange(T result,JsonElement element,Integer[] index) throws JsonException{
    	  if(element.isJsonArray()){
              for (int i : index) {
                  if (indexIsInRange(element.toJsonArray(), i)) {
                      result.add(element.toJsonArray().get(i));
                  }
                  else{
                      com.jayway.jsonpath.json.JsonNull jn = factory.createJsonNull(i,element);
                	  result.add(jn);
                  }
                  
              }
          }
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


    private Integer[] getArrayIndex() {

        String prepared = pathFragment.replaceAll(" ", "");
        prepared = prepared.substring(1, prepared.length() - 1);

        List<Integer> index = new LinkedList<Integer>();

        String[] split = prepared.split(",");

        for (String s : split) {
            index.add(Integer.parseInt(s));
        }
        return index.toArray(new Integer[0]);
    }
}
