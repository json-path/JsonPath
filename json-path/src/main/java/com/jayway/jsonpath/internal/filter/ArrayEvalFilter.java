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

    private static final Pattern CONDITION_STATEMENT_PATTERN = Pattern.compile("\\[\\s?\\?\\(.*\\)\\s?]");
    private static final Pattern CONDITION_PATTERN = Pattern.compile("\\s?(@.*?)\\s?([!=<>]+)\\s?(.*?)\\s?");
    private static final Pattern HASPATH_PATTERN = Pattern.compile("\\s?(@.*)\\s?(.*?)\\s?");
    private static final Pattern REGEX_PATTERN = Pattern.compile("\\s?\\/(.*)\\/\\.test\\((@\\..*)\\)\\s?");

    private Expression[] expressions;

    public ArrayEvalFilter(String condition) {
        super(condition);

        // [?(@.name == 'Luke Skywalker' && @.occupation == 'Farm boy')]
        // [?(@.name == 'Luke Skywalker')]

        condition = condition.trim();
        condition = condition.substring(3, condition.length()-2);

        String[] split = condition.split("&&");

        expressions = new Expression[split.length];
        for (int i = 0; i < split.length; i++) {
            expressions[i] = createExpression(split[i]);
        }
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
            if (isMatch(item, configuration, expressions)) {
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

    private boolean isMatch(Object check, Configuration configuration, Expression... expressions) {
        for (Expression expression: expressions) {
            boolean match =  expression.evaluate(check, configuration);
            if(!match){
                return false;
            }
        }
        return true;
    }

    static boolean isConditionStatement(String condition) {
        return CONDITION_STATEMENT_PATTERN.matcher(condition).matches();
    }

    static Expression createExpression(String condition) {
        Matcher conditionMatcher = CONDITION_PATTERN.matcher(condition);
        if (conditionMatcher.matches()) {
            String property = conditionMatcher.group(1).trim();
            String operator = conditionMatcher.group(2).trim();
            String expected = conditionMatcher.group(3).trim();
            return new OperatorExpression(condition, property, operator, expected);
        }
        Matcher hasPathMatcher = HASPATH_PATTERN.matcher(condition);
        if (hasPathMatcher.matches()) {
            // evaluates @ or @.foo in expressions
            return new HasPathExpression(condition);
        }
        Matcher regexMatcher = REGEX_PATTERN.matcher(condition);
        if (regexMatcher.matches()) {
            String regex = regexMatcher.group(1).trim();
            String field = regexMatcher.group(2).trim();
            return new RegexExpression(regex, field);
        }
        return null;
    }

    static abstract class Expression {
        public abstract boolean evaluate(Object check, Configuration configuration);
    }

    static class OperatorExpression extends Expression {
        private final String condition;
        private final String field;
        private final String operator;
        private final String expected;
        private final JsonPath path;


        OperatorExpression(String condition, String field, String operator, String expected) {
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
        OperatorExpression(String field, String operator, String expected) {
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
        public boolean evaluate(Object check, Configuration configuration) {
            try {
                Object value = (check != null) ? path.read(check, configuration.options(Option.THROW_ON_MISSING_PROPERTY)) : null;
                return ExpressionEvaluator.eval(value, operator, expected);
            } catch (PathNotFoundException e){
                return false;
            }
        }

        @Override
        public String toString() {
            return "OperatorExpression{" +
                    "field='" + field + '\'' +
                    ", operator='" + operator + '\'' +
                    ", expected='" + expected + '\'' +
                    '}';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            OperatorExpression that = (OperatorExpression) o;

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

    static class HasPathExpression extends Expression {
        private final JsonPath path;

        public HasPathExpression(String condition) {
            if(condition.startsWith("@.")){
                this.path = JsonPath.compile(condition.replace("@.", "$."));
            } else {
                this.path = JsonPath.compile(condition.replace("@", "$"));
            }
        }

        @Override
        public boolean evaluate(Object obj, Configuration configuration) {
            JsonProvider jsonProvider = configuration.getProvider();

            if(jsonProvider.isMap(obj)){
                try{
                    path.read(obj, Configuration.builder().options(Option.THROW_ON_MISSING_PROPERTY).jsonProvider(jsonProvider).build());
                    return true;
                } catch (PathNotFoundException e){
                    return false;
                }
            }
            return false;
        }
    }

    static class RegexExpression extends Expression {
        private Pattern pattern;
        private JsonPath path;

        public RegexExpression(String regex, String field) {
            pattern = Pattern.compile(regex);
            if(field.startsWith("@.")){
                this.path = JsonPath.compile(field.replace("@.", "$."));
            } else {
                this.path = JsonPath.compile(field.replace("@", "$"));
            }
        }

        @Override
        public boolean evaluate(Object obj, Configuration configuration) {
            JsonProvider jsonProvider = configuration.getProvider();

            if(jsonProvider.isMap(obj)){
                try{
                    Object value = (obj != null) ? path.read(obj, configuration.options(Option.THROW_ON_MISSING_PROPERTY)) : null;
                    return (value instanceof String) && pattern.matcher((String)value).find();
                } catch (PathNotFoundException e){
                    return false;
                }
            }
            return false;
        }

    }
}
