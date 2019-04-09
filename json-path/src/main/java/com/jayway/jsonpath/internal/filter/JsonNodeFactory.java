/**
 * 
 */
package com.jayway.jsonpath.internal.filter;

import com.jayway.jsonpath.internal.filter.ValueNodes.JsonNode;

/** 
 * 
 */
public interface JsonNodeFactory {
	
	boolean handle(String lib);
	
    /**
	 * @param object
	 * @return
	 */
	boolean isJson(Object object);
	
	/**
	 * @param charSequence
	 * @return
	 */
	JsonNode newInstance(CharSequence charSequence);
	
	/**
	 * @param object
	 * @return
	 */
	JsonNode newInstance(Object object);
}
