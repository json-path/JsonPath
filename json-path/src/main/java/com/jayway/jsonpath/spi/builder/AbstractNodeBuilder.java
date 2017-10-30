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

import static com.jayway.jsonpath.internal.Utils.notNull;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.Criteria;
import com.jayway.jsonpath.Filter;
import com.jayway.jsonpath.InvalidPathException;
import com.jayway.jsonpath.JsonPathException;
import com.jayway.jsonpath.Option;
import com.jayway.jsonpath.PathNotFoundException;
import com.jayway.jsonpath.Predicate;
import com.jayway.jsonpath.internal.Path;
import com.jayway.jsonpath.internal.filter.BooleanNode;
import com.jayway.jsonpath.internal.filter.ClassNode;
import com.jayway.jsonpath.internal.filter.Evaluator;
import com.jayway.jsonpath.internal.filter.EvaluatorFactory;
import com.jayway.jsonpath.internal.filter.NullNode;
import com.jayway.jsonpath.internal.filter.NumberNode;
import com.jayway.jsonpath.internal.filter.PathNode;
import com.jayway.jsonpath.internal.filter.PatternNode;
import com.jayway.jsonpath.internal.filter.RelationalExpressionNode;
import com.jayway.jsonpath.internal.filter.RelationalOperator;
import com.jayway.jsonpath.internal.filter.StringNode;
import com.jayway.jsonpath.internal.filter.UndefinedNode;
import com.jayway.jsonpath.internal.filter.ValueListNode;
import com.jayway.jsonpath.internal.filter.ValueNode;
import com.jayway.jsonpath.internal.path.PathCompiler;
import com.jayway.jsonpath.internal.path.PredicateContextImpl;
import com.jayway.jsonpath.spi.json.JsonProvider;

/**
 *
 */
public abstract class AbstractNodeBuilder implements NodeBuilder
{
	//********************************************************************//
	//						NESTED DECLARATIONS			  			      //
	//********************************************************************//

	class RelationalExpressionNodeImpl extends RelationalExpressionNode
	{

		/**
		 * @param left
		 * @param relationalOperator
		 * @param right
		 */
		public RelationalExpressionNodeImpl(ValueNode left,
		        RelationalOperator relationalOperator, ValueNode right)
		{
			super(left, relationalOperator, right);
		}		


	    @Override
	    public boolean apply(PredicateContext ctx) {
	        ValueNode l = left;
	        ValueNode r = right;

	        if(left.isPathNode()){
	            l = evaluate(left.asPathNode(), ctx);
	        }
	        if(right.isPathNode()){
	            r = evaluate(right.asPathNode(), ctx);
	        }
	        Evaluator evaluator = EvaluatorFactory.createEvaluator(relationalOperator);
	        if(evaluator != null){
	            return evaluator.evaluate(l, r, ctx);
	        }
	        return false;
	    }
		
	}
	
	class CriteriaImpl extends Criteria
	{
		/**
		 * @param left
		 * @param criteriaType
		 * @param right
		 */
		public CriteriaImpl(ValueNode left){
			super(left);
		}

	    /**
		 * @param left
		 * @param criteriaType
		 * @param right
		 */
		public CriteriaImpl(ValueNode left, RelationalOperator criteriaType,
		        ValueNode right){
			
			super(left, criteriaType, right);
		}

		/**
		 * @param criteriaChain
		 * @param valueNode
		 */
		public CriteriaImpl(List<Criteria> criteriaChain, 
				ValueNode left){
			
			super(criteriaChain, left);
		}

		protected Collection<RelationalExpressionNode> toRelationalExpressionNodes(){
	        
			List<RelationalExpressionNode> nodes =
				new ArrayList<RelationalExpressionNode>(criteriaChain.size());
	       
			for (Criteria criteria : criteriaChain){
	            nodes.add(criteria.toRelationalExpressionNode());
	        }
	        return nodes;
	    }

		public RelationalExpressionNode toRelationalExpressionNode(){
	        
			return new RelationalExpressionNodeImpl(left, criteriaType, right);	        
	    }
		
	    /**
	     * The <code>subsetof</code> operator selects objects for which the specified field is
	     * an array whose elements comprise a subset of the set comprised by the elements of
	     * the specified array.
	     *
	     * @param c the values to match against
	     * @return the criteria
	     */
	    public Criteria subsetof(Collection<?> c) {
	        notNull(c, "collection can not be null");
	        this.criteriaType = RelationalOperator.SUBSETOF;
	        this.right = toValueListNode(c);
	        return this;
	    }

	    
	    /**
	     * Static factory method to create a Criteria using the provided key
	     *
	     * @param key ads new filed to criteria
	     * @return the criteria builder
	     */
	    public Criteria and(String key) {
	        checkComplete();
	        return new CriteriaImpl(this.criteriaChain, toValueNode(
	        		prefixPath(key)));
	    }

	    /**
	     * Creates a criterion using equality
	     *
	     * @param o
	     * @return the criteria
	     */
	    public Criteria is(Object o) {
	        this.criteriaType = RelationalOperator.EQ;
	        this.right = toValueNode(o);
	        return this;
	    }
	    
	    /**
	     * Creates a criterion using the <b>!=</b> operator
	     *
	     * @param o
	     * @return the criteria
	     */
	    public Criteria ne(Object o) {
	        this.criteriaType = RelationalOperator.NE;
	        this.right = toValueNode(o);
	        return this;
	    }

	    /**
	     * Creates a criterion using the <b>&lt;</b> operator
	     *
	     * @param o
	     * @return the criteria
	     */
	    public Criteria lt(Object o) {
	        this.criteriaType = RelationalOperator.LT;
	        this.right = toValueNode(o);
	        return this;
	    }

	    /**
	     * Creates a criterion using the <b>&lt;=</b> operator
	     *
	     * @param o
	     * @return the criteria
	     */
	    public Criteria lte(Object o) {
	        this.criteriaType = RelationalOperator.LTE;
	        this.right = toValueNode(o);
	        return this;
	    }

	    /**
	     * Creates a criterion using the <b>&gt;</b> operator
	     *
	     * @param o
	     * @return the criteria
	     */
	    public Criteria gt(Object o) {
	        this.criteriaType = RelationalOperator.GT;
	        this.right = toValueNode(o);
	        return this;
	    }

	    /**
	     * Creates a criterion using the <b>&gt;=</b> operator
	     *
	     * @param o
	     * @return the criteria
	     */
	    public Criteria gte(Object o) {
	        this.criteriaType = RelationalOperator.GTE;
	        this.right = toValueNode(o);
	        return this;
	    }

	    /**
	     * Creates a criterion using a Regex
	     *
	     * @param pattern
	     * @return the criteria
	     */
	    public Criteria regex(Pattern pattern) {
	        notNull(pattern, "pattern can not be null");
	        this.criteriaType = RelationalOperator.REGEX;
	        this.right = toValueNode(pattern);
	        return this;
	    }
	    /**
	     * The <code>in</code> operator is analogous to the SQL IN modifier, allowing you
	     * to specify an array of possible matches.
	     *
	     * @param c the collection containing the values to match against
	     * @return the criteria
	     */
	    public Criteria in(Collection<?> c) {
	        notNull(c, "collection can not be null");
	        this.criteriaType = RelationalOperator.IN;
	        this.right = toValueListNode(c);
	        return this;
	    }

	    /**
	     * The <code>contains</code> operator asserts that the provided object is contained
	     * in the result. The object that should contain the input can be either an object or a String.
	     *
	     * @param o that should exists in given collection or
	     * @return the criteria
	     */
	    public Criteria contains(Object o) {
	        this.criteriaType = RelationalOperator.CONTAINS;
	        this.right = toValueNode(o);
	        return this;
	    }
	    
	    /**
	     * The <code>all</code> operator is similar to $in, but instead of matching any value
	     * in the specified array all values in the array must be matched.
	     *
	     * @param c
	     * @return the criteria
	     */
	    public Criteria all(Collection<?> c) {
	        notNull(c, "collection can not be null");
	        this.criteriaType = RelationalOperator.ALL;
	        this.right = toValueListNode(c);
	        return this;
	    }
	    
	    /**
	     * The <code>size</code> operator matches:
	     * <p/>
	     * <ol>
	     * <li>array with the specified number of elements.</li>
	     * <li>string with given length.</li>
	     * </ol>
	     *
	     * @param size
	     * @return the criteria
	     */
	    public Criteria size(int size) {
	        this.criteriaType = RelationalOperator.SIZE;
	        this.right = toValueNode(size);
	        return this;
	    }

	    /**
	     * The $type operator matches values based on their Java JSON type.
	     *
	     * Supported types are:
	     *
	     *  List.class
	     *  Map.class
	     *  String.class
	     *  Number.class
	     *  Boolean.class
	     *
	     * Other types evaluates to false
	     *
	     * @param clazz
	     * @return the criteria
	     */
	    public Criteria type(Class<?> clazz) {
	        this.criteriaType = RelationalOperator.TYPE;
	        this.right = createClassNode(clazz);
	        return this;
	    }

	    /**
	     * Check for existence (or lack thereof) of a field.
	     *
	     * @param shouldExist
	     * @return the criteria
	     */
	    public Criteria exists(boolean shouldExist) {
	        this.criteriaType = RelationalOperator.EXISTS;
	        this.right = toValueNode(shouldExist);
	        this.left = left.asPathNode().asExistsCheck(shouldExist);
	        return this;
	    }
	    
	    /**
	     * The <code>nin</code> operator is similar to $in except that it selects objects for
	     * which the specified field does not have any value in the specified array.
	     *
	     * @param c the values to match against
	     * @return the criteria
	     */
	    public Criteria nin(Collection<?> c) {
	        notNull(c, "collection can not be null");
	        this.criteriaType = RelationalOperator.NIN;
	        this.right = toValueListNode(c);
	        return this;
	    }

	    private void checkComplete(){
	        boolean complete = (left != null && criteriaType != null && right != null);
	        if(!complete){
	            throw new JsonPathException(
	            "Criteria build exception. Complete on criteria before defining next.");
	        }
	    }

	}
	
	//********************************************************************//
	//						ABSTRACT DECLARATIONS						  //
	//********************************************************************//

	//********************************************************************//
	//						STATIC DECLARATIONS							  //
	//********************************************************************//


    public static String prefixPath(String key){
        if (!key.startsWith("$") && !key.startsWith("@")) {
            key = "@." + key;
        }
        return key;
    }
    
	//********************************************************************//
	//						INSTANCE DECLARATIONS						  //
	//********************************************************************//
	
	/**
	 * 
	 */
	protected AbstractNodeBuilder(){
	}

	/**
	 * @inheritDoc
	 *
	 * @see com.jayway.jsonpath.spi.builder.NodeBuilder#isPath(java.lang.Object)
	 */
	@Override
	public boolean isPath(Object o)
	{
		if(o == null || !(o instanceof String)){
		  return false;
	    }
	    String str = o.toString().trim();
	    if (str.length() <= 0) {
	        return false;
	    }
	    char c0 = str.charAt(0);
	    if(c0 == '@' || c0 == '$'){
	        try {
	            PathCompiler.compile(str);
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
	 * @see com.jayway.jsonpath.spi.builder.NodeBuilder#toValueNode(java.lang.Object)
	 */
	@Override
	public ValueNode toValueNode(Object o)
	{
	    if(o == null) return ValueNode.NULL_NODE;
	    if(o instanceof ValueNode) return (ValueNode)o;
	    if(o instanceof Class) return createClassNode((Class<?>)o);
	    else if(isPath(o)) return new PathNode(o.toString(), false, false);
	    else if(isJson(o)) return createJsonNode(o.toString());
	    else if(o instanceof String) return createStringNode(o.toString(), true);
	    else if(o instanceof Character) return createStringNode(o.toString(), false);
	    else if(o instanceof Number) return createNumberNode(o.toString());
	    else if(o instanceof Boolean) return createBooleanNode(o.toString());
	    else if(o instanceof Pattern) return createPatternNode((Pattern)o);
	    else throw new JsonPathException("Could not determine value type");
	}

    public ValueListNode toValueListNode (Collection<?> c)
    {
    	Iterator iterator = c.iterator();            	
    	List<ValueNode> valueNodes = new ArrayList<ValueNode>();
    	while(iterator.hasNext())
    	{
    		valueNodes.add(toValueNode(iterator.next()));
    	}
    	return new ValueListNode(Collections.unmodifiableList(valueNodes));
    }
	
	/**
	 * @inheritDoc
	 *
	 * @see com.jayway.jsonpath.spi.builder.NodeBuilder#createStringNode(java.lang.CharSequence, boolean)
	 */
	@Override
	public StringNode createStringNode(CharSequence charSequence,
	        boolean escape)
	{
		return new StringNode(charSequence, escape);
	}

	/**
	 * @inheritDoc
	 *
	 * @see com.jayway.jsonpath.spi.builder.NodeBuilder#createClassNode(java.lang.Class)
	 */
	@Override
	public ClassNode createClassNode(Class<?> clazz)
	{
		return new ClassNode(clazz);
	}

	/**
	 * @inheritDoc
	 *
	 * @see com.jayway.jsonpath.spi.builder.NodeBuilder#createNumberNode(java.lang.CharSequence)
	 */
	@Override
	public NumberNode createNumberNode(CharSequence charSequence)
	{
		 return new NumberNode(charSequence);
	}


	/**
	 * @inheritDoc
	 *
	 * @see com.jayway.jsonpath.spi.builder.NodeBuilder#createNumberNode(java.lang.CharSequence)
	 */
	@Override
	public NumberNode createNumberNode(Number number)
	{
		 return this.createNumberNode(number.toString());
	}

	/**
	 * @inheritDoc
	 *
	 * @see com.jayway.jsonpath.spi.builder.NodeBuilder#createBooleanNode(java.lang.CharSequence)
	 */
	@Override
	public BooleanNode createBooleanNode(CharSequence charSequence)
	{
		return Boolean.parseBoolean(charSequence.toString())
				? ValueNode.TRUE : ValueNode.FALSE;
	}

	/**
	 * @inheritDoc
	 *
	 * @see com.jayway.jsonpath.spi.builder.NodeBuilder#createBooleanNode(java.lang.CharSequence)
	 */
	@Override
	public BooleanNode createBooleanNode(Boolean bool)
	{
		return bool.booleanValue()?ValueNode.TRUE:ValueNode.FALSE;
	}

	/**
	 * @inheritDoc
	 *
	 * @see com.jayway.jsonpath.spi.builder.NodeBuilder#createNullNode()
	 */
	@Override
	public NullNode createNullNode(){
		
		return ValueNode.NULL_NODE;
	}

	/**
	 * @inheritDoc
	 *
	 * @see com.jayway.jsonpath.spi.builder.NodeBuilder#createPatternNode(java.lang.CharSequence)
	 */
	@Override
	public PatternNode createPatternNode(CharSequence pattern){
		
		 return new PatternNode(pattern);
	}

	/**
	 * @inheritDoc
	 *
	 * @see com.jayway.jsonpath.spi.builder.NodeBuilder#createPatternNode(java.util.regex.Pattern)
	 */
	@Override
	public PatternNode createPatternNode(Pattern pattern){
		
		return new PatternNode(pattern);
	}

	/**
	 * @inheritDoc
	 *
	 * @see com.jayway.jsonpath.spi.builder.NodeBuilder#createUndefinedNode()
	 */
	@Override
	public UndefinedNode createUndefinedNode(){
		
		 return ValueNode.UNDEFINED;
	}

	/**
	 * @inheritDoc
	 *
	 * @see com.jayway.jsonpath.spi.builder.NodeBuilder#createPathNode(java.lang.CharSequence, boolean, boolean)
	 */
	@Override
	public PathNode createPathNode(CharSequence path, boolean existsCheck,
	        boolean shouldExists){
		
		 return new PathNode(path, existsCheck, shouldExists);
	}

	/**
	 * @inheritDoc
	 *
	 * @see com.jayway.jsonpath.spi.builder.NodeBuilder#createPathNode(com.jayway.jsonpath.internal.Path)
	 */
	@Override
	public ValueNode createPathNode(Path path){
		
		return new PathNode(path);
	}

	/**
	 * @inheritDoc
	 *
	 * @see com.jayway.jsonpath.spi.builder.NodeBuilder#createRelationalExpressionNode(com.jayway.jsonpath.internal.filter.ValueNode, com.jayway.jsonpath.internal.filter.RelationalOperator, com.jayway.jsonpath.internal.filter.ValueNode)
	 */
	@Override
	public RelationalExpressionNode createRelationalExpressionNode(
	        ValueNode left, RelationalOperator operator, ValueNode right){
		
		return new RelationalExpressionNodeImpl(left,operator,right);
	}
    
	/**
	 * @inheritDoc
	 *
	 * @see com.jayway.jsonpath.spi.builder.NodeBuilder#evaluate(com.jayway.jsonpath.internal.filter.PathNode, com.jayway.jsonpath.Predicate.PredicateContext)
	 */
	@Override
	public ValueNode evaluate(PathNode pathNode, Predicate.PredicateContext ctx) {
		
        if (pathNode.isExistsCheck()) {
            try {
                Configuration c = Configuration.builder().jsonProvider(ctx.configuration().jsonProvider()).options(Option.REQUIRE_PROPERTIES).build();
                Object result = pathNode.getPath().evaluate(ctx.item(), ctx.root(), c).getValue(false);
                return result == JsonProvider.UNDEFINED ? ValueNode.FALSE : ValueNode.TRUE;
            } catch (PathNotFoundException e) {
                return ValueNode.FALSE;
            }
        } else {
            try {
                Object res;
                if (ctx instanceof PredicateContextImpl) {
                    //This will use cache for document ($) queries
                    PredicateContextImpl ctxi = (PredicateContextImpl) ctx;
                    res = ctxi.evaluate(pathNode.getPath());
                } else {
                    Object doc = pathNode.getPath().isRootPath() ? ctx.root() : ctx.item();
                    res = pathNode.getPath().evaluate(doc, ctx.root(), ctx.configuration()).getValue();
                }
                res = ctx.configuration().jsonProvider().unwrap(res);

                if (res instanceof Number) return createNumberNode((Number)res);
                else if (res instanceof BigDecimal) return createNumberNode((BigDecimal)res);
                else if (res instanceof String) return createStringNode((CharSequence) res, false);
                else if (res instanceof Boolean) return createBooleanNode((Boolean)res);
                else if (res == null) return ValueNode.NULL_NODE;
                else if (ctx.configuration().jsonProvider().isArray(res)) return createJsonNode(
                		ctx.configuration().mappingProvider().map(res, List.class, ctx.configuration()));
                else if (ctx.configuration().jsonProvider().isMap(res)) return createJsonNode(
                		ctx.configuration().mappingProvider().map(res, Map.class, ctx.configuration()));
                else throw new JsonPathException("Could not convert " + res.toString() + " to a ValueNode");
            } catch (PathNotFoundException e) {
                return ValueNode.UNDEFINED;
            }
        }
    }

    /**
     * Static factory method to create a Criteria using the provided key
     *
     * @param key filed name
     * @return the new criteria
     */
    @Deprecated
    //This should be private.It exposes internal classes
    public Criteria where(Path key) {
    	
        return new CriteriaImpl(createPathNode(key));
    }


    /**
     * Static factory method to create a Criteria using the provided key
     *
     * @param key filed name
     * @return the new criteria
     */

    public Criteria where(String key) {
    	
        return new CriteriaImpl(toValueNode(prefixPath(key)));
    }


    /**
     * Parse the provided criteria
     *
     * Deprecated use {@link Filter#parse(String)}
     *
     * @param criteria
     * @return a criteria
     */
    @Deprecated
    public Criteria parse(String criteria) {
    	
        if(criteria == null){
            throw new InvalidPathException("Criteria can not be null");
        }
        String[] split = criteria.trim().split(" ");
        if(split.length == 3){
            return create(split[0], split[1], split[2]);
        } else if(split.length == 1){
            return create(split[0], "EXISTS", "true");
        } else {
            throw new InvalidPathException("Could not parse criteria");
        }
    }

    /**
     * Creates a new criteria
     * @param left path to evaluate in criteria
     * @param operator operator
     * @param right expected value
     * @return a new Criteria
     */
    @Deprecated
    public Criteria create(String left, String operator, String right)
    {
    	Criteria criteria = new CriteriaImpl(toValueNode(left), 
    		RelationalOperator.fromString(operator), toValueNode(right));
        return criteria;
    }
}
