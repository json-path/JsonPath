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

public enum Option {

    /**
     * returns <code>null</code> for missing leaf.
     *
     * <pre>
     * [
     *      {
     *         "foo" : "foo1",
     *         "bar" : "bar1"
     *      }
     *      {
     *         "foo" : "foo2"
     *      }
     * ]
     *</pre>
     *
     * the path :
     *
     * "$[*].bar"
     *
     * Without flag ["bar1"] is returned
     * With flag ["bar1", null] is returned
     *
     *
     */
    DEFAULT_PATH_LEAF_TO_NULL,

    /**
     * Makes this implementation more compliant to the Goessner spec. All results are returned as Lists.
     */
    ALWAYS_RETURN_LIST,

    /**
     * Returns a list of path strings representing the path of the evaluation hits
     */
    AS_PATH_LIST,

    /**
     * Suppress all exceptions when evaluating path.
     * <br/>
     * If an exception is thrown and the option {@link Option#ALWAYS_RETURN_LIST} an empty list is returned.
     * If an exception is thrown and the option {@link Option#ALWAYS_RETURN_LIST} is not present null is returned.
     */
    SUPPRESS_EXCEPTIONS,

    /**
     * Configures JsonPath to require properties defined in path when an <bold>indefinite</bold> path is evaluated.
     *
     *
     * Given:
     *
     * <pre>
     * [
     *     {
     *         "a" : "a-val",
     *         "b" : "b-val"
     *     },
     *     {
     *         "a" : "a-val",
     *     }
     * ]
     * </pre>
     *
     * evaluating the path "$[*].b"
     *
     * If REQUIRE_PROPERTIES option is present PathNotFoundException is thrown.
     * If REQUIRE_PROPERTIES option is not present ["b-val"] is returned.
     */
    REQUIRE_PROPERTIES,

    /**
     * This mode can be used query at special filters.
     * In the origin design, the filter receive an array and return an array.
     * However, the subsequent operation will apply on each element in the returned array, rather than the whole array itself.
     * The mode treats the result of filter as array, and it might be useful to some situation.
     * For example:
     *
     * Given:
     * <pre>
     * [
     *      {
     *          "price" : 20,
     *          "name" : "book1"
     *      },
     *      {
     *          "price" : 30,
     *          "name" : "book1"
     *      },
     *      {
     *          "price" : 40,
     *          "name" : "book1"
     *      },
     *      {
     *          "price" : 50,
     *          "name" : "book1"
     *      },
     * ]
     * </pre>
     *
     * We want to get the first book which price is greater than 25.
     *
     * evaluating the path "$[?(@.price > 25)][0]"
     *
     * when using classic mode, the result will be null.
     * when using this mode, the result will be [{"price":30,"name":"book1"}]. That is what we expected.
     *
     *
     * Notice: When using this mode, the path of the result will be incorrect. Besides, SET operation will don't work.
     */
    FILTER_AS_ARRAY

}
