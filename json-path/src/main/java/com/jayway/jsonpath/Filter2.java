package com.jayway.jsonpath;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;

/**
 *
 */
public class Filter2 implements Predicate {

    private List<Criteria2> criteriaList = new ArrayList<Criteria2>();

    private Filter2(Criteria2 criteria) {
        this.criteriaList.add(criteria);
    }

    private Filter2(List<Criteria2> criteriaList) {
        this.criteriaList = criteriaList;
    }

    public static Filter2 filter(Criteria2 criteria) {
        return new Filter2(criteria);
    }

    public static Filter2 filter(List<Criteria2> criteriaList) {
        return new Filter2(criteriaList);
    }

    @Override
    public boolean apply(Object target, Configuration configuration) {
        for (Criteria2 criteria : criteriaList) {
            if (!criteria.apply(target, configuration)) {
                return false;
            }
        }
        return true;
    }

    public void addCriteria(Criteria2 criteria) {
        criteriaList.add(criteria);
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Criteria2 crit : criteriaList) {
            sb.append(crit.toString());
        }
        return sb.toString();
    }
}
