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
package com.jayway.jsonpath.internal.filter.eval;

/**
 * @author Kalle Stenflo
 */
public class ExpressionEvaluator {

    public static <T> boolean eval(T actual, String comparator, String expected) {

        comparator = comparator.trim();

        if (actual instanceof Long) {

            Long a = (Long) actual;
            Long e = Long.parseLong(expected.trim());

            if ("=".equals(comparator) || "==".equals(comparator)) {
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

            if ("=".equals(comparator) || "==".equals(comparator)) {
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

            if ("=".equals(comparator) || "==".equals(comparator)) {
                return a.equals(expected);
            } else if ("!=".equals(comparator) || "<>".equals(comparator)) {
                return !a.equals(expected);
            }
        }

        return false;
    }
}
