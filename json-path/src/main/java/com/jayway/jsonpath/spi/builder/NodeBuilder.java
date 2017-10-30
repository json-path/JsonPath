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

import java.util.regex.Pattern;

import com.jayway.jsonpath.Criteria;
import com.jayway.jsonpath.Filter;
import com.jayway.jsonpath.Predicate;
import com.jayway.jsonpath.internal.Path;
import com.jayway.jsonpath.internal.filter.BooleanNode;
import com.jayway.jsonpath.internal.filter.ClassNode;
import com.jayway.jsonpath.internal.filter.JsonNode;
import com.jayway.jsonpath.internal.filter.NullNode;
import com.jayway.jsonpath.internal.filter.NumberNode;
import com.jayway.jsonpath.internal.filter.PathNode;
import com.jayway.jsonpath.internal.filter.PatternNode;
import com.jayway.jsonpath.internal.filter.RelationalExpressionNode;
import com.jayway.jsonpath.internal.filter.RelationalOperator;
import com.jayway.jsonpath.internal.filter.StringNode;
import com.jayway.jsonpath.internal.filter.UndefinedNode;
import com.jayway.jsonpath.internal.filter.ValueNode;

public interface NodeBuilder
{    
	boolean isPath(Object o);
	
	boolean isJson(Object o);
	
	ValueNode toValueNode(Object value);
	
	StringNode createStringNode(CharSequence charSequence, boolean escape);

    ClassNode createClassNode(Class<?> clazz);

    NumberNode createNumberNode(CharSequence charSequence);
    
	NumberNode createNumberNode(Number number);

    BooleanNode createBooleanNode(CharSequence charSequence);
    
    BooleanNode createBooleanNode(Boolean booleanValue);

    NullNode createNullNode();

    JsonNode createJsonNode(CharSequence json);

    JsonNode createJsonNode(Object parsedJson);

    PatternNode createPatternNode(CharSequence pattern);

    PatternNode createPatternNode(Pattern pattern);

    UndefinedNode createUndefinedNode();

    PathNode createPathNode(CharSequence path, boolean existsCheck, boolean shouldExists);

    ValueNode createPathNode(Path path);
    
    ValueNode evaluate(PathNode pathNode, Predicate.PredicateContext ctx);

	RelationalExpressionNode createRelationalExpressionNode(ValueNode left,
	        RelationalOperator operator, ValueNode right);
	
    @Deprecated
    Criteria where(Path key);

    Criteria where(String key);

    @Deprecated
    Criteria parse(String criteria);

    Criteria create(String left, String operator, String right);

}
