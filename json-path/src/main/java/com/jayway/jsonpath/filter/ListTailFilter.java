package com.jayway.jsonpath.filter;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by IntelliJ IDEA.
 * User: kallestenflo
 * Date: 2/15/11
 * Time: 8:16 PM
 */
public class ListTailFilter extends JsonPathFilterBase {


    private static final Pattern LIST_TAIL_PATTERN_SHORT = Pattern.compile("\\[\\s*-\\s*(\\d+):\\s*\\]");   // [(@.length - 12)] OR [-13:]
    private static final Pattern LIST_TAIL_PATTERN_LONG = Pattern.compile("\\[\\s*\\(\\s*@\\.length\\s*-\\s*(\\d+)\\s*\\)\\s*\\]"); //[(@.length-1)]

    public static final Pattern PATTERN = Pattern.compile("(" + LIST_TAIL_PATTERN_SHORT.pattern() + "|" + LIST_TAIL_PATTERN_LONG.pattern() + ")");

    private final String pathFragment;

    public ListTailFilter(String pathFragment) {
        this.pathFragment = pathFragment;
    }

    @Override
    public FilterOutput apply(FilterOutput filterItems) {

        int index = getTailIndex(filterItems.getResultAsList().size());

        return new FilterOutput(filterItems.getResultAsList().get(index));
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
}
