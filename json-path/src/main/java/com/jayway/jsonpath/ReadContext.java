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

import java.util.List;

/**
 * User: kalle
 * Date: 8/30/13
 * Time: 12:03 PM
 */
public interface ReadContext {

    /**
     * Returns the JSON model that this context is reading
     *
     * @return json model
     */
    Object json();

    /**
     * Reads the given path from this context
     *
     * @param path    path to read
     * @param filters filters
     * @param <T>
     * @return result
     */
    <T> T read(String path, Filter2... filters);

    /**
     * Reads the given path from this context
     *
     * @param path path to apply
     * @param <T>
     * @return result
     */
    <T> T read(JsonPath path);

    /**
     * Reads the given path list from this context
     *
     * @param path    a container Object
     * @param filters filters
     * @return list of definite path strings to object matched by path
     */
    List<String> readPathList(String path, Filter2... filters);

    /**
     * Reads the given path list from this context
     *
     * @param path path to apply
     * @return list of definite path strings to object matched by path
     */
    List<String> readPathList(JsonPath path);

}
