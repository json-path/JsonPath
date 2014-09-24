package com.jayway.jsonpath;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 *
 */
public class Filter implements Predicate {

    protected final List<Predicate> criteriaList;

    private Filter() {
        criteriaList = Collections.emptyList();
    }

    private Filter(Predicate criteria) {
        criteriaList = Collections.singletonList(criteria);
    }

    private Filter(List<Predicate> criteriaList) {
        this.criteriaList = new ArrayList<Predicate>(criteriaList);
    }



    /**
     * Creates a new Filter based on given criteria
     * @param criteria criteria
     * @return a new Filter
     */
    public static Filter filter(Predicate criteria) {
        return new Filter(criteria);
    }

    /**
     * Create a new Filter based on given list of criteria.
     * @param criteriaList list of criteria
     * @return
     */
    public static Filter filter(List<Predicate> criteriaList) {
        return new Filter(criteriaList);
    }

    @Override
    public boolean apply(PredicateContext ctx) {
        for (Predicate criteria : criteriaList) {
            if (!criteria.apply(ctx)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Predicate crit : criteriaList) {
            sb.append(crit.toString());
        }
        return sb.toString();
    }

    public Filter or(final Predicate other){
        return new OrFilter(this, other);
    }

    public Filter and(final Predicate other){
        return filter(Arrays.asList(this, other));
    }
    
    private static final class OrFilter extends Filter {

        private final Predicate left;
        private final Predicate right;
  
        private OrFilter(Predicate left, Predicate right) {
            this.left = left;
            this.right = right;
        }
  
        @Override
        public boolean apply(PredicateContext ctx) {
            boolean a = left.apply(ctx);
            return a || right.apply(ctx);
        }
    }
}
