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
package com.jayway.jsonpath.spi.impl;

import com.jayway.jsonpath.spi.JsonProvider;
import org.apache.commons.lang3.SerializationUtils;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @author Kalle Stenflo
 */
public abstract class AbstractJsonProvider implements JsonProvider {

    @Override
    public Object clone(Object obj){
        return SerializationUtils.clone((Serializable)obj);
    }

    /**
     * checks if object is <code>instanceof</code> <code>java.util.List</code> or <code>java.util.Map</code>
     *
     * @param obj object to check
     * @return true if List or Map
     */
    public boolean isContainer(Object obj) {
        return (isList(obj) || isMap(obj));
    }

    /**
     * checks if object is <code>instanceof</code> <code>java.util.List</code>
     *
     * @param obj object to check
     * @return true if List
     */
    public boolean isList(Object obj) {
        return (obj instanceof List);
    }

    /**
     * Converts give object to a List
     *
     * @param list object to convert
     * @return object as list
     */
    @SuppressWarnings({"unchecked"})
    public List<Object> toList(Object list) {
        return (List<Object>) list;
    }


    /**
     * Converts given object to a Map
     *
     * @param map object to convert
     * @return object as map
     */
    @SuppressWarnings({"unchecked"})
    public Map<String, Object> toMap(Object map) {
        return (Map<String, Object>) map;
    }

    /**
     * Extracts a value from a Map
     *
     * @param map map to read from
     * @param key key to read
     * @return value of key in map
     */
    public Object getMapValue(Object map, String key) {
        return toMap(map).get(key);
    }

    /**
     * checks if object is <code>instanceof</code> <code>java.util.Map</code>
     *
     * @param obj object to check
     * @return true if Map
     */
    public boolean isMap(Object obj) {
        return (obj instanceof Map);
    }

}
