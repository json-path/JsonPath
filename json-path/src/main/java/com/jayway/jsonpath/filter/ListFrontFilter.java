package com.jayway.jsonpath.filter;

import org.json.simple.JSONArray;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by IntelliJ IDEA.
 * User: kallestenflo
 * Date: 2/15/11
 * Time: 8:20 PM
 */
public class ListFrontFilter extends JsonPathFilterBase {


    public static final Pattern PATTERN = Pattern.compile("\\[\\s?:(\\d+)\\s?\\]");               //[ :2 ]

    private final String pathFragment;

    public ListFrontFilter(String pathFragment) {
        this.pathFragment = pathFragment;
    }

    @Override
    public FilterOutput apply(FilterOutput filterItems) {

        List<Object> result = new JSONArray();

        Integer[] index = getListPullIndex();
        for (int i : index) {
            if (indexIsInRange(filterItems.getResultAsList(), i)) {
                result.add(filterItems.getResultAsList().get(i));
            }
        }
        return new FilterOutput(result);
    }


    private Integer[] getListPullIndex() {
        Matcher matcher = PATTERN.matcher(pathFragment);
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
