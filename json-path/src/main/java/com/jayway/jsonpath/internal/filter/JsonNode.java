package com.jayway.jsonpath.internal.filter;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.jayway.jsonpath.Predicate;

public abstract class JsonNode extends ValueNode {
    	
	    public abstract Object parse(Predicate.PredicateContext ctx);
	    
    	public abstract  ValueNode asValueListNode(Predicate.PredicateContext ctx);
    	
        protected final Object json;
        protected final boolean parsed;

        JsonNode(CharSequence charSequence) {
            json = charSequence.toString();
            parsed = false;
        }

        public JsonNode(Object json) 
        {
        	if(String.class == json.getClass()
        		|| CharSequence.class.isAssignableFrom(json.getClass()))
        	{
        		this.json = json.toString();
        		parsed = false;
        	} else
        	{
        		this.json = json;
        		parsed = true;
        	}
        }

        @Override
        public Class<?> type(Predicate.PredicateContext ctx) {
            if(isArray(ctx)) return List.class;
            else if(isMap(ctx)) return Map.class;
            else if(parse(ctx) instanceof Number) return Number.class;
            else if(parse(ctx) instanceof String) return String.class;
            else if(parse(ctx) instanceof Boolean) return Boolean.class;
            else return Void.class;
        }

        public boolean isJsonNode() {
            return true;
        }

        public JsonNode asJsonNode() {
            return this;
        }

        public boolean isParsed() {
            return parsed;
        }

        public Object getJson() {
            return json;
        }

        public boolean isArray(Predicate.PredicateContext ctx) {
            return parse(ctx) instanceof List;
        }

        public boolean isMap(Predicate.PredicateContext ctx) {
            return parse(ctx) instanceof Map;
        }

        public int length(Predicate.PredicateContext ctx) {
            return isArray(ctx) ? ((List<?>) parse(ctx)).size() : -1;
        }

        public boolean isEmpty(Predicate.PredicateContext ctx) {
            if (isArray(ctx) || isMap(ctx)) return ((Collection<?>) parse(ctx)).size() == 0;
            else if((parse(ctx) instanceof String)) return ((String)parse(ctx)).length() == 0;
            return true;
        }

        @Override
        public String toString() {
            return json.toString();
        }

        public boolean equals(JsonNode jsonNode, Predicate.PredicateContext ctx) {
            return ((this == jsonNode)||(json==null?jsonNode.json==null:json.equals(jsonNode.parse(ctx))));
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof JsonNode)) return false;

            JsonNode jsonNode = (JsonNode) o;

            return (json == null?jsonNode.json==null:json.equals(jsonNode.json));
        }
    }