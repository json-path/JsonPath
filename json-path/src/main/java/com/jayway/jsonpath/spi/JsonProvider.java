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
package com.jayway.jsonpath.spi;

import com.jayway.jsonpath.InvalidJsonException;

import java.io.InputStream;
import java.io.Reader;
import java.util.List;
import java.util.Map;

/**
 * @author Kalle Stenflo
 */
public interface JsonProvider {

    Mode getMode();

    Object parse(String json) throws InvalidJsonException;

    Object parse(Reader jsonReader) throws InvalidJsonException;

    Object parse(InputStream jsonStream) throws InvalidJsonException;


    String toJson(Object obj);

    Map<String, Object> createMap();

    List<Object> createList();


    /**
     * checks if object is <code>instanceof</code> <code>java.util.List</code> or <code>java.util.Map</code>
     *
     * @param obj object to check
     * @return true if List or Map
     */
    boolean isContainer(Object obj);

    /**
     * checks if object is <code>instanceof</code> <code>java.util.List</code>
     *
     * @param obj object to check
     * @return true if List
     */
     boolean isList(Object obj);

    /**
     * Converts give object to a List
     *
     * @param list
     * @return
     */
     List<Object> toList(Object list);


    /**
     * Converts given object to a Map
     *
     * @param map
     * @return
     */
     Map<String, Object> toMap(Object map);

    /**
     * Extracts a value from a Map
     *
     * @param map
     * @param key
     * @return
     */
     Object getMapValue(Object map, String key);

    /**
     * checks if object is <code>instanceof</code> <code>java.util.Map</code>
     *
     * @param obj object to check
     * @return true if Map
     */
     boolean isMap(Object obj);


}
