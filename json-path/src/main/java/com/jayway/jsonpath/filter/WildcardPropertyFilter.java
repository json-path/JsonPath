package com.jayway.jsonpath.filter;

import com.jayway.jsonpath.JsonUtil;
import net.minidev.json.JSONArray;

import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by IntelliJ IDEA.
 * User: kallestenflo
 * Date: 2/15/11
 * Time: 8:42 PM
 */
public class WildcardPropertyFilter extends JsonPathFilterBase {

    public final static Pattern PATTERN = Pattern.compile("\\*");


    @Override
    public FilterOutput apply(FilterOutput filter) {

        List<Object> result = new JSONArray();

        if (filter.isList()) {
            for (Object current : filter.getResultAsList()) {
                for (Object value : JsonUtil.toMap(current).values()) {
                    result.add(value);
                }
            }
        } else {
            for (Object value : JsonUtil.toMap(filter.getResult()).values()) {
                result.add(value);
            }
        }
        return new FilterOutput(result);

    }
}
