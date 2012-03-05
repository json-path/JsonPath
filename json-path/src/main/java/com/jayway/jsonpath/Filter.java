package com.jayway.jsonpath;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: kallestenflo
 * Date: 3/5/12
 * Time: 12:05 PM
 */
public class Filter {

    private HashMap<String, Criteria> criteria = new LinkedHashMap<String, Criteria>();

    public Filter(Criteria criteria) {
        addCriteria(criteria);
    }


    public static Filter filter(Criteria criteria) {
        return new Filter(criteria);
    }


    public Filter addCriteria(Criteria criteria) {
        Criteria existing = this.criteria.get(criteria.getKey());
        String key = criteria.getKey();
        if (existing == null) {
            this.criteria.put(key, criteria);
        } else {
            existing.andOperator(criteria);
        }
        return this;
    }

    protected List<Criteria> getCriteria() {
        return new ArrayList<Criteria>(this.criteria.values());
    }

    public boolean apply(Map<String, Object> map) {

        for (Criteria criterion : getCriteria()) {
            if(!criterion.apply(map)){
                return false;
            }
        }

        return true;

    }

}
