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
package com.jayway.jsonpath.internal;

import com.jayway.jsonpath.InvalidConversionException;

/**
 * @author Kalle Stenflo
 */
public class ConvertUtils {

    /**
     * converts to Integer with radix 10
     *
     * @param o object to convert
     * @return converted value
     */
    public static Integer toInt(Object o) {
        if (null == o)
            return null;
        if (o instanceof Number)
            return ((Number) o).intValue();
        try {
            return Integer.valueOf(o.toString().trim(), 10);
        } catch (Exception e) {
            throw new InvalidConversionException("Could not convert " + o.toString() + " to Integer");
        }
    }


    /**
     * converts to Long with radix 10
     *
     * @param o object to convert
     * @return converted value
     */
    public static Long toLong(Object o) {
        if (null == o)
            return null;
        if (o instanceof Number)
            return ((Number) o).longValue();
        try {
            return Long.valueOf(o.toString().trim(), 10);
        } catch (Exception e) {
            throw new InvalidConversionException("Could not convert " + o.toString() + " to Long");
        }
    }

    /**
     * converts to Double with radix 10
     *
     * @param o object to convert
     * @return converted value
     */
    public static Double toDouble(Object o) {
        if (null == o)
            return null;
        if (o instanceof Number)
            return ((Number) o).doubleValue();
        try {
            return Double.valueOf(o.toString().trim());
        } catch (Exception e) {
            throw new InvalidConversionException("Could not convert " + o.toString() + " to Double");
        }
    }
}
