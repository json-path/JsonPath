package com.jayway.jsonpath.reader.filter;

import com.jayway.jsonpath.InvalidPathException;
import com.jayway.jsonpath.reader.filter.eval.ExpressionEvaluator;
import com.jayway.jsonpath.spi.JsonProvider;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by IntelliJ IDEA.
 * User: kallestenflo
 * Date: 11/5/11
 * Time: 12:35 AM
 */
public class ArrayEvalFilter extends Filter {

    public static final Pattern PATTERN = Pattern.compile("(.*?)\\s?([=<>]+)\\s?(.*)");

    public ArrayEvalFilter(String condition) {
        super(condition);
    }

    @Override
    public Object filter(Object obj, JsonProvider jsonProvider) {
        //[?(@.isbn = 10)]
        List<Object> src = jsonProvider.toList(obj);
        List<Object> result = jsonProvider.createList();

        String trimmedCondition = trim(condition, 5, 2);

        ConditionStatement conditionStatement = createConditionStatement(trimmedCondition);

        for (Object item : src) {
            if (isMatch(item, conditionStatement, jsonProvider)) {
                result.add(item);
            }
        }
        return result;
    }

    private boolean isMatch(Object check, ConditionStatement conditionStatement, JsonProvider jsonProvider) {
        if (!jsonProvider.isMap(check)) {
            return false;
        }
        Map obj = jsonProvider.toMap(check);

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
            this.operator = operator;
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
