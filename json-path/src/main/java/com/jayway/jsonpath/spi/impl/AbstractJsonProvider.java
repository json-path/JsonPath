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
import java.util.ArrayList;
import java.util.Collection;
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
     * checks if object is a map or an array
     *
     * @param obj object to check
     * @return true if obj is a map or an array
     */
    public boolean isContainer(Object obj) {
        return (isArray(obj) || isMap(obj));
    }

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
     * Extracts a value from an object or array
     *
     * @param obj an array or an object
     * @param key a String key or a numerical index
     * @return the entry at the given key, i.e. obj[key]
     */
    public Object getProperty(Object obj, Object key) {
      if (isMap(obj))
        return ((Map) obj).get(key.toString());
      else{
        int index = key instanceof Integer? (Integer) key : Integer.parseInt(key.toString());
        return ((List) obj).get(index);
      }
    }
        
    /**
     * Sets a value in an object or array
     *
     * @param obj an array or an object
     * @param key a String key or a numerical index
     * @param value the value to set
     */
    public void setProperty(Object obj, Object key, Object value) {
        if (isMap(obj))
          ((Map) obj).put(key.toString(), value);
        else{
          int index = key instanceof Integer? (Integer) key : Integer.parseInt(key.toString());
          ((List) obj).add(index, value);
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
    public Collection<String> getPropertyKeys(Object obj) {
        if (isArray(obj)){
            List l = (List) obj;
            List<String> keys = new ArrayList<String>(l.size());
            for (int i = 0; i < l.size(); i++){
              keys.add(String.valueOf(i));
            }
            return keys;
        }
        else{
            return ((Map)obj).keySet();
        }
    }
    
    /**
     * Get the length of an array or object
     * @param obj an array or an object
     * @return the number of entries in the array or object
     */
    public int length(Object obj) {
        if (isArray(obj)){
          return ((List)obj).size();
        }
        return getPropertyKeys(obj).size();
    }
    
    /**
     * Converts given object to an {@link Iterable}
     *
     * @param obj an array or an object
     * @return the entries for an array or the values for a map
     */
    public Iterable<Object> toIterable(Object obj) {
       if (isArray(obj))
         return ((Iterable) obj);
       else
         return ((Map)obj).values();
    }

}
