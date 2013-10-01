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

import com.jayway.jsonpath.spi.json.JsonProvider;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;

/**
 * A filter is used to filter the content of a JSON array in a JSONPath.
 * <p/>
 * Sample
 * <p/>
 * <code>
 * String doc = {"items": [{"name" : "john"}, {"name": "bob"}]}
 * <p/>
 * List<String> names = JsonPath.read(doc, "$items[?].name", Filter.filter(Criteria.where("name").is("john"));
 * </code>
 *
 * @author Kalle Stenflo
 * @see Criteria
 */
public abstract class Filter<T> {

    /**
     * Creates a new filter based on given criteria
     *
     * @param criteria the filter criteria
     * @return a new filter
     */
    public static Filter filter(Criteria criteria) {
        return new MapFilter(criteria);
    }

    /**
     * Filters the provided list based on this filter configuration
     *
     * @param filterItems   items to filter
     * @param configuration the json provider configuration that is used to create the result list
     * @return the filtered list
     */
    public Object doFilter(Iterable<T> filterItems, Configuration configuration) {
        JsonProvider provider = configuration.getProvider();
        Object result = provider.createArray();
        for (T filterItem : filterItems) {
            if (accept(filterItem, configuration)) {
                provider.setProperty(result, provider.length(result), filterItem);
            }
        }
        return result;
    }

    /**
     * Check if this filter will accept or reject the given object
     *
     * @param obj item to check
     * @return true if filter matches
     */
    public abstract boolean accept(T obj);

    /**
     * Check if this filter will accept or reject the given object
     *
     * @param obj           item to check
     * @param configuration
     * @return true if filter matches
     */
    public abstract boolean accept(T obj, Configuration configuration);

    /**
     * Adds a new criteria to this filter
     *
     * @param criteria to add
     * @return the updated filter
     */
    public abstract Filter addCriteria(Criteria criteria);


    // --------------------------------------------------------
    //
    // Default filter implementation
    //
    // --------------------------------------------------------
    public static abstract class FilterAdapter<T> extends Filter<T> {

        @Override
        public boolean accept(T obj) {
            return false;
        }

        @Override
        public boolean accept(T obj, Configuration configuration) {
            return accept(obj);
        }


        @Override
        public Filter addCriteria(Criteria criteria) {
            throw new UnsupportedOperationException("can not add criteria to a FilterAdapter.");
        }

    }

    private static class MapFilter extends FilterAdapter<Object> {

        private HashMap<String, Criteria> criteria = new LinkedHashMap<String, Criteria>();

        public MapFilter(Criteria criteria) {
            addCriteria(criteria);
        }

        public MapFilter addCriteria(Criteria criteria) {
            String key = criteria.getKey().toString();
            Criteria existing = this.criteria.get(key);
            if (existing == null) {
                this.criteria.put(key, criteria);
            } else {
                existing.andOperator(criteria);
            }
            return this;
        }

        @Override
        public boolean accept(Object map) {
            return accept(map, Configuration.defaultConfiguration());
        }

        @Override
        public boolean accept(Object map, Configuration configuration) {
            for (Criteria criterion : this.criteria.values()) {
                if (!criterion.matches(map, configuration)) {
                    return false;
                }
            }
            return true;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("[?(");

            Iterator<Criteria> criteriaIterator = criteria.values().iterator();
            Criteria criterion = criteriaIterator.next();
            sb.append(criterion.toString());

            while (criteriaIterator.hasNext()) {
                sb.append(" && ")
                        .append(criteriaIterator.next().toString());
            }
            sb.append(")]");

            return sb.toString();
        }
    }
}
