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
package com.jayway.jsonpath;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

import com.jayway.jsonpath.internal.Utils;
import com.jayway.jsonpath.internal.filter.PredicateNode;
import com.jayway.jsonpath.internal.filter.RelationalExpressionNode;
import com.jayway.jsonpath.internal.filter.RelationalOperator;
import com.jayway.jsonpath.internal.filter.ValueNode;

/**
 *
 */
public abstract class Criteria implements Predicate {

	protected abstract Collection<RelationalExpressionNode> toRelationalExpressionNodes();
	

	public abstract RelationalExpressionNode toRelationalExpressionNode();
	
	/**
     * The <code>subsetof</code> operator selects objects for which the specified field is
     * an array whose elements comprise a subset of the set comprised by the elements of
     * the specified array.
     *
     * @param c the values to match against
     * @return the criteria
     */
    public abstract Criteria subsetof(Collection<?> c);

    
    /**
     * Static factory method to create a Criteria using the provided key
     *
     * @param key ads new filed to criteria
     * @return the criteria builder
     */
    public abstract Criteria and(String key);

    /**
     * Creates a criterion using equality
     *
     * @param o
     * @return the criteria
     */
    public abstract Criteria is(Object o);
    
    /**
     * Creates a criterion using the <b>!=</b> operator
     *
     * @param o
     * @return the criteria
     */
    public abstract Criteria ne(Object o);

    /**
     * Creates a criterion using the <b>&lt;</b> operator
     *
     * @param o
     * @return the criteria
     */
    public abstract Criteria lt(Object o);

    /**
     * Creates a criterion using the <b>&lt;=</b> operator
     *
     * @param o
     * @return the criteria
     */
    public abstract Criteria lte(Object o);

    /**
     * Creates a criterion using the <b>&gt;</b> operator
     *
     * @param o
     * @return the criteria
     */
    public abstract Criteria gt(Object o);

    /**
     * Creates a criterion using the <b>&gt;=</b> operator
     *
     * @param o
     * @return the criteria
     */
    public abstract Criteria gte(Object o);

    /**
     * Creates a criterion using a Regex
     *
     * @param pattern
     * @return the criteria
     */
    public abstract Criteria regex(Pattern pattern);
    
    /**
     * The <code>in</code> operator is analogous to the SQL IN modifier, allowing you
     * to specify an array of possible matches.
     *
     * @param c the collection containing the values to match against
     * @return the criteria
     */
    public abstract Criteria in(Collection<?> c);

    /**
     * The <code>contains</code> operator asserts that the provided object is contained
     * in the result. The object that should contain the input can be either an object or a String.
     *
     * @param o that should exists in given collection or
     * @return the criteria
     */
    public abstract Criteria contains(Object o);
    
    /**
     * The <code>all</code> operator is similar to $in, but instead of matching any value
     * in the specified array all values in the array must be matched.
     *
     * @param c
     * @return the criteria
     */
    public abstract Criteria all(Collection<?> c);

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
    public abstract Criteria size(int size);

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
    public abstract Criteria type(Class<?> clazz);
    
    /**
     * Check for existence (or lack thereof) of a field.
     *
     * @param shouldExist
     * @return the criteria
     */
    public abstract Criteria exists(boolean shouldExist);
    
    /**
     * The <code>nin</code> operator is similar to $in except that it selects objects for
     * which the specified field does not have any value in the specified array.
     *
     * @param c the values to match against
     * @return the criteria
     */
    public abstract Criteria nin(Collection<?> c);
    
    
    protected final List<Criteria> criteriaChain;
    protected ValueNode left;
    protected RelationalOperator criteriaType;
    protected ValueNode right;

    protected Criteria(List<Criteria> criteriaChain, ValueNode left) {
        this.left = left;
        this.criteriaChain = criteriaChain;
        this.criteriaChain.add(this);
    }

    protected Criteria(ValueNode left) {
        this(new LinkedList<Criteria>(), left);
    }

	protected Criteria(ValueNode left, RelationalOperator criteriaType,
			ValueNode right)
	{
		this(new LinkedList<Criteria>(), left);
		this.criteriaType = criteriaType;
		this.right = right;
	}

	@Override
    public boolean apply(PredicateContext ctx) {
        for (RelationalExpressionNode expressionNode : toRelationalExpressionNodes()) {
            if(!expressionNode.apply(ctx)){
                return false;
            }
        }
        return true;
    }

    @Override
    public String toString() {
        return Utils.join(" && ", toRelationalExpressionNodes());
    }    

    /**
     * Creates a criterion using equality
     *
     * @param o
     * @return the criteria
     */
    public Criteria eq(Object o) {
        return is(o);
    }   

    /**
     * The <code>in</code> operator is analogous to the SQL IN modifier, allowing you
     * to specify an array of possible matches.
     *
     * @param o the values to match against
     * @return the criteria
     */
    public Criteria in(Object... o) {
        return in(Arrays.asList(o));
    }

    /**
     * The <code>nin</code> operator is similar to $in except that it selects objects for
     * which the specified field does not have any value in the specified array.
     *
     * @param o the values to match against
     * @return the criteria
     */
    public Criteria nin(Object... o) {
        return nin(Arrays.asList(o));
    }

    /**
     * The <code>subsetof</code> operator selects objects for which the specified field is
     * an array whose elements comprise a subset of the set comprised by the elements of
     * the specified array.
     *
     * @param o the values to match against
     * @return the criteria
     */
    public Criteria subsetof(Object... o) {
        return subsetof(Arrays.asList(o));
    }

    /**
     * The <code>all</code> operator is similar to $in, but instead of matching any value
     * in the specified array all values in the array must be matched.
     *
     * @param o
     * @return the criteria
     */
    public Criteria all(Object... o) {
        return all(Arrays.asList(o));
    }

    /**
     * The <code>notEmpty</code> operator checks that an array or String is not empty.
     *
     * @return the criteria
     */
    @Deprecated
    public Criteria notEmpty() {
        return empty(false);
    }

    /**
     * The <code>notEmpty</code> operator checks that an array or String is empty.
     *
     * @param empty should be empty
     * @return the criteria
     */
    public Criteria empty(boolean empty) {
        this.criteriaType = RelationalOperator.EMPTY;
        this.right = empty ? ValueNode.TRUE : ValueNode.FALSE;
        return this;
    }

    /**
     * The <code>matches</code> operator checks that an object matches the given predicate.
     *
     * @param p
     * @return the criteria
     */
    public Criteria matches(Predicate p) {
        this.criteriaType = RelationalOperator.MATCHES;
        this.right = new PredicateNode(p);
        return this;
    }
}
