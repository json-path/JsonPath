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
package com.jayway.jsonpath.spi.json;

import com.jayway.jsonpath.InvalidJsonException;

import java.io.InputStream;
import java.io.Reader;
import java.util.Collection;

public interface JsonProvider {

    Mode getMode();

    Object parse(String json) throws InvalidJsonException;

    Object parse(Reader jsonReader) throws InvalidJsonException;

    Object parse(InputStream jsonStream) throws InvalidJsonException;


    String toJson(Object obj);

    Object createMap();

    Iterable createArray();


    Object clone(Object model);

    /**
     * checks if object is a map or an array
     *
     * @param obj object to check
     * @return true if obj is a map or an array
     */
    boolean isContainer(Object obj);

    /**
     * checks if object is an array
     *
     * @param obj object to check
     * @return true if obj is an array
     */
    boolean isArray(Object obj);

    /**
     * Get the length of an array or object
     *
     * @param obj an array or an object
     * @return the number of entries in the array or object
     */
    int length(Object obj);

    /**
     * Converts given object to an {@link Iterable}
     *
     * @param obj an array or an object
     * @return the entries for an array or the values for a map
     */
    Iterable<Object> toIterable(Object obj);


    /**
     * Returns the keys from the given object or the indexes from an array
     *
     * @param obj an array or an object
     * @return the keys for an object or the indexes for an array
     */
    Collection<String> getPropertyKeys(Object obj);

    /**
     * Extracts a value from an object or array
     *
     * @param obj an array or an object
     * @param key a String key or a numerical index
     * @return the entry at the given key, i.e. obj[key]
     */
    Object getProperty(Object obj, Object key);

    /**
     * Sets a value in an object or array
     *
     * @param obj   an array or an object
     * @param key   a String key or a numerical index
     * @param value the value to set
     */
    void setProperty(Object obj, Object key, Object value);

    /**
     * checks if object is a map (i.e. no array)
     *
     * @param obj object to check
     * @return true if the object is a map
     */
    boolean isMap(Object obj);


}
