package com.jayway.jsonpath.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.jayway.jsonpath.Predicate.PredicateContext;
import com.jayway.jsonpath.internal.filter.JsonNode;
import com.jayway.jsonpath.internal.filter.ValueListNode;
import com.jayway.jsonpath.internal.filter.ValueNode;
import com.jayway.jsonpath.spi.builder.AbstractNodeBuilder;

import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;

public class JsonSmartNodeBuilder extends AbstractNodeBuilder{
	
	class JsonNodeImpl extends JsonNode{


		JsonNodeImpl(CharSequence charSequence)
		{
			super(charSequence);
		}

		JsonNodeImpl(Object parsedJson)
		{
			super(parsedJson);
		}

		@Override
		public Object parse(PredicateContext ctx){
		   
			try {
                return parsed ? json : new JSONParser(
            	    JSONParser.MODE_PERMISSIVE).parse(json.toString());
            
            } catch (ParseException e) {
               throw new IllegalArgumentException(e);
            }
		}
		
		@Override
		public ValueNode asValueListNode(PredicateContext ctx)
		{
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
		
		return new JsonNodeImpl(json);
	}

	/**
	 * @inheritDoc
	 *
	 * @see com.jayway.jsonpath.spi.builder.NodeBuilder#createJsonNode(java.lang.Object)
	 */
	@Override
	public JsonNode createJsonNode(Object parsedJson){
		
		return new JsonNodeImpl(parsedJson);
	}
}
