/*
 * Copyright 2011 the original author or authors.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.jayway.jsonpath.spi.builder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.jayway.jsonpath.Predicate.PredicateContext;
import com.jayway.jsonpath.internal.filter.JsonNode;
import com.jayway.jsonpath.internal.filter.ValueListNode;
import com.jayway.jsonpath.internal.filter.ValueNode;

import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;

public class JsonSmartNodeBuilder extends AbstractNodeBuilder
{
	class JsonNodeImpl extends JsonNode{
		/**
		 * @param charSequence
		 */
		JsonNodeImpl(CharSequence charSequence)
		{
			super(charSequence);
		}

		/**
		 * @param parsedJson
		 */
		JsonNodeImpl(Object parsedJson)
		{
			super(parsedJson);
		}

		/**
		 * @inheritDoc
		 *
		 * @see com.jayway.jsonpath.internal.filter.JsonNode#parse(com.jayway.jsonpath.Predicate.PredicateContext)
		 */
		@Override
		public Object parse(PredicateContext ctx){
		   
			try {
                return parsed ? json : new JSONParser(
            	    JSONParser.MODE_PERMISSIVE).parse(
            	    		json.toString());
            
            } catch (ParseException e) {
               throw new IllegalArgumentException(e);
            }
		}

		/**
		 * @inheritDoc
		 *
		 * @see com.jayway.jsonpath.internal.filter.JsonNode#asValueListNode(com.jayway.jsonpath.Predicate.PredicateContext)
		 */
		@Override
		public ValueNode asValueListNode(PredicateContext ctx){
			if(!isArray(ctx)){
                return ValueNode.UNDEFINED;
            } else {            	
            	List list = (List) parse(ctx);
            	Iterator iterator = list.iterator();            	
            	List<ValueNode> valueNodes = new ArrayList<ValueNode>();
            	while(iterator.hasNext())
            	{
            		valueNodes.add(toValueNode(iterator.next()));
            	}
                return new ValueListNode(Collections.unmodifiableList(valueNodes));
            }
		}
		
	}

	public JsonSmartNodeBuilder(){}

	/**
	 * @inheritDoc
	 *
	 * @see com.jayway.jsonpath.spi.builder.NodeBuilder#isJson(java.lang.Object)
	 */
	@Override
	public boolean isJson(Object o)
	{
		if(o == null || !(o instanceof String)){
            return false;
        }
        String str = o.toString().trim();
        if (str.length() <= 1) {
            return false;
        }
        char c0 = str.charAt(0);
        char c1 = str.charAt(str.length() - 1);
        if ((c0 == '[' && c1 == ']') || (c0 == '{' && c1 == '}')){
            try {
                new JSONParser(JSONParser.MODE_PERMISSIVE).parse(str);
                return true;
            } catch(Exception e){
                return false;
            }
        }
        return false;
	}

	/**
	 * @inheritDoc
	 *
	 * @see com.jayway.jsonpath.spi.builder.NodeBuilder#createJsonNode(java.lang.CharSequence)
	 */
	@Override
	public JsonNode createJsonNode(CharSequence json){
		JsonNodeImpl n = new JsonNodeImpl(json);
		return n;
	}

	/**
	 * @inheritDoc
	 *
	 * @see com.jayway.jsonpath.spi.builder.NodeBuilder#createJsonNode(java.lang.Object)
	 */
	@Override
	public JsonNode createJsonNode(Object parsedJson){
		JsonNodeImpl n = new JsonNodeImpl(parsedJson);
		return n;
	}
}