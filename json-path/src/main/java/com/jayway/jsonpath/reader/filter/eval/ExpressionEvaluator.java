package com.jayway.jsonpath.reader.filter.eval;

/**
 * User: kalle stenflo
 * Date: 2/4/11
 * Time: 9:21 PM
 */
public class ExpressionEvaluator {


    public static <T> boolean eval(T actual, String comparator, String expected) {

        comparator = comparator.trim();

        if (actual instanceof Long) {

            Long a = (Long) actual;
            Long e = Long.parseLong(expected.trim());

            if ("=".equals(comparator)) {
                return a.longValue() == e.longValue();
            } else if ("!=".equals(comparator) || "<>".equals(comparator)) {
                return a.longValue() != e.longValue();
            } else if (">".equals(comparator)) {
                return a.longValue() > e.longValue();
            } else if (">=".equals(comparator)) {
                return a.longValue() >= e.longValue();
            } else if ("<".equals(comparator)) {
                return a.longValue() < e.longValue();
            } else if ("<=".equals(comparator)) {
                return a.longValue() <= e.longValue();
            }
        } else if (actual instanceof Integer) {
            Integer a = (Integer) actual;
            Integer e = Integer.parseInt(expected.trim());

            if ("=".equals(comparator)) {
                return a.intValue() == e.intValue();
            } else if ("!=".equals(comparator) || "<>".equals(comparator)) {
                return a.intValue() != e.intValue();
            } else if (">".equals(comparator)) {
                return a.intValue() > e.intValue();
            } else if (">=".equals(comparator)) {
                return a.intValue() >= e.intValue();
            } else if ("<".equals(comparator)) {
                return a.intValue() < e.intValue();
            } else if ("<=".equals(comparator)) {
                return a.intValue() <= e.intValue();
            }
        } else if (actual instanceof Double) {

            Double a = (Double) actual;
            Double e = Double.parseDouble(expected.trim());

            if ("=".equals(comparator)) {
                return a.doubleValue() == e.doubleValue();
            } else if ("!=".equals(comparator) || "<>".equals(comparator)) {
                return a.doubleValue() != e.doubleValue();
            } else if (">".equals(comparator)) {
                return a.doubleValue() > e.doubleValue();
            } else if (">=".equals(comparator)) {
                return a.doubleValue() >= e.doubleValue();
            } else if ("<".equals(comparator)) {
                return a.doubleValue() < e.doubleValue();
            } else if ("<=".equals(comparator)) {
                return a.doubleValue() <= e.doubleValue();
            }
        } else if (actual instanceof String) {

            String a = (String)actual;
            expected = expected.trim();
            if(expected.startsWith("'")) {
                expected = expected.substring(1);
            }
            if(expected.endsWith("'")){
                expected = expected.substring(0, expected.length()-1);
            }

            if ("=".equals(comparator)) {
                return a.equals(expected);
            } else if ("!=".equals(comparator) || "<>".equals(comparator)) {
                return !a.equals(expected);
            }
        }

        return false;
    }
}
