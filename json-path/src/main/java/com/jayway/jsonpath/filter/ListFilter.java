package com.jayway.jsonpath.filter;

import com.jayway.jsonpath.JsonUtil;
import com.jayway.jsonpath.eval.ExpressionEvaluator;
import org.json.simple.JSONArray;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//import javax.script.ScriptEngine;
//import javax.script.ScriptEngineManager;

/**
 * User: kalle stenflo
 * Date: 2/2/11
 * Time: 2:32 PM
 */
public class ListFilter extends JsonPathFilterBase {

    //private static ScriptEngine SCRIPT_ENGINE = new ScriptEngineManager().getEngineByName("js");

    private static final Pattern LIST_INDEX_PATTERN = Pattern.compile("\\[(\\s?\\d+\\s?,?)+\\]");               //[1] OR [1,2,3]
    private static final Pattern LIST_PULL_PATTERN = Pattern.compile("\\[\\s?:(\\d+)\\s?\\]");               //[ :2 ]
    private static final Pattern LIST_WILDCARD_PATTERN = Pattern.compile("\\[\\*\\]");                      //[*]
    private static final Pattern LIST_TAIL_PATTERN_SHORT = Pattern.compile("\\[\\s*-\\s*(\\d+):\\s*\\]");   // [(@.length - 12)] OR [-13:]
    private static final Pattern LIST_TAIL_PATTERN_LONG = Pattern.compile("\\[\\s*\\(\\s*@\\.length\\s*-\\s*(\\d+)\\s*\\)\\s*\\]"); //[(@.length-1)]
    private static final Pattern LIST_TAIL_PATTERN = Pattern.compile("(" + LIST_TAIL_PATTERN_SHORT.pattern() + "|" + LIST_TAIL_PATTERN_LONG.pattern() + ")");
    private static final Pattern LIST_ITEM_HAS_PROPERTY_PATTERN = Pattern.compile("\\[\\s?\\?\\s?\\(\\s?@\\.(\\w+)\\s?\\)\\s?\\]");  //[?(@.title)]
    private static final Pattern LIST_ITEM_MATCHES_EVAL = Pattern.compile("\\[\\s?\\?\\s?\\(\\s?@.(\\w+)\\s?([=<>]+)\\s?(.*)\\s?\\)\\s?\\]");    //[?( @.title< 'ko')]

    private final String pathFragment;

    public ListFilter(String pathFragment) {
        this.pathFragment = pathFragment;
    }

    @Override
    public FilterOutput apply(FilterOutput items) {
        Object result = new JSONArray();

        if (LIST_INDEX_PATTERN.matcher(pathFragment).matches()) {
            result = filterByListIndex(items.getResultAsList());
        } else if (LIST_WILDCARD_PATTERN.matcher(pathFragment).matches()) {
            result = filterByWildcard(items.getResultAsList());
        } else if (LIST_TAIL_PATTERN.matcher(pathFragment).matches()) {
            result = filterByListTailIndex(items.getResultAsList());
        } else if (LIST_PULL_PATTERN.matcher(pathFragment).matches()) {
            result = filterByPullIndex(items.getResultAsList());
        } else if (LIST_ITEM_HAS_PROPERTY_PATTERN.matcher(pathFragment).matches()) {
            result = filterByItemProperty(items.getResultAsList());
        } else if (LIST_ITEM_MATCHES_EVAL.matcher(pathFragment).matches()) {
            result = filterByItemEvalMatch(items.getResultAsList());
        }

        return new FilterOutput(result);
    }

    private List<Object> filterByItemEvalMatch(List<Object> items) {
        List<Object> result = new JSONArray();

        for (Object current : items) {
            for (Object item : items) {
                if (isEvalMatch(item)) {
                    result.add(item);
                }
            }
        }
        return result;
    }


    private List<Object> filterByItemProperty(List<Object> items) {
        List<Object> result = new JSONArray();

        String prop = getFilterProperty();

        //for (Object current : items) {
            for (Object item : JsonUtil.toList(items)) {

                if (JsonUtil.isMap(item)) {
                    if (JsonUtil.toMap(item).containsKey(prop)) {
                        result.add(item);
                    }
                }
            }
        //}
        return result;
    }


    private List<Object> filterByWildcard(List<Object> items) {
        List<Object> result = new JSONArray();

        for (Object current : items) {
            if(current instanceof List){
                result.addAll(JsonUtil.toList(current));
            }
            else {
                result.add(current);
            }
        }
        return result;
    }

    private Object filterByListTailIndex(List<Object> items) {

        //for (Object current : items) {
        //    Map array = JsonUtil.toMap(current);

        int index = getTailIndex(items.size());

        return items.get(index);

    }

    private Object filterByListIndex(List<Object> items) {
         Object result = null;

        //for (Object current : items) {
        //List target = JsonUtil.toList(current);
        Integer[] index = getArrayIndex();
        if (index.length > 1) {
            List<Object> tmp = new JSONArray();
            for (int i : index) {
                if (indexIsInRange(items, i)) {
                    tmp.add(items.get(i));
                }
            }
            return result = tmp;
        } else {
            if (indexIsInRange(items, index[0])) {
                result = items.get(index[0]);
            }
        }
        //}
        return result;
    }

    private List<Object> filterByPullIndex(List<Object> items) {
        List<Object> result = new JSONArray();

        //for (Object current : items) {
            //List target = JsonUtil.toList(current);
            Integer[] index = getListPullIndex();
            for (int i : index) {
                if (indexIsInRange(items, i)) {
                    result.add(items.get(i));
                }
            }
        //}
        return result;
    }

    private boolean isEvalMatch(Object check) {
        Matcher matcher = LIST_ITEM_MATCHES_EVAL.matcher(pathFragment);

        if (matcher.matches()) {
            String property = matcher.group(1);
            String operator = matcher.group(2);
            String expected = matcher.group(3);

            if (!JsonUtil.isMap(check)) {
                return false;
            }
            Map obj = JsonUtil.toMap(check);

            if (!obj.containsKey(property)) {
                return false;
            }

            Object propertyValue = obj.get(property);

            if (JsonUtil.isContainer(propertyValue)) {
                return false;
            }

            String expression = propertyValue + " " + operator + " " + expected;

            return ExpressionEvaluator.eval(propertyValue, operator, expected);

        }
        return false;
    }

    private String getFilterProperty() {
        Matcher matcher = LIST_ITEM_HAS_PROPERTY_PATTERN.matcher(pathFragment);
        if (matcher.matches()) {
            return matcher.group(1);
        }
        throw new IllegalArgumentException("invalid list filter property");
    }

    private int getTailIndex(int arraySize) {

        Matcher matcher = LIST_TAIL_PATTERN_SHORT.matcher(pathFragment);
        if (matcher.matches()) {

            int index = Integer.parseInt(matcher.group(1));

            return arraySize - index;
        }
        matcher = LIST_TAIL_PATTERN_LONG.matcher(pathFragment);
        if (matcher.matches()) {

            int index = Integer.parseInt(matcher.group(1));

            return arraySize - index;
        }

        throw new IllegalArgumentException("invalid list index");

    }

    private Integer[] getListPullIndex() {
        Matcher matcher = LIST_PULL_PATTERN.matcher(pathFragment);
        if (matcher.matches()) {

            int pullCount = Integer.parseInt(matcher.group(1));

            List<Integer> result = new LinkedList<Integer>();

            for (int y = 0; y < pullCount; y++) {
                result.add(y);
            }
            return result.toArray(new Integer[0]);
        }
        throw new IllegalArgumentException("invalid list index");
    }

    private Integer[] getArrayIndex() {

        String prepared = pathFragment.replaceAll(" ", "");
        prepared = prepared.substring(1, prepared.length() - 1);

        List<Integer> index = new LinkedList<Integer>();

        String[] split = prepared.split(",");

        for (String s : split) {
            index.add(Integer.parseInt(s));
        }
        return index.toArray(new Integer[0]);
    }

    private boolean indexIsInRange(List list, int index) {
        if (index < 0) {
            return false;
        } else if (index > list.size() - 1) {
            return false;
        } else {
            return true;
        }
    }

}