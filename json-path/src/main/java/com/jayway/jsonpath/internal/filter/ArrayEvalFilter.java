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
package com.jayway.jsonpath.internal.filter;

import com.jayway.jsonpath.InvalidPathException;
import com.jayway.jsonpath.internal.filter.eval.ExpressionEvaluator;
import com.jayway.jsonpath.spi.JsonProvider;

import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Kalle Stenflo
 */
public class ArrayEvalFilter extends PathTokenFilter {

    private static final Pattern PATTERN = Pattern.compile("(.*?)\\s?([=<>]+)\\s?(.*)");
    private final ConditionStatement conditionStatement;

    public ArrayEvalFilter(String condition) {
        super(condition);
        //[?(@.isbn == 10)]

        String trimmedCondition = condition;

        if(condition.contains("['")){
            trimmedCondition = trimmedCondition.replace("['", ".");
            trimmedCondition = trimmedCondition.replace("']", "");
        }
        if(trimmedCondition.startsWith("[?(@==")){
            trimmedCondition = trim(trimmedCondition, 4, 2);
        } else {
            trimmedCondition = trim(trimmedCondition, 5, 2);
        }
        this.conditionStatement = createConditionStatement(trimmedCondition);
    }

    @Override
    public Object filter(Object obj, JsonProvider jsonProvider) {
        Iterable<Object> src = null;
        try {
            src = jsonProvider.toIterable(obj);
        } catch (ClassCastException e){
            throw new InvalidPathException("The path fragment '" + this.condition + "' can not be applied to a JSON object only a JSON array.", e);
        }
        Object result = jsonProvider.createArray();
        for (Object item : src) {
            if (isMatch(item, conditionStatement, jsonProvider)) {
                jsonProvider.setProperty(result, jsonProvider.length(result), item);
            }
        }
        return result;
    }

    @Override
    public Object getRef(Object obj, JsonProvider jsonProvider) {
        throw new UnsupportedOperationException("");
    }

    @Override
    public boolean isArrayFilter() {
        return true;
    }

    private boolean isMatch(Object check, ConditionStatement conditionStatement, JsonProvider jsonProvider) {
        if (jsonProvider.isMap(check)) {
            Collection<String> keys = jsonProvider.getPropertyKeys(check);

            if (!keys.contains(conditionStatement.getField())) {
                return false;
            }

            Object propertyValue = jsonProvider.getProperty(check, conditionStatement.getField());

            if (jsonProvider.isContainer(propertyValue)) {
                return false;
            }
            return ExpressionEvaluator.eval(propertyValue, conditionStatement.getOperator(), conditionStatement.getExpected());
        } else if(jsonProvider.isArray(check)) {
            return false;
        } else {
            return ExpressionEvaluator.eval(check, conditionStatement.getOperator(), conditionStatement.getExpected());
        }

    }


    private ConditionStatement createConditionStatement(String str) {
        Matcher matcher = PATTERN.matcher(str);
        if (matcher.matches()) {
            String property = matcher.group(1);
            String operator = matcher.group(2);
            String expected = matcher.group(3);

            return new ConditionStatement(property, operator, expected);
        } else {
            throw new InvalidPathException("Invalid match " + str);
        }
    }

    private static class ConditionStatement {
        private final String field;
        private final String operator;
        private final String expected;

        private ConditionStatement(String field, String operator, String expected) {
            this.field = field;
            this.operator = operator.trim();
            

            if(expected.startsWith("'")){
                this.expected = trim(expected, 1, 1);
            }else{
                this.expected = expected;
            }
        }

        public String getField() {
            return field;
        }

        public String getOperator() {
            return operator;
        }

        public String getExpected() {
            return expected;
        }
    }
}
