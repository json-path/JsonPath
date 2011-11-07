package com.jayway.jsonpath.reader.filter;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: kallestenflo
 * Date: 11/4/11
 * Time: 11:25 PM
 */
public class ArrayIndexFilter extends Filter {
    public ArrayIndexFilter(String condition) {
        super(condition);
    }

    @Override
    public Object filter(Object obj) {

        List<Object> src = toList(obj);
        List<Object> result = new LinkedList<Object>();

        String trimmedCondition = trim(condition, 1, 1);

        if (trimmedCondition.startsWith(":")) {
            trimmedCondition = trim(trimmedCondition, 1, 0);
            int get = Integer.parseInt(trimmedCondition);
            for (int i = 0; i < get; i++) {
                result.add(src.get(i));
            }
            return result;

        } else if (trimmedCondition.endsWith(":")) {
            trimmedCondition = trim(trimmedCondition, 1, 1);
            int get = Integer.parseInt(trimmedCondition);
            return src.get(src.size() - get);
        } else {
            String[] indexArr = trimmedCondition.split(",");

            if (indexArr.length == 1) {

                return src.get(Integer.parseInt(indexArr[0]));

            } else {
                for (String idx : indexArr) {
                    result.add(src.get(Integer.parseInt(idx.trim())));
                }
                return result;
            }
        }
    }
}
