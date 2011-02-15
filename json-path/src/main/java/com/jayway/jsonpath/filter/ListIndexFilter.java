package com.jayway.jsonpath.filter;

import org.json.simple.JSONArray;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by IntelliJ IDEA.
 * User: kallestenflo
 * Date: 2/15/11
 * Time: 8:02 PM
 */
public class ListIndexFilter extends JsonPathFilterBase {

    public static final Pattern PATTERN = Pattern.compile("\\[(\\s?\\d+\\s?,?)+\\]");               //[1] OR [1,2,3]

    private final String pathFragment;

    public ListIndexFilter(String pathFragment) {
        this.pathFragment = pathFragment;
    }

    @Override
    public FilterOutput apply(FilterOutput filterItems) {

        Object result = null;

        Integer[] index = getArrayIndex();
        if (index.length > 1) {
            List<Object> tmp = new JSONArray();
            for (int i : index) {
                if (indexIsInRange(filterItems.getResultAsList(), i)) {
                    tmp.add(filterItems.getResultAsList().get(i));
                }
            }
            result = tmp;
        } else {
            if (indexIsInRange(filterItems.getResultAsList(), index[0])) {
                result = filterItems.getResultAsList().get(index[0]);
            }
        }
        return new FilterOutput(result);
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
}
