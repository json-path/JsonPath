package com.jayway.jsonpath;

import com.jayway.jsonpath.internal.spi.compiler.PathCompiler;
import com.jayway.jsonpath.spi.compiler.EvaluationContext;
import com.jayway.jsonpath.spi.compiler.Path;
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
public class Criteria2 implements Predicate {

    private static final Logger logger = LoggerFactory.getLogger(Criteria2.class);

    private final Path path;
    private CriteriaType criteriaType;
    private Object expected;

    private final List<Criteria2> criteriaChain;

    private enum CriteriaType {
        EQ {
            @Override
            boolean eval(Object expected, Object actual, Configuration configuration) {
                boolean res = (0 == safeCompare(expected, actual, configuration));
                logger.debug("[{}] {} [{}] => {}", actual, name(), expected, res);
                return res;
            }
        },
        NE {
            @Override
            boolean eval(Object expected, Object actual, Configuration configuration) {
                boolean res = (0 != safeCompare(expected, actual, configuration));
                logger.debug("[{}] {} [{}] => {}", actual, name(), expected, res);
                return res;
            }
        },
        GT {
            @Override
            boolean eval(Object expected, Object actual, Configuration configuration) {
                if ((expected == null) ^ (actual == null)) {
                    return false;
                }
                boolean res = (0 > safeCompare(expected, actual, configuration));
                logger.debug("[{}] {} [{}] => {}", actual, name(), expected, res);
                return res;
            }
        },
        GTE {
            @Override
            boolean eval(Object expected, Object actual, Configuration configuration) {
                if ((expected == null) ^ (actual == null)) {
                    return false;
                }
                boolean res = (0 >= safeCompare(expected, actual, configuration));
                logger.debug("[{}] {} [{}] => {}", actual, name(), expected, res);
                return res;
            }
        },
        LT {
            @Override
            boolean eval(Object expected, Object actual, Configuration configuration) {
                if ((expected == null) ^ (actual == null)) {
                    return false;
                }
                boolean res = (0 < safeCompare(expected, actual, configuration));
                logger.debug("[{}] {} [{}] => {}", actual, name(), expected, res);
                return res;
            }
        },
        LTE {
            @Override
            boolean eval(Object expected, Object actual, Configuration configuration) {
                if ((expected == null) ^ (actual == null)) {
                    return false;
                }
                boolean res = (0 <= safeCompare(expected, actual, configuration));
                logger.debug("[{}] {} [{}] => {}", actual, name(), expected, res);
                return res;
            }
        },
        IN {
            @Override
            boolean eval(Object expected, Object actual, Configuration configuration) {
                boolean res = false;
                Collection exps = (Collection) expected;
                for (Object exp : exps) {
                    if (0 == safeCompare(exp, actual, configuration)) {
                        res = true;
                        break;
                    }
                }
                logger.debug("[{}] {} [{}] => {}", actual, name(), join(", ", exps), res);
                return res;
            }
        },
        NIN {
            @Override
            boolean eval(Object expected, Object actual, Configuration configuration) {
                Collection nexps = (Collection) expected;
                boolean res = !nexps.contains(actual);
                logger.debug("[{}] {} [{}] => {}", actual, name(), join(", ", nexps), res);
                return res;
            }
        },
        ALL {
            @Override
            boolean eval(Object expected, Object actual, Configuration configuration) {
                boolean res = true;
                Collection exps = (Collection) expected;
                if (configuration.getProvider().isArray(actual)) {
                    for (Object exp : exps) {
                        boolean found = false;
                        for (Object check : configuration.getProvider().toIterable(actual)) {
                            if (0 == safeCompare(exp, check, configuration)) {
                                found = true;
                                break;
                            }
                        }
                        if (!found) {
                            res = false;
                            break;
                        }
                    }
                    logger.debug("[{}] {} [{}] => {}", join(", ", configuration.getProvider().toIterable(actual)), name(), join(", ", exps), res);
                } else {
                    res = false;
                    logger.debug("[{}] {} [{}] => {}", "<NOT AN ARRAY>", name(), join(", ", exps), res);
                }
                return res;
            }
        },
        SIZE {
            @Override
            boolean eval(Object expected, Object actual, Configuration configuration) {
                int size = (Integer) expected;
                boolean res;
                if (configuration.getProvider().isArray(actual)) {
                    int length = configuration.getProvider().length(actual);
                    res = length == size;
                    logger.debug("Array with size {} {} {} => {}", length, name(), size, res);
                } else if (actual instanceof String) {
                    int length = ((String) actual).length();
                    res = length == size;
                    logger.debug("String with length {} {} {} => {}", length, name(), size, res);
                } else {
                    res = false;
                    logger.debug("{} {} {} => {}", actual == null ? "null" : actual.getClass().getName(), name(), size, res);
                }
                return res;
            }
        },
        EXISTS {
            @Override
            boolean eval(Object expected, Object actual, Configuration configuration) {
                //This must be handled outside
                throw new UnsupportedOperationException();
            }
        },
        TYPE {
            @Override
            boolean eval(Object expected, Object actual, Configuration configuration) {
                final Class<?> expType = (Class<?>) expected;
                final Class<?> actType = actual == null ? null : actual.getClass();
                if (actType != null) {
                    return expType.isAssignableFrom(actType);
                }
                return false;
            }
        },
        REGEX {
            @Override
            boolean eval(Object expected, Object actual, Configuration configuration) {
                boolean res = false;
                final Pattern pattern = (Pattern) expected;
                if (actual != null && actual instanceof String) {
                    res = pattern.matcher(actual.toString()).matches();
                }
                logger.debug("[{}] {} [{}] => {}", actual, name(), expected.toString(), res);
                return res;
            }
        },
        MATCHES {
            @Override
            boolean eval(Object expected, Object actual, Configuration configuration) {
                Predicate exp = (Predicate) expected;
                return exp.apply(actual, configuration);
            }
        },
        NOT_EMPTY {
            @Override
            boolean eval(Object expected, Object actual, Configuration configuration) {
                boolean res = false;
                if (actual != null) {
                    if (configuration.getProvider().isArray(actual)) {
                        int len = configuration.getProvider().length(actual);
                        res = (0 != len);
                        logger.debug("array length = {} {} => {}", len, name(), res);
                    } else if (actual instanceof String) {
                        int len = ((String) actual).length();
                        res = (0 != len);
                        logger.debug("string length = {} {} => {}", len, name(), res);
                    }
                }
                return res;
            }
        };

        abstract boolean eval(Object expected, Object actual, Configuration configuration);

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

    private Criteria2(List<Criteria2> criteriaChain, Path path) {
        if (!path.isDefinite()) {
            throw new InvalidCriteriaException("A criteria path must be definite. The path " + path.toString() + " is not!");
        }
        this.path = path;
        this.criteriaChain = criteriaChain;
        this.criteriaChain.add(this);
    }

    private Criteria2(Path path) {
        this(new LinkedList<Criteria2>(), path);
    }

    private Criteria2(Path path, CriteriaType criteriaType, Object expected) {
        this(new LinkedList<Criteria2>(), path);
        this.criteriaType = criteriaType;
        this.expected = expected;
    }


    @Override
    public boolean apply(Object model, Configuration configuration) {
        for (Criteria2 criteria : criteriaChain) {
            if (!criteria.eval(model, configuration)) {
                return false;
            }
        }
        return true;
    }

    private boolean eval(Object model, Configuration configuration) {
        if (CriteriaType.EXISTS == criteriaType) {
            boolean exists = ((Boolean) expected);
            try {
                path.evaluate(model, configuration.options(Option.THROW_ON_MISSING_PROPERTY)).get();
                return exists == true;
            } catch (PathNotFoundException e) {
                return exists == false;
            }
        } else {

            try {
                final Object actual = path.evaluate(model, configuration).get();
                return criteriaType.eval(expected, actual, configuration);
            } catch (CompareException e) {
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
    public static Criteria2 where(Path key) {
        return new Criteria2(key);
    }

    /**
     * Static factory method to create a Criteria using the provided key
     *
     * @param key filed name
     * @return the new criteria
     */

    public static Criteria2 where(String key) {
        return where(PathCompiler.tokenize(key));
    }

    /**
     * Static factory method to create a Criteria using the provided key
     *
     * @param key ads new filed to criteria
     * @return the criteria builder
     */
    public Criteria2 and(String key) {
        return new Criteria2(this.criteriaChain, PathCompiler.tokenize(key));
    }

    /**
     * Creates a criterion using equality
     *
     * @param o
     * @return
     */
    public Criteria2 is(Object o) {
        this.criteriaType = CriteriaType.TYPE.EQ;
        this.expected = o;
        return this;
    }

    /**
     * Creates a criterion using equality
     *
     * @param o
     * @return
     */
    public Criteria2 eq(Object o) {
        return is(o);
    }

    /**
     * Creates a criterion using the <b>!=</b> operator
     *
     * @param o
     * @return
     */
    public Criteria2 ne(Object o) {
        this.criteriaType = CriteriaType.TYPE.NE;
        this.expected = o;
        return this;
    }

    /**
     * Creates a criterion using the <b>&lt;</b> operator
     *
     * @param o
     * @return
     */
    public Criteria2 lt(Object o) {
        this.criteriaType = CriteriaType.TYPE.LT;
        this.expected = o;
        return this;
    }

    /**
     * Creates a criterion using the <b>&lt;=</b> operator
     *
     * @param o
     * @return
     */
    public Criteria2 lte(Object o) {
        this.criteriaType = CriteriaType.TYPE.LTE;
        this.expected = o;
        return this;
    }

    /**
     * Creates a criterion using the <b>&gt;</b> operator
     *
     * @param o
     * @return
     */
    public Criteria2 gt(Object o) {
        this.criteriaType = CriteriaType.TYPE.GT;
        this.expected = o;
        return this;
    }

    /**
     * Creates a criterion using the <b>&gt;=</b> operator
     *
     * @param o
     * @return
     */
    public Criteria2 gte(Object o) {
        this.criteriaType = CriteriaType.TYPE.GTE;
        this.expected = o;
        return this;
    }

    /**
     * Creates a criterion using a Regex
     *
     * @param pattern
     * @return
     */
    public Criteria2 regex(Pattern pattern) {
        notNull(pattern, "pattern can not be null");
        this.criteriaType = CriteriaType.TYPE.REGEX;
        this.expected = pattern;
        return this;
    }

    /**
     * The <code>in</code> operator is analogous to the SQL IN modifier, allowing you
     * to specify an array of possible matches.
     *
     * @param o the values to match against
     * @return
     */
    public Criteria2 in(Object... o) {
        return in(Arrays.asList(o));
    }

    /**
     * The <code>in</code> operator is analogous to the SQL IN modifier, allowing you
     * to specify an array of possible matches.
     *
     * @param c the collection containing the values to match against
     * @return
     */
    public Criteria2 in(Collection<?> c) {
        notNull(c, "collection can not be null");
        this.criteriaType = CriteriaType.TYPE.IN;
        this.expected = c;
        return this;
    }

    /**
     * The <code>nin</code> operator is similar to $in except that it selects objects for
     * which the specified field does not have any value in the specified array.
     *
     * @param o the values to match against
     * @return
     */
    public Criteria2 nin(Object... o) {
        return nin(Arrays.asList(o));
    }

    /**
     * The <code>nin</code> operator is similar to $in except that it selects objects for
     * which the specified field does not have any value in the specified array.
     *
     * @param c the values to match against
     * @return
     */
    public Criteria2 nin(Collection<?> c) {
        notNull(c, "collection can not be null");
        this.criteriaType = CriteriaType.TYPE.NIN;
        this.expected = c;
        return this;
    }

    /**
     * The <code>all</code> operator is similar to $in, but instead of matching any value
     * in the specified array all values in the array must be matched.
     *
     * @param o
     * @return
     */
    public Criteria2 all(Object... o) {
        return all(Arrays.asList(o));
    }

    /**
     * The <code>all</code> operator is similar to $in, but instead of matching any value
     * in the specified array all values in the array must be matched.
     *
     * @param c
     * @return
     */
    public Criteria2 all(Collection<?> c) {
        notNull(c, "collection can not be null");
        this.criteriaType = CriteriaType.TYPE.ALL;
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
     * @return
     */
    public Criteria2 size(int size) {
        this.criteriaType = CriteriaType.TYPE.SIZE;
        this.expected = size;
        return this;
    }


    /**
     * Check for existence (or lack thereof) of a field.
     *
     * @param b
     * @return
     */
    public Criteria2 exists(boolean b) {
        this.criteriaType = CriteriaType.TYPE.EXISTS;
        this.expected = b;
        return this;
    }

    /**
     * The $type operator matches values based on their Java type.
     *
     * @param t
     * @return
     */
    public Criteria2 type(Class<?> t) {
        notNull(t, "type can not be null");
        this.criteriaType = CriteriaType.TYPE.TYPE;
        this.expected = t;
        return this;
    }

    /**
     * The <code>notEmpty</code> operator checks that an array or String is not empty.
     *
     * @return
     */
    public Criteria2 notEmpty() {
        this.criteriaType = CriteriaType.TYPE.NOT_EMPTY;
        this.expected = null;
        return this;
    }

    /**
     * The <code>matches</code> operator checks that an object matches the given predicate.
     *
     * @return
     */
    public Criteria2 matches(Predicate p) {
        this.criteriaType = CriteriaType.TYPE.MATCHES;
        this.expected = p;
        return this;
    }

    private static int safeCompare(Object expected, Object actual, Configuration configuration) {
        if (expected == null && actual != null) {
            return -1;
        } else if (expected != null && actual == null) {
            return 1;
        } else if (expected == null && actual == null) {
            return 0;
        } else if (expected instanceof String && actual instanceof String) {
            return ((String) expected).compareTo((String) actual);
        } else if (expected instanceof Number && actual instanceof Number) {
            return new BigDecimal(expected.toString()).compareTo(new BigDecimal(actual.toString()));
        } else if (expected instanceof String && actual instanceof Number) {
            return new BigDecimal(expected.toString()).compareTo(new BigDecimal(actual.toString()));
        } else if (expected instanceof String && actual instanceof Boolean) {
            Boolean e = Boolean.valueOf((String)expected);
            Boolean a = (Boolean) actual;
            return e.compareTo(a);
        } else if (expected instanceof Boolean && actual instanceof Boolean) {
            Boolean e = (Boolean) expected;
            Boolean a = (Boolean) actual;
            return e.compareTo(a);
        } else {
            logger.debug("Can not compare a {} with a {}", expected.getClass().getName(), actual.getClass().getName());
            throw new CompareException();
        }

    }

    private static class CompareException extends RuntimeException {
    }


    public static Criteria2 create(String path, String operator, String expected) {
        if (expected.startsWith("'") && expected.endsWith("'")) {
            expected = expected.substring(1, expected.length() - 1);
        }

        Path p = PathCompiler.tokenize(path);

        if (operator.isEmpty()) {
            return Criteria2.where(path).exists(true);
        } else {
            return new Criteria2(p, CriteriaType.parse(operator), expected);
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(path.toString())
                .append("|")
                .append(criteriaType.name())
                .append("|")
                .append(expected)
                .append("|");
        return sb.toString();
    }
}
