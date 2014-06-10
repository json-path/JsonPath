package com.jayway.jsonpath;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class Filter implements Predicate {

    private List<Criteria> criteriaList = new ArrayList<Criteria>();

    private Filter(Criteria criteria) {
        this.criteriaList.add(criteria);
    }

    private Filter(List<Criteria> criteriaList) {
        this.criteriaList = criteriaList;
    }

    public static Filter filter(Criteria criteria) {
        return new Filter(criteria);
    }

    public static Filter filter(List<Criteria> criteriaList) {
        return new Filter(criteriaList);
    }

    @Override
    public boolean apply(Object target, Configuration configuration) {
        for (Criteria criteria : criteriaList) {
            if (!criteria.apply(target, configuration)) {
                return false;
            }
        }
        return true;
    }

    public void addCriteria(Criteria criteria) {
        criteriaList.add(criteria);
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Criteria crit : criteriaList) {
            sb.append(crit.toString());
        }
        return sb.toString();
    }
}
