package com.jayway.jsonpath.reader.filter;

import com.jayway.jsonpath.spi.JsonProvider;

/**
 * Created by IntelliJ IDEA.
 * User: kallestenflo
 * Date: 11/4/11
 * Time: 10:14 PM
 */
public abstract class Filter {

    protected final String condition;

    public Filter(String condition) {
        this.condition = condition;
    }


    protected String trim(String str, int front, int end) {
        String res = str;

        if (front > 0) {
            res = str.substring(front);
        }
        if (end > 0) {
            res = res.substring(0, res.length() - end);
        }
        return res;
    }

    public abstract Object filter(Object obj, JsonProvider jsonProvider);

}
