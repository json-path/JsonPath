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

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;
import com.jayway.jsonpath.PathNotFoundException;
import com.jayway.jsonpath.internal.filter.eval.ExpressionEvaluator;
import com.jayway.jsonpath.spi.JsonProvider;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Kalle Stenflo
 */
public class ArrayEvalFilter extends PathTokenFilter {

    private static final Pattern PATTERN = Pattern.compile("\\[\\s?\\?\\(\\s?(@.*?)\\s?([!=<>]+)\\s?(.*?)\\s?\\)\\s?]");

    private final ConditionStatement conditionStatement;

    public ArrayEvalFilter(ConditionStatement statement) {
        super(statement.condition);
        this.conditionStatement = statement;
    }



    @Override
    public Object filter(Object obj, Configuration configuration) {
        JsonProvider jsonProvider = configuration.getProvider();
        Iterable<Object> src = null;
        try {
            src = jsonProvider.toIterable(obj);
        } catch (ClassCastException e){
            throw new PathNotFoundException("The path fragment '" + this.condition + "' can not be applied to a JSON object only a JSON array.", e);
        }
        Object result = jsonProvider.createArray();
        for (Object item : src) {
            if (isMatch(item, conditionStatement, configuration)) {
                jsonProvider.setProperty(result, jsonProvider.length(result), item);
            }
        }
        return result;
    }

    @Override
    public Object getRef(Object obj, Configuration configuration) {
        throw new UnsupportedOperationException("");
    }

    @Override
    public boolean isArrayFilter() {
        return true;
    }

    private boolean isMatch(Object check, ConditionStatement conditionStatement, Configuration configuration) {
        try {
            Object value = conditionStatement.path.read(check, configuration.options(Option.THROW_ON_MISSING_PROPERTY));
            return ExpressionEvaluator.eval(value, conditionStatement.getOperator(), conditionStatement.getExpected());
        } catch (PathNotFoundException e){
            return false;
        } catch (RuntimeException e){
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    static ConditionStatement createConditionStatement(String condition) {
        Matcher matcher = PATTERN.matcher(condition);
        if (matcher.matches()) {
            String property = matcher.group(1).trim();
            String operator = matcher.group(2).trim();
            String expected = matcher.group(3).trim();

            return new ConditionStatement(condition, property, operator, expected);
        } else {
            return null;
        }
    }

    static class ConditionStatement {
        private final String condition;
        private final String field;
        private final String operator;
        private final String expected;
        private final JsonPath path;


        ConditionStatement(String condition, String field, String operator, String expected) {
            this.condition = condition;
            this.field = field;
            this.operator = operator;


            if(expected.startsWith("'")){
                this.expected = trim(expected, 1, 1);
            }else{
                this.expected = expected;
            }

            if(field.startsWith("@.")){
                this.path = JsonPath.compile(this.field.replace("@.", "$."));
            } else {
                this.path = JsonPath.compile(this.field.replace("@", "$"));
            }
        }
        ConditionStatement(String field, String operator, String expected) {
            this(null, field, operator, expected);
        }

        String getCondition() {
            return condition;
        }

        public JsonPath getJsonPath() {
            return path;
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

        @Override
        public String toString() {
            return "ConditionStatement{" +
                    "field='" + field + '\'' +
                    ", operator='" + operator + '\'' +
                    ", expected='" + expected + '\'' +
                    '}';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            ConditionStatement that = (ConditionStatement) o;

            if (expected != null ? !expected.equals(that.expected) : that.expected != null) return false;
            if (field != null ? !field.equals(that.field) : that.field != null) return false;
            if (operator != null ? !operator.equals(that.operator) : that.operator != null) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = field != null ? field.hashCode() : 0;
            result = 31 * result + (operator != null ? operator.hashCode() : 0);
            result = 31 * result + (expected != null ? expected.hashCode() : 0);
            return result;
        }
    }
}
