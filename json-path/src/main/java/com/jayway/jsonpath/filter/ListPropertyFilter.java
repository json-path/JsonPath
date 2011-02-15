package com.jayway.jsonpath.filter;

import com.jayway.jsonpath.JsonUtil;
import org.json.simple.JSONArray;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by IntelliJ IDEA.
 * User: kallestenflo
 * Date: 2/15/11
 * Time: 8:23 PM
 */
public class ListPropertyFilter extends JsonPathFilterBase {

    public static final Pattern PATTERN = Pattern.compile("\\[\\s?\\?\\s?\\(\\s?@\\.(\\w+)\\s?\\)\\s?\\]");  //[?(@.title)]

    private final String pathFragment;

    public ListPropertyFilter(String pathFragment) {
        this.pathFragment = pathFragment;
    }

    @Override
    public FilterOutput apply(FilterOutput filterItems) {

        List<Object> result = new JSONArray();

        String prop = getFilterProperty();

        for (Object item : filterItems.getResultAsList()) {

            if (JsonUtil.isMap(item)) {
                if (JsonUtil.toMap(item).containsKey(prop)) {
                    result.add(item);
                }
            }
        }
        return new FilterOutput(result);
    }


    private String getFilterProperty() {
        Matcher matcher = PATTERN.matcher(pathFragment);
        if (matcher.matches()) {
            return matcher.group(1);
        }
        throw new IllegalArgumentException("invalid list filter property");
    }
}
