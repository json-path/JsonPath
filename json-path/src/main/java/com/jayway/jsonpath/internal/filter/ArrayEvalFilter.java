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

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Kalle Stenflo
 */
public class ArrayEvalFilter extends PathTokenFilter {

    private static final Pattern PATTERN = Pattern.compile("(.*?)\\s?([=<>]+)\\s?(.*)");

    public ArrayEvalFilter(String condition) {
        super(condition);
    }

    @Override
    public Object filter(Object obj, JsonProvider jsonProvider) {
        //[?(@.isbn == 10)]
        List<Object> src = jsonProvider.toList(obj);
        List<Object> result = jsonProvider.createList();

        String trimmedCondition = condition;

        if(condition.contains("['")){
            trimmedCondition = trimmedCondition.replace("['", ".");
            trimmedCondition = trimmedCondition.replace("']", "");
        }

        trimmedCondition = trim(trimmedCondition, 5, 2);

        ConditionStatement conditionStatement = createConditionStatement(trimmedCondition);

        for (Object item : src) {
            if (isMatch(item, conditionStatement, jsonProvider)) {
                result.add(item);
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
        if (!jsonProvider.isMap(check)) {
            return false;
        }
        Map<String, Object> obj = jsonProvider.toMap(check);

        if (!obj.containsKey(conditionStatement.getField())) {
            return false;
        }

        Object propertyValue = obj.get(conditionStatement.getField());

        if (jsonProvider.isContainer(propertyValue)) {
            return false;
        }
        return ExpressionEvaluator.eval(propertyValue, conditionStatement.getOperator(), conditionStatement.getExpected());
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

    private class ConditionStatement {
        private String field;
        private String operator;
        private String expected;

        private ConditionStatement(String field, String operator, String expected) {
            this.field = field;
            this.operator = operator.trim();
            this.expected = expected;

            if(this.expected.startsWith("'")){
                this.expected = trim(this.expected, 1, 1);
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
