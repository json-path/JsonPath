package com.jayway.jsonpath.internal.function;

import com.jayway.jsonpath.Filter;
import com.jayway.jsonpath.internal.EvaluationContext;
import com.jayway.jsonpath.internal.PathRef;

/**
 * Applies on the children of a Map or an Array
 */ 
public class Last extends Child {

    @Override
    public Object invoke(String currentPath, PathRef parent, Object model, EvaluationContext ctx, Filter filter) {   	
    	Object res = super.invoke(currentPath, parent, model, ctx, filter);
    	if(res!=null && ctx.configuration().jsonProvider().isArray(res)){	
    		int length = ctx.configuration().jsonProvider().length(res);
            if(length  > 0) {
            	return ctx.configuration().jsonProvider().getArrayIndex(res, length-1);
            }
    	}	
    	return res;		
    }
}
