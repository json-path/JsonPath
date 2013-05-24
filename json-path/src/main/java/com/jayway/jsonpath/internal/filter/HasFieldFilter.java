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
package com.jayway.jsonpath.internal.filter;

import com.jayway.jsonpath.spi.JsonProvider;

import java.util.Collection;

/**
 * @author Kalle Stenflo
 */
public class HasFieldFilter extends PathTokenFilter {

    private final String trimmedCondition;

    public HasFieldFilter(String condition) {
        super(condition);
        String trimmedCondition = condition;

        if(condition.contains("['")){
            trimmedCondition = trimmedCondition.replace("['", ".");
            trimmedCondition = trimmedCondition.replace("']", "");
        }

        this.trimmedCondition = trim(trimmedCondition, 5, 2);
    }

    @Override
    public Object filter(Object obj, JsonProvider jsonProvider) {

        //[?(@.isbn)]
        Iterable<Object> src = jsonProvider.toIterable(obj);
        Object result = jsonProvider.createArray();

        for (Object item : src) {
            if(jsonProvider.isMap(item)){
                Collection<String> keys = jsonProvider.getPropertyKeys(item);
                if(keys.contains(trimmedCondition)){
                    jsonProvider.setProperty(result, jsonProvider.length(result), item);
                }
            }
        }
        return result;
    }

    @Override
    public Object getRef(Object obj, JsonProvider jsonProvider) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isArrayFilter() {
        return true;
    }
}
