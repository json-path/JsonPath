package com.jayway.jsonpath;

import javax.lang.model.type.NullType;
import java.util.*;
import java.util.regex.Pattern;

import static java.util.Arrays.asList;

/**
 * Created by IntelliJ IDEA.
 * User: kallestenflo
 * Date: 3/5/12
 * Time: 12:08 PM
 */
public class Criteria {


    /**
     * Custom "not-null" object as we have to be able to work with {@literal null} values as well.
     */
    private static final Object NOT_SET = new Object();

    private String key;

    private List<Criteria> criteriaChain;

    private LinkedHashMap<String, Object> criteria = new LinkedHashMap<String, Object>();

    private Object isValue = NOT_SET;


    public Criteria(String key) {
        this.criteriaChain = new ArrayList<Criteria>();
        this.criteriaChain.add(this);
        this.key = key;
    }

    protected Criteria(List<Criteria> criteriaChain, String key) {
        this.criteriaChain = criteriaChain;
        this.criteriaChain.add(this);
        this.key = key;
    }

    public String getKey() {
        return this.key;
    }

    public boolean apply(Map<String, Object> map) {

        if (this.criteriaChain.size() == 1) {
            return criteriaChain.get(0).singleObjectApply(map);
        } else {

            for (Criteria c : this.criteriaChain) {

                if (!c.singleObjectApply(map)) {
                    return false;
                }
            }
            return true;
        }
    }

    protected boolean singleObjectApply(Map<String, Object> map) {

        boolean not = false;
        for (String key : this.criteria.keySet()) {
            Object expectedVal = null;
            Object actualVal = map.get(this.key);
            if (not) {
                expectedVal = this.criteria.get(key);
                not = false;
            } else {
                if ("$not".equals(key)) {
                    not = true;
                } else {
                    expectedVal = this.criteria.get(key);
                    if ("$gt".equals(key)) {

                        Number expectedNumber = (Number) expectedVal;
                        Number actualNumber = (Number) actualVal;

                        return (actualNumber.doubleValue() > expectedNumber.doubleValue());

                    } else if ("$gte".equals(key)) {

                        Number expectedNumber = (Number) expectedVal;
                        Number actualNumber = (Number) actualVal;

                        return (actualNumber.doubleValue() >= expectedNumber.doubleValue());

                    } else if ("$lt".equals(key)) {

                        Number expectedNumber = (Number) expectedVal;
                        Number actualNumber = (Number) actualVal;

                        return (actualNumber.doubleValue() < expectedNumber.doubleValue());

                    } else if ("$lte".equals(key)) {

                        Number expectedNumber = (Number) expectedVal;
                        Number actualNumber = (Number) actualVal;

                        return (actualNumber.doubleValue() <= expectedNumber.doubleValue());

                    } else if ("$ne".equals(key)) {

                        return !expectedVal.equals(actualVal);

                    } else if ("$in".equals(key)) {

                        Collection exp = (Collection) expectedVal;

                        return exp.contains(actualVal);

                    } else if ("$nin".equals(key)) {

                        Collection exp = (Collection) expectedVal;

                        return !exp.contains(actualVal);
                    } else if ("$all".equals(key)) {

                        Collection exp = (Collection) expectedVal;
                        Collection act = (Collection) actualVal;

                        return act.containsAll(exp);

                    } else if ("$size".equals(key)) {

                        int exp = (Integer) expectedVal;
                        List act = (List) actualVal;

                        return (act.size() == exp);

                    } else if ("$exists".equals(key)) {

                        boolean exp = (Boolean) expectedVal;
                        boolean act = map.containsKey(this.key);

                        return act == exp;

                    } else if ("$type".equals(key)) {

                        Class<?> exp = (Class<?>) expectedVal;
                        Class<?> act = map.containsKey(this.key) ? map.get(this.key).getClass() : NullType.class;

                        return act.equals(exp);

                    } else if ("$regex".equals(key)) {


                        Pattern exp = (Pattern) expectedVal;
                        String act = (String) actualVal;

                        return exp.matcher(act).matches();

                    } else if ("$or".equals(key)) {


                        System.out.println("auch");
                    }


                }


            }
        }
        if (isValue != NOT_SET) {
            return isValue.equals(map.get(key));
        } else {

        }
        return true;
    }


    /**
     * Static factory method to create a Criteria using the provided key
     *
     * @param key
     * @return
     */

    public static Criteria where(String key) {
        return new Criteria(key);
    }

    /**
     * Static factory method to create a Criteria using the provided key
     *
     * @return
     */
    public Criteria and(String key) {
        return new Criteria(this.criteriaChain, key);
    }

    /**
     * Creates a criterion using equality
     *
     * @param o
     * @return
     */
    public Criteria is(Object o) {
        if (isValue != NOT_SET) {
            throw new InvalidCriteriaException(
                    "Multiple 'is' values declared. You need to use 'and' with multiple criteria");
        }
        if (this.criteria.size() > 0 && "$not".equals(this.criteria.keySet().toArray()[this.criteria.size() - 1])) {
            throw new InvalidCriteriaException("Invalid query: 'not' can't be used with 'is' - use 'ne' instead.");
        }
        this.isValue = o;
        return this;
    }

    /**
     * Creates a criterion using the $ne operator
     *
     * @param o
     * @return
     */
    public Criteria ne(Object o) {
        criteria.put("$ne", o);
        return this;
    }

    /**
     * Creates a criterion using the $lt operator
     *
     * @param o
     * @return
     */
    public Criteria lt(Object o) {
        criteria.put("$lt", o);
        return this;
    }

    /**
     * Creates a criterion using the $lte operator
     *
     * @param o
     * @return
     */
    public Criteria lte(Object o) {
        criteria.put("$lte", o);
        return this;
    }

    /**
     * Creates a criterion using the $gt operator
     *
     * @param o
     * @return
     */
    public Criteria gt(Object o) {
        criteria.put("$gt", o);
        return this;
    }

    /**
     * Creates a criterion using the $gte operator
     *
     * @param o
     * @return
     */
    public Criteria gte(Object o) {
        criteria.put("$gte", o);
        return this;
    }

    /**
     * Creates a criterion using the $in operator
     *
     * @param o the values to match against
     * @return
     */
    public Criteria in(Object... o) {
        if (o.length > 1 && o[1] instanceof Collection) {
            throw new InvalidCriteriaException("You can only pass in one argument of type "
                    + o[1].getClass().getName());
        }
        return in(Arrays.asList(o));
    }

    /**
     * Creates a criterion using the $in operator
     *
     * @param c the collection containing the values to match against
     * @return
     */
    public Criteria in(Collection<?> c) {
        criteria.put("$in", c);
        return this;
    }

    /**
     * Creates a criterion using the $nin operator
     *
     * @param o
     * @return
     */
    public Criteria nin(Object... o) {
        return nin(Arrays.asList(o));
    }

    public Criteria nin(Collection<?> o) {
        criteria.put("$nin", o);
        return this;
    }



    /**
     * Creates a criterion using the $all operator
     *
     * @param o
     * @return
     */
    public Criteria all(Object... o) {
        return all(Arrays.asList(o));
    }

    public Criteria all(Collection<?> o) {
        criteria.put("$all", o);
        return this;
    }

    /**
     * Creates a criterion using the $size operator
     *
     * @param s
     * @return
     */
    public Criteria size(int s) {
        criteria.put("$size", s);
        return this;
    }

    /**
     * Creates a criterion using the $exists operator
     *
     * @param b
     * @return
     */
    public Criteria exists(boolean b) {
        criteria.put("$exists", b);
        return this;
    }

    /**
     * Creates a criterion using the $type operator
     *
     * @param t
     * @return
     */
    public Criteria type(Class<?> t) {
        criteria.put("$type", t);
        return this;
    }

    /**
     * Creates a criterion using the $not meta operator which affects the clause directly following
     *
     * @return
     */
    public Criteria not() {
        criteria.put("$not", null);
        return this;
    }


    /**
     * Creates a criterion using a $regex and $options
     *
     * @param pattern
     * @return
     */
    public Criteria regex(Pattern pattern) {
        criteria.put("$regex", pattern);

        return this;
    }

    /**
     * Creates a criterion using the $mod operator
     *
     * @param value
     * @param remainder
     * @return
     */
    /*
    public Criteria mod(Number value, Number remainder) {
        List<Object> l = new ArrayList<Object>();
        l.add(value);
        l.add(remainder);
        criteria.put("$mod", l);
        return this;
    }
    */

    /**
     * Creates an 'or' criteria using the $or operator for all of the provided criteria
     *
     * @param criteria
     */
    public Criteria orOperator(Criteria... criteria) {
        criteriaChain.add(new Criteria("$or").is(asList(criteria)));
        return this;
    }

    /**
     * Creates a 'nor' criteria using the $nor operator for all of the provided criteria
     *
     * @param criteria
     */
    /*
    public Criteria norOperator(Criteria... criteria) {
        criteriaChain.add(new Criteria("$nor").is(asList(criteria)));
        return this;
    }*/

    /**
     * Creates an 'and' criteria using the $and operator for all of the provided criteria
     *
     * @param criteria
     */
    public Criteria andOperator(Criteria... criteria) {
        criteriaChain.add(new Criteria("$and").is(asList(criteria)));
        return this;
    }



}
