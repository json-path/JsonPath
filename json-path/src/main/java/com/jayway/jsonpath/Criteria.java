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

import com.jayway.jsonpath.internal.Path;
import com.jayway.jsonpath.internal.PathCompiler;
import com.jayway.jsonpath.internal.token.PredicateContextImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

import static com.jayway.jsonpath.internal.Utils.join;
import static com.jayway.jsonpath.internal.Utils.notNull;


/**
 *
 */
@SuppressWarnings("unchecked")
public class Criteria implements Predicate {

    private static final Logger logger = LoggerFactory.getLogger(Criteria.class);

    private static final String[] OPERATORS = {
            CriteriaType.EQ.toString(),
            CriteriaType.GTE.toString(),
            CriteriaType.LTE.toString(),
            CriteriaType.NE.toString(),
            CriteriaType.LT.toString(),
            CriteriaType.GT.toString()
    };

    private final Path path;
    private CriteriaType criteriaType;
    private Object expected;

    private final List<Criteria> criteriaChain;

    private static enum CriteriaType {
        EQ {
            @Override
            boolean eval(Object expected, Object actual, PredicateContext ctx) {
                boolean res = (0 == safeCompare(expected, actual));
                if(logger.isDebugEnabled()) logger.debug("[{}] {} [{}] => {}", actual, name(), expected, res);
                return res;
            }

            @Override
            public String toString() {
                return "==";
            }
        },
        NE {
            @Override
            boolean eval(Object expected, Object actual, PredicateContext ctx) {
                boolean res = (0 != safeCompare(expected, actual));
                if(logger.isDebugEnabled()) logger.debug("[{}] {} [{}] => {}", actual, name(), expected, res);
                return res;
            }

            @Override
            public String toString() {
                return "!=";
            }
        },
        GT {
            @Override
            boolean eval(Object expected, Object actual, PredicateContext ctx) {
                if ((expected == null) ^ (actual == null)) {
                    return false;
                }
                boolean res = (0 > safeCompare(expected, actual));
                if(logger.isDebugEnabled()) logger.debug("[{}] {} [{}] => {}", actual, name(), expected, res);
                return res;
            }
            @Override
            public String toString() {
                return ">";
            }
        },
        GTE {
            @Override
            boolean eval(Object expected, Object actual, PredicateContext ctx) {
                if ((expected == null) ^ (actual == null)) {
                    return false;
                }
                boolean res = (0 >= safeCompare(expected, actual));
                if(logger.isDebugEnabled()) logger.debug("[{}] {} [{}] => {}", actual, name(), expected, res);
                return res;
            }
            @Override
            public String toString() {
                return ">=";
            }
        },
        LT {
            @Override
            boolean eval(Object expected, Object actual, PredicateContext ctx) {
                if ((expected == null) ^ (actual == null)) {
                    return false;
                }
                boolean res = (0 < safeCompare(expected, actual));
                if(logger.isDebugEnabled()) logger.debug("[{}] {} [{}] => {}", actual, name(), expected, res);
                return res;
            }
            @Override
            public String toString() {
                return "<";
            }
        },
        LTE {
            @Override
            boolean eval(Object expected, Object actual, PredicateContext ctx) {
                if ((expected == null) ^ (actual == null)) {
                    return false;
                }
                boolean res = (0 <= safeCompare(expected, actual));
                if(logger.isDebugEnabled()) logger.debug("[{}] {} [{}] => {}", actual, name(), expected, res);
                return res;
            }
            @Override
            public String toString() {
                return "<=";
            }
        },
        IN {
            @Override
            boolean eval(Object expected, Object actual, PredicateContext ctx) {
                boolean res = false;
                Collection exps = (Collection) expected;
                for (Object exp : exps) {
                    if (0 == safeCompare(exp, actual)) {
                        res = true;
                        break;
                    }
                }
                if(logger.isDebugEnabled()) logger.debug("[{}] {} [{}] => {}", actual, name(), join(", ", exps), res);
                return res;
            }
        },
        NIN {
            @Override
            boolean eval(Object expected, Object actual, PredicateContext ctx) {
                Collection nexps = (Collection) expected;
                boolean res = !nexps.contains(actual);
                if(logger.isDebugEnabled()) logger.debug("[{}] {} [{}] => {}", actual, name(), join(", ", nexps), res);
                return res;
            }
        },
        ALL {
            @Override
            boolean eval(Object expected, Object actual, PredicateContext ctx) {
                boolean res = true;
                Collection exps = (Collection) expected;
                if (ctx.configuration().jsonProvider().isArray(actual)) {
                    for (Object exp : exps) {
                        boolean found = false;
                        for (Object check : ctx.configuration().jsonProvider().toIterable(actual)) {
                            if (0 == safeCompare(exp, check)) {
                                found = true;
                                break;
                            }
                        }
                        if (!found) {
                            res = false;
                            break;
                        }
                    }
                    if(logger.isDebugEnabled()) logger.debug("[{}] {} [{}] => {}", join(", ", ctx.configuration().jsonProvider().toIterable(actual)), name(), join(", ", exps), res);
                } else {
                    res = false;
                    if(logger.isDebugEnabled()) logger.debug("[{}] {} [{}] => {}", "<NOT AN ARRAY>", name(), join(", ", exps), res);
                }
                return res;
            }
        },
        SIZE {
            @Override
            boolean eval(Object expected, Object actual, PredicateContext ctx) {
                int size = (Integer) expected;
                boolean res;
                if (ctx.configuration().jsonProvider().isArray(actual)) {
                    int length = ctx.configuration().jsonProvider().length(actual);
                    res = (length == size);
                    if(logger.isDebugEnabled()) logger.debug("Array with size {} {} {} => {}", length, name(), size, res);
                } else if (actual instanceof String) {
                    int length = ((String) actual).length();
                    res = length == size;
                    if(logger.isDebugEnabled()) logger.debug("String with length {} {} {} => {}", length, name(), size, res);
                } else {
                    res = false;
                    if(logger.isDebugEnabled()) logger.debug("{} {} {} => {}", actual == null ? "null" : actual.getClass().getName(), name(), size, res);
                }
                return res;
            }
        },
        EXISTS {
            @Override
            boolean eval(Object expected, Object actual, PredicateContext ctx) {
                //This must be handled outside
                throw new UnsupportedOperationException();
            }
        },
        TYPE {
            @Override
            boolean eval(Object expected, Object actual, PredicateContext ctx) {
                final Class<?> expType = (Class<?>) expected;
                final Class<?> actType = actual == null ? null : actual.getClass();

                return actType != null && expType.isAssignableFrom(actType);
            }
        },
        REGEX {
            @Override
            boolean eval(Object expected, Object actual, PredicateContext ctx) {
                boolean res = false;
                final Pattern pattern = (Pattern) expected;
                if (actual != null && actual instanceof String) {
                    res = pattern.matcher(actual.toString()).matches();
                }
                if(logger.isDebugEnabled()) logger.debug("[{}] {} [{}] => {}", actual, name(), expected.toString(), res);
                return res;
            }
        },
        MATCHES {
            @Override
            boolean eval(Object expected, final Object actual, final PredicateContext ctx) {
                PredicateContextImpl pci = (PredicateContextImpl) ctx;
                Predicate exp = (Predicate) expected;
                return exp.apply(new PredicateContextImpl(actual, ctx.root(), ctx.configuration(), pci.documentPathCache()));
            }
        },
        NOT_EMPTY {
            @Override
            boolean eval(Object expected, Object actual, PredicateContext ctx) {
                boolean res = false;
                if (actual != null) {
                    if (ctx.configuration().jsonProvider().isArray(actual)) {
                        int len = ctx.configuration().jsonProvider().length(actual);
                        res = (0 != len);
                        if(logger.isDebugEnabled())  logger.debug("array length = {} {} => {}", len, name(), res);
                    } else if (actual instanceof String) {
                        int len = ((String) actual).length();
                        res = (0 != len);
                        if(logger.isDebugEnabled()) logger.debug("string length = {} {} => {}", len, name(), res);
                    }
                }
                return res;
            }
        };

        abstract boolean eval(Object expected, Object actual, PredicateContext ctx);

        public static CriteriaType parse(String str) {
            if ("==".equals(str)) {
                return EQ;
            } else if (">".equals(str)) {
                return GT;
            } else if (">=".equals(str)) {
                return GTE;
            } else if ("<".equals(str)) {
                return LT;
            } else if ("<=".equals(str)) {
                return LTE;
            } else if ("!=".equals(str)) {
                return NE;
            } else {
                throw new UnsupportedOperationException("CriteriaType " + str + " can not be parsed");
            }
        }
    }

    private Criteria(List<Criteria> criteriaChain, Path path) {
        if (!path.isDefinite()) {
            throw new InvalidCriteriaException("A criteria path must be definite. The path " + path.toString() + " is not!");
        }
        this.path = path;
        this.criteriaChain = criteriaChain;
        this.criteriaChain.add(this);
    }

    private Criteria(Path path) {
        this(new LinkedList<Criteria>(), path);
    }

    private Criteria(Path path, CriteriaType criteriaType, Object expected) {
        this(new LinkedList<Criteria>(), path);
        this.criteriaType = criteriaType;
        this.expected = expected;
    }


    @Override
    public boolean apply(PredicateContext ctx) {
        for (Criteria criteria : criteriaChain) {
            if (!criteria.eval(ctx)) {
                return false;
            }
        }
        return true;
    }

    private boolean eval(PredicateContext ctx) {
        if (CriteriaType.EXISTS == criteriaType) {
            boolean exists = ((Boolean) expected);
            try {
                Configuration c = Configuration.builder().jsonProvider(ctx.configuration().jsonProvider()).options(Option.REQUIRE_PROPERTIES).build();
                path.evaluate(ctx.item(), ctx.root(), c).getValue();
                return exists;
            } catch (PathNotFoundException e) {
                return !exists;
            }
        } else {
            try {
                final Object actual = path.evaluate(ctx.item(), ctx.root(), ctx.configuration()).getValue();

                Object expectedVal = expected;
                if(expected instanceof Path){
                    Path expectedPath = (Path) expected;

                    if(ctx instanceof PredicateContextImpl){
                        //This will use cache for document ($) queries
                        PredicateContextImpl ctxi = (PredicateContextImpl) ctx;
                        expectedVal = ctxi.evaluate(expectedPath);
                    } else {
                        Object doc = expectedPath.isRootPath()?ctx.root():ctx.item();
                        expectedVal = expectedPath.evaluate(doc, ctx.root(), ctx.configuration()).getValue();
                    }
                }

                return criteriaType.eval(expectedVal, actual, ctx);
            } catch (ValueCompareException e) {
                return false;
            } catch (PathNotFoundException e) {
                return false;
            }
        }
    }


    /**
     * Static factory method to create a Criteria using the provided key
     *
     * @param key filed name
     * @return the new criteria
     */
    public static Criteria where(Path key) {
        return new Criteria(key);
    }

    /**
     * Static factory method to create a Criteria using the provided key
     *
     * @param key filed name
     * @return the new criteria
     */

    public static Criteria where(String key) {
        return where(PathCompiler.compile(key));
    }

    /**
     * Static factory method to create a Criteria using the provided key
     *
     * @param key ads new filed to criteria
     * @return the criteria builder
     */
    public Criteria and(String key) {
        return new Criteria(this.criteriaChain, PathCompiler.compile(key));
    }

    /**
     * Creates a criterion using equality
     *
     * @param o
     * @return the criteria
     */
    public Criteria is(Object o) {
        this.criteriaType = CriteriaType.EQ;
        this.expected = o;
        return this;
    }

    /**
     * Creates a criterion using equality
     *
     * @param o
     * @return the criteria
     */
    public Criteria eq(Object o) {
        return is(o);
    }

    /**
     * Creates a criterion using the <b>!=</b> operator
     *
     * @param o
     * @return the criteria
     */
    public Criteria ne(Object o) {
        this.criteriaType = CriteriaType.NE;
        this.expected = o;
        return this;
    }

    /**
     * Creates a criterion using the <b>&lt;</b> operator
     *
     * @param o
     * @return the criteria
     */
    public Criteria lt(Object o) {
        this.criteriaType = CriteriaType.LT;
        this.expected = o;
        return this;
    }

    /**
     * Creates a criterion using the <b>&lt;=</b> operator
     *
     * @param o
     * @return the criteria
     */
    public Criteria lte(Object o) {
        this.criteriaType = CriteriaType.LTE;
        this.expected = o;
        return this;
    }

    /**
     * Creates a criterion using the <b>&gt;</b> operator
     *
     * @param o
     * @return the criteria
     */
    public Criteria gt(Object o) {
        this.criteriaType = CriteriaType.GT;
        this.expected = o;
        return this;
    }

    /**
     * Creates a criterion using the <b>&gt;=</b> operator
     *
     * @param o
     * @return the criteria
     */
    public Criteria gte(Object o) {
        this.criteriaType = CriteriaType.GTE;
        this.expected = o;
        return this;
    }

    /**
     * Creates a criterion using a Regex
     *
     * @param pattern
     * @return the criteria
     */
    public Criteria regex(Pattern pattern) {
        notNull(pattern, "pattern can not be null");
        this.criteriaType = CriteriaType.REGEX;
        this.expected = pattern;
        return this;
    }

    /**
     * The <code>in</code> operator is analogous to the SQL IN modifier, allowing you
     * to specify an array of possible matches.
     *
     * @param o the values to match against
     * @return the criteria
     */
    public Criteria in(Object... o) {
        return in(Arrays.asList(o));
    }

    /**
     * The <code>in</code> operator is analogous to the SQL IN modifier, allowing you
     * to specify an array of possible matches.
     *
     * @param c the collection containing the values to match against
     * @return the criteria
     */
    public Criteria in(Collection<?> c) {
        notNull(c, "collection can not be null");
        this.criteriaType = CriteriaType.IN;
        this.expected = c;
        return this;
    }

    /**
     * The <code>nin</code> operator is similar to $in except that it selects objects for
     * which the specified field does not have any value in the specified array.
     *
     * @param o the values to match against
     * @return the criteria
     */
    public Criteria nin(Object... o) {
        return nin(Arrays.asList(o));
    }

    /**
     * The <code>nin</code> operator is similar to $in except that it selects objects for
     * which the specified field does not have any value in the specified array.
     *
     * @param c the values to match against
     * @return the criteria
     */
    public Criteria nin(Collection<?> c) {
        notNull(c, "collection can not be null");
        this.criteriaType = CriteriaType.NIN;
        this.expected = c;
        return this;
    }

    /**
     * The <code>all</code> operator is similar to $in, but instead of matching any value
     * in the specified array all values in the array must be matched.
     *
     * @param o
     * @return the criteria
     */
    public Criteria all(Object... o) {
        return all(Arrays.asList(o));
    }

    /**
     * The <code>all</code> operator is similar to $in, but instead of matching any value
     * in the specified array all values in the array must be matched.
     *
     * @param c
     * @return the criteria
     */
    public Criteria all(Collection<?> c) {
        notNull(c, "collection can not be null");
        this.criteriaType = CriteriaType.ALL;
        this.expected = c;
        return this;
    }

    /**
     * The <code>size</code> operator matches:
     * <p/>
     * <ol>
     * <li>array with the specified number of elements.</li>
     * <li>string with given length.</li>
     * </ol>
     *
     * @param size
     * @return the criteria
     */
    public Criteria size(int size) {
        this.criteriaType = CriteriaType.SIZE;
        this.expected = size;
        return this;
    }


    /**
     * Check for existence (or lack thereof) of a field.
     *
     * @param b
     * @return the criteria
     */
    public Criteria exists(boolean b) {
        this.criteriaType = CriteriaType.EXISTS;
        this.expected = b;
        return this;
    }

    /**
     * The $type operator matches values based on their Java type.
     *
     * @param t
     * @return the criteria
     */
    public Criteria type(Class<?> t) {
        notNull(t, "type can not be null");
        this.criteriaType = CriteriaType.TYPE;
        this.expected = t;
        return this;
    }

    /**
     * The <code>notEmpty</code> operator checks that an array or String is not empty.
     *
     * @return the criteria
     */
    public Criteria notEmpty() {
        this.criteriaType = CriteriaType.NOT_EMPTY;
        this.expected = null;
        return this;
    }

    /**
     * The <code>matches</code> operator checks that an object matches the given predicate.
     *
     * @param p
     * @return the criteria
     */
    public Criteria matches(Predicate p) {
        this.criteriaType = CriteriaType.MATCHES;
        this.expected = p;
        return this;
    }

    /**
     * Creates a new criteria
     * @param path path to evaluate in criteria
     * @param operator operator
     * @param expected expected value
     * @return a new Criteria
     */
    public static Criteria create(String path, String operator, String expected) {
        if (!expected.isEmpty() && expected.charAt(0) == '\'' && expected.charAt(expected.length() - 1) == '\'') {
            expected = expected.substring(1, expected.length() - 1);
        }

        Path p = PathCompiler.compile(path);

        if (("$".equals(path) || "@".equals(path) )&& (operator == null || operator.isEmpty()) && (expected == null || expected.isEmpty())) {
            return new Criteria(p, CriteriaType.NE, null);
        } else if (operator.isEmpty()) {
            return Criteria.where(path).exists(true);
        } else {
            if(expected.startsWith("$") || expected.startsWith("@")){
                Path compile = PathCompiler.compile(expected);
                if(!compile.isDefinite()){
                    throw new InvalidPathException("the predicate path: " + expected + " is not definite");
                }
                return new Criteria(p, CriteriaType.parse(operator), compile);
            } else {
                return new Criteria(p, CriteriaType.parse(operator), expected);
            }

        }
    }

    private static int safeCompare(Object expected, Object providerParsed) throws ValueCompareException {

        if(expected == providerParsed){
            return 0;
        }

        boolean expNullish = isNullish(expected);
        boolean provNullish = isNullish(providerParsed);

        if (expNullish && !provNullish) {
            return -1;
        } else if (!expNullish && provNullish) {
            return 1;
        } else if (expNullish && provNullish) {
            return 0;
        } else if (expected instanceof String && providerParsed instanceof String) {
            return ((String) expected).compareTo((String) providerParsed);
        } else if (expected instanceof Number && providerParsed instanceof Number) {
            return new BigDecimal(expected.toString()).compareTo(new BigDecimal(providerParsed.toString()));
        } else if (expected instanceof String && providerParsed instanceof Number) {
            return new BigDecimal(expected.toString()).compareTo(new BigDecimal(providerParsed.toString()));
        } else if (expected instanceof String && providerParsed instanceof Boolean) {
            Boolean e = Boolean.valueOf((String) expected);
            Boolean a = (Boolean) providerParsed;
            return e.compareTo(a);
        } else if (expected instanceof Boolean && providerParsed instanceof Boolean) {
            Boolean e = (Boolean) expected;                          
            Boolean a = (Boolean) providerParsed;
            return e.compareTo(a);
        } else {
            logger.debug("Can not compare a {} with a {}", expected.getClass().getName(), providerParsed.getClass().getName());
            throw new ValueCompareException();
        }
    }

    private static boolean isNullish(Object o) {
        return (o == null || ((o instanceof String) && ("null".equals(o))));
    }



    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(path.toString())
                //.append("|")
                .append(criteriaType.toString())
                //.append("|")
                .append(wrapString(expected));
                //.append("|");
        return sb.toString();
    }

    private String wrapString(Object o){
        if(o == null){
            return "null";
        }
        if(o instanceof String){
            return "'" + o.toString() + "'";
        } else {
            return o.toString();
        }
    }



    public static Criteria parse(String criteria){

        int operatorIndex = -1;
        String left = "";
        String operator = "";
        String right = "";
        for (int y = 0; y < OPERATORS.length; y++) {
            operatorIndex = criteria.indexOf(OPERATORS[y]);
            if (operatorIndex != -1) {
                operator = OPERATORS[y];
                break;
            }
        }
        if (!operator.isEmpty()) {
            left = criteria.substring(0, operatorIndex).trim();
            right = criteria.substring(operatorIndex + operator.length()).trim();
        } else {
            left = criteria.trim();
        }
        return Criteria.create(left, operator, right);
    }
}
