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
     * Returns a list of leave path strings representing the path of the evaluation hits
     *
     * Using [*], it will return the root to leave path
     * Otherwise, return the path like AS_PATH_LIST
     *
     * For issue 753
     * Only return the path from root to leaves
     * Eg.
     * {
     *   "name": "Martin",
     *   "family": "Newman",
     *   "phones": ["+48 612 34 56 78", "+49 12 34 56 78 90"],
     *   "age": 18,
     *   "married": true,
     *   "salary": {"amount": 12.10, currency:"EURO"},
     *   "kids": [{"name": "Lena"}, {"name": "Luna"}, {"name": "Lego"}]
     * }
     *
     * Return:
     *
     *  "$['name']",
     *  "$['family']",
     *  "$['phones'][0]",
     *  "$['phones'][1]",
     *  "$['age']",
     *  "$['married']",
     *  "$['salary']['amount']",
     *  "$['salary']['currency']",
     *  "$['kids'][0]['name']",
     *  "$['kids'][1]['name']",
     *  "$['kids'][2]['name']"
     *
     * Created by XiaoLing12138 on 05/21/2022.
     */
    AS_LEAVE_PATH_LIST,

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
    REQUIRE_PROPERTIES

}
