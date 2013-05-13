/*
 * Copyright 2011 the original author or authors.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.jayway.jsonpath;

import java.util.*;
import java.util.regex.Pattern;

import static java.util.Arrays.asList;
import static org.apache.commons.lang.Validate.notEmpty;
import static org.apache.commons.lang.Validate.notNull;

/**
 * @author Kalle Stenflo
 */
public class Criteria {


    private enum CriteriaType {
        GT,
        GTE,
        LT,
        LTE,
        NE,
        IN,
        NIN,
        ALL,
        SIZE,
        EXISTS,
        TYPE,
        REGEX,
        OR
    }

    /**
     * Custom "not-null" object as we have to be able to work with {@literal null} values as well.
     */
    private static final Object NOT_SET = new Object();

    private final JsonPath key;

    private final List<Criteria> criteriaChain;

    private final LinkedHashMap<CriteriaType, Object> criteria = new LinkedHashMap<CriteriaType, Object>();

    private Object isValue = NOT_SET;


    private Criteria(String key) {
        notEmpty(key, "key can not be null or empty");
        this.criteriaChain = new ArrayList<Criteria>();
        this.criteriaChain.add(this);
        this.key = JsonPath.compile(key);
    }

    private Criteria(List<Criteria> criteriaChain, String key) {
        notEmpty(key, "key can not be null or empty");
        this.criteriaChain = criteriaChain;
        this.criteriaChain.add(this);
        this.key = JsonPath.compile(key);
    }

    public JsonPath getKey() {
        return this.key;
    }

    /**
     * Checks if this criteria matches the given map
     *
     * @param map map to check
     * @return true if criteria is a match
     */
    public boolean matches(Map<String, Object> map) {

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

    private static Object readSafely(JsonPath path, Map<String, Object> map){
      try{
        return path.read(map);
      } catch (InvalidPathException e){
        return null;
      }
    }

    private static <T> boolean objectOrAnyCollectionItemMatches(final Object singleObjectOrCollection,
        final Predicate<T> predicate){
            if (singleObjectOrCollection instanceof Collection) {
                Iterator it = ((Collection) singleObjectOrCollection).iterator();
                while (it.hasNext()) {
                    if (predicate.accept((T) it.next())) {
                        return true;
                }
            }
            return false;
        }
        return predicate.accept((T) singleObjectOrCollection);
    }

    boolean singleObjectApply(Map<String, Object> map) {

        for (CriteriaType key : this.criteria.keySet()) {

            Object actualVal = readSafely(this.key, map);
            final Object expectedVal = this.criteria.get(key);

            if (CriteriaType.GT.equals(key)) {

                if (expectedVal == null || actualVal == null) {
                    return false;
                }

                final Number expectedNumber = (Number) expectedVal;
                return objectOrAnyCollectionItemMatches(actualVal, new Predicate<Number>() {

                  @Override
                  public boolean accept(Number value) {
                    return (value.doubleValue() > expectedNumber.doubleValue());
                  }
                });

            } else if (CriteriaType.GTE.equals(key)) {

                if (expectedVal == null || actualVal == null) {
                    return false;
                }

                final Number expectedNumber = (Number) expectedVal;
                return objectOrAnyCollectionItemMatches(actualVal, new Predicate<Number>() {

                  @Override
                  public boolean accept(Number value) {
                    return (value.doubleValue() >= expectedNumber.doubleValue());
                  }
                });

            } else if (CriteriaType.LT.equals(key)) {

                if (expectedVal == null || actualVal == null) {
                    return false;
                }

                final Number expectedNumber = (Number) expectedVal;
                return objectOrAnyCollectionItemMatches(actualVal, new Predicate<Number>() {

                  @Override
                  public boolean accept(Number value) {
                    return (value.doubleValue() < expectedNumber.doubleValue());
                  }
                });

            } else if (CriteriaType.LTE.equals(key)) {

                if (expectedVal == null || actualVal == null) {
                    return false;
                }

                final Number expectedNumber = (Number) expectedVal;
                return objectOrAnyCollectionItemMatches(actualVal, new Predicate<Number>() {

                  @Override
                  public boolean accept(Number value) {
                    return (value.doubleValue() <= expectedNumber.doubleValue());
                  }
                });

            } else if (CriteriaType.NE.equals(key)) {
                if (expectedVal == null && actualVal == null) {
                    return false;
                }
                if (expectedVal == null) {
                    return true;
                } else {
                    return !expectedVal.equals(actualVal);
                }

            } else if (CriteriaType.IN.equals(key)) {

                Collection exp = (Collection) expectedVal;

                return exp.contains(actualVal);

            } else if (CriteriaType.NIN.equals(key)) {

                Collection exp = (Collection) expectedVal;

                return !exp.contains(actualVal);
            } else if (CriteriaType.ALL.equals(key)) {

                Collection exp = (Collection) expectedVal;
                Collection act = (Collection) actualVal;

                return act.containsAll(exp);

            } else if (CriteriaType.SIZE.equals(key)) {

                int exp = (Integer) expectedVal;
                List act = (List) actualVal;

                return (act.size() == exp);

            } else if (CriteriaType.EXISTS.equals(key)) {

              final boolean exp = (Boolean) expectedVal;
              return objectOrAnyCollectionItemMatches(map, new Predicate<Object>() {

                @Override
                public boolean accept(final Object value) {
                  boolean act = true;
                  try {
                    Object val = getKey().read(value);
                    if(val instanceof Collection){
                      act = !((Collection) val).isEmpty();
                    }
                  } catch (InvalidPathException e) {
                    act = false;
                  }
                  return act == exp;

                }

              });

            } else if (CriteriaType.TYPE.equals(key)) {

                Class<?> exp = (Class<?>) expectedVal;
                Class<?> act = actualVal == null ? null : actualVal.getClass();

                if (act == null) {
                    return false;
                } else {
                    return act.equals(exp);
                }

            } else if (CriteriaType.REGEX.equals(key)) {
                final Pattern exp = (Pattern) expectedVal;
                
                return objectOrAnyCollectionItemMatches(actualVal, new Predicate<String>() {

                  @Override
                  public boolean accept(String value) {
                    return value != null && exp.matcher(value).matches();
                  }
                });
               

            } else {
                throw new UnsupportedOperationException("Criteria type not supported: " + key.name());
            }
        }
        if (isValue != NOT_SET) {

            if (isValue instanceof Collection) {
                Collection<Criteria> cs = (Collection<Criteria>) isValue;
                for (Criteria crit : cs) {
                    for (Criteria c : crit.criteriaChain) {
                        if (!c.singleObjectApply(map)) {
                            return false;
                        }
                    }
                }
                return true;
            } else {
                Object actualVal = readSafely(this.key, map);

                if (isValue == null) {
                    return actualVal == null;
                } else {
                    return isValue.equals(actualVal);
                }
            }
        } else {

        }
        return true;
    }


    /**
     * Static factory method to create a Criteria using the provided key
     *
     * @param key filed name
     * @return the new criteria
     */

    public static Criteria where(String key) {
        return new Criteria(key);
    }

    /**
     * Static factory method to create a Criteria using the provided key
     *
     * @param key ads new filed to criteria
     * @return the criteria builder
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
     * Creates a criterion using equality
     *
     * @param o
     * @return
     */
    public Criteria eq(Object o) {
        return is(o);
    }

    /**
     * Creates a criterion using the <b>!=</b> operator
     *
     * @param o
     * @return
     */
    public Criteria ne(Object o) {
        criteria.put(CriteriaType.NE, o);
        return this;
    }

    /**
     * Creates a criterion using the <b>&lt;</b> operator
     *
     * @param o
     * @return
     */
    public Criteria lt(Object o) {
        criteria.put(CriteriaType.LT, o);
        return this;
    }

    /**
     * Creates a criterion using the <b>&lt;=</b> operator
     *
     * @param o
     * @return
     */
    public Criteria lte(Object o) {
        criteria.put(CriteriaType.LTE, o);
        return this;
    }

    /**
     * Creates a criterion using the <b>&gt;</b> operator
     *
     * @param o
     * @return
     */
    public Criteria gt(Object o) {
        criteria.put(CriteriaType.GT, o);
        return this;
    }

    /**
     * Creates a criterion using the <b>&gt;=</b> operator
     *
     * @param o
     * @return
     */
    public Criteria gte(Object o) {
        criteria.put(CriteriaType.GTE, o);
        return this;
    }

    /**
     * The <code>in</code> operator is analogous to the SQL IN modifier, allowing you
     * to specify an array of possible matches.
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
      * The <code>in</code> operator is analogous to the SQL IN modifier, allowing you
      * to specify an array of possible matches.
      *
      * @param c the collection containing the values to match against
      * @return
      */
    public Criteria in(Collection<?> c) {
        notNull(c, "collection can not be null");
        checkFilterCanBeApplied(CriteriaType.IN);
        criteria.put(CriteriaType.IN, c);
        return this;
    }

    /**
     * The <code>nin</code> operator is similar to $in except that it selects objects for
     * which the specified field does not have any value in the specified array.
     *
     * @param o the values to match against
     * @return
     */
    public Criteria nin(Object... o) {
        return nin(Arrays.asList(o));
    }

    /**
     * The <code>nin</code> operator is similar to $in except that it selects objects for
     * which the specified field does not have any value in the specified array.
     *
     * @param c the values to match against
     * @return
     */
    public Criteria nin(Collection<?> c) {
        notNull(c, "collection can not be null");
        checkFilterCanBeApplied(CriteriaType.NIN);
        criteria.put(CriteriaType.NIN, c);
        return this;
    }


    /**
     * The <code>all</code> operator is similar to $in, but instead of matching any value in the specified array all values in the array must be matched.
     *
     * @param o
     * @return
     */
    public Criteria all(Object... o) {
        return all(Arrays.asList(o));
    }
    /**
     * The <code>all</code> operator is similar to $in, but instead of matching any value in the specified array all values in the array must be matched.
     *
     * @param c
     * @return
     */
    public Criteria all(Collection<?> c) {
        notNull(c, "collection can not be null");
        checkFilterCanBeApplied(CriteriaType.ALL);
        criteria.put(CriteriaType.ALL, c);
        return this;
    }

    /**
     * The <code>size</code> operator matches any array with the specified number of elements.
     *
     * @param s
     * @return
     */
    public Criteria size(int s) {
        checkFilterCanBeApplied(CriteriaType.SIZE);
        criteria.put(CriteriaType.SIZE, s);
        return this;
    }

    /**
     * Check for existence (or lack thereof) of a field.
     *
     * @param b
     * @return
     */
    public Criteria exists(boolean b) {
        criteria.put(CriteriaType.EXISTS, b);
        return this;
    }

    /**
     * The $type operator matches values based on their Java type.
     *
     * @param t
     * @return
     */
    public Criteria type(Class<?> t) {
        notNull(t, "type can not be null");
        criteria.put(CriteriaType.TYPE, t);
        return this;
    }


    /**
     * Creates a criterion using a Regex
     *
     * @param pattern
     * @return
     */
    public Criteria regex(Pattern pattern) {
        notNull(pattern, "pattern can not be null");
        criteria.put(CriteriaType.REGEX, pattern);
        return this;
    }


    /**
     * Creates an 'or' criteria using the $or operator for all of the provided criteria
     *
     * @param criteria
     */
    /*
    public Criteria orOperator(Criteria... criteria) {
        criteriaChain.add(new Criteria("$or").is(asList(criteria)));
        return this;
    }
    */

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
    
    private void checkFilterCanBeApplied(CriteriaType type){
      if (getKey().getTokenizer().size() > 2){
        throw new IllegalArgumentException("Cannot use "+type+" filter on a multi-level path expression");
      }
    }

    
    private interface Predicate<T> {
        boolean accept(T value);
    }

}
