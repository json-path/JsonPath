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
package com.jayway.jsonpath.internal.spi.json;

import com.jayway.jsonpath.spi.json.JsonProvider;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public abstract class AbstractJsonProvider implements JsonProvider {

    /**
     * checks if object is an array
     *
     * @param obj object to check
     * @return true if obj is an array
     */
    public boolean isArray(Object obj) {
        return (obj instanceof List);
    }


    /**
     * Extracts a value from an array
     *
     * @param obj an array
     * @param idx index
     * @return the entry at the given index
     */
    public Object getArrayIndex(Object obj, int idx) {
        return ((List) obj).get(idx);
    }

    /**
     * Extracts a value from an map
     *
     * @param obj a map
     * @param key property key
     * @return the map entry or {@link com.jayway.jsonpath.spi.json.JsonProvider#UNDEFINED} for missing properties
     */
    public Object getMapValue(Object obj, String key){
        Map m = (Map) obj;
        if(!m.containsKey(key)){
            return JsonProvider.UNDEFINED;
        } else {
            return m.get(key);
        }
    }

    /**
     * Sets a value in an object or array
     *
     * @param obj   an array or an object
     * @param key   a String key or a numerical index
     * @param value the value to set
     */
    @SuppressWarnings("unchecked")
    public void setProperty(Object obj, Object key, Object value) {
        if (isMap(obj))
            ((Map) obj).put(key.toString(), value);
        else {
            List list = (List) obj;
            int index;
            if (key != null) {
                index = key instanceof Integer ? (Integer) key : Integer.parseInt(key.toString());
            } else {
                index = list.size();
            }
            list.add(index, value);
        }
    }


    /**
     * checks if object is a map (i.e. no array)
     *
     * @param obj object to check
     * @return true if the object is a map
     */
    public boolean isMap(Object obj) {
        return (obj instanceof Map);
    }

    /**
     * Returns the keys from the given object or the indexes from an array
     *
     * @param obj an array or an object
     * @return the keys for an object or the indexes for an array
     */
    @SuppressWarnings("unchecked")
    public Collection<String> getPropertyKeys(Object obj) {
        if (isArray(obj)) {
            List l = (List) obj;
            List<String> keys = new ArrayList<String>(l.size());
            for (int i = 0; i < l.size(); i++) {
                keys.add(String.valueOf(i));
            }
            return keys;
        } else {
            return ((Map) obj).keySet();
        }
    }

    /**
     * Get the length of an array or object
     *
     * @param obj an array or an object
     * @return the number of entries in the array or object
     */
    public int length(Object obj) {
        if (isArray(obj)) {
            return ((List) obj).size();
        }
        return getPropertyKeys(obj).size();
    }

    /**
     * Converts given object to an {@link Iterable}
     *
     * @param obj an array or an object
     * @return the entries for an array or the values for a map
     */
    @SuppressWarnings("unchecked")
    public Iterable<Object> toIterable(Object obj) {
        if (isArray(obj))
            return ((Iterable) obj);
        else
            return ((Map) obj).values();
    }

}
