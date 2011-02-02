package com.jayway.jsonpath.filter;

import com.jayway.jsonpath.PathItem;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.Collection;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: kallestenflo
 * Date: 2/2/11
 * Time: 2:01 PM
 */
public abstract class JsonPathFilterBase {




    public abstract List<Object> apply(List<Object> filter);


}
