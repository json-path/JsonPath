package com.jayway.jsonpath.filter;

import com.jayway.jsonpath.InvalidPathException;
import com.jayway.jsonpath.json.JsonElement;
import com.jayway.jsonpath.json.JsonException;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

import org.apache.log4j.Logger;

/**
 * User: kallestenflo
 * Date: 2/2/11
 * Time: 2:00 PM
 */
public class JsonPathFilterChain {

    private List<JsonPathFilterBase> filters;
    private JsonElement payload = null;
    private final Logger log = Logger.getLogger(JsonPathFilterChain.class);
    
    public JsonPathFilterChain(List<String> pathFragments) {
        filters = configureFilters(pathFragments);
    }
    
    public JsonPathFilterChain(List<String> pathFragments,JsonElement payload) {
        filters = configureFilters(pathFragments);
        this.payload = payload;
    }

    private List<JsonPathFilterBase> configureFilters(List<String> pathFragments) {

        List<JsonPathFilterBase> configured = new LinkedList<JsonPathFilterBase>();

        for (String pathFragment : pathFragments) {
            configured.add(JsonPathFilterFactory.createFilter(pathFragment));
        }
        return configured;
    }

    public FilterOutput filter(JsonElement root) throws JsonException {
    	FilterOutput out = new FilterOutput(root);
    	log.info(out.getResult().toString());
        for (JsonPathFilterBase filter : filters) {
            if (filter == null) {
                throw new InvalidPathException();
            }
            if(out.getList() == null){
                return null;
            }
            out = filter.apply(out);
            if(out.getResult()!=null)
            	log.info(out.getResult().toString());
        }
        
        
        return out;
    }
}
