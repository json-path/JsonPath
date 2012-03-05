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

import java.util.List;
import java.util.Map;

/**
 * @author Kalle Stenflo
 */
public class HasFieldFilter extends PathTokenFilter {

    public HasFieldFilter(String condition) {
        super(condition);
    }

    @Override
    public Object filter(Object obj, JsonProvider jsonProvider) {

        //[?(@.isbn)]
        List<Object> src = jsonProvider.toList(obj);
        List<Object> result = jsonProvider.createList();

        String trimmedCondition = condition;

        if(condition.contains("['")){
            trimmedCondition = trimmedCondition.replace("['", ".");
            trimmedCondition = trimmedCondition.replace("']", "");
        }

        trimmedCondition = trim(trimmedCondition, 5, 2);

        for (Object item : src) {
            if(jsonProvider.isMap(item)){
                Map<String, Object> map = jsonProvider.toMap(item);
                if(map.containsKey(trimmedCondition)){
                    result.add(map);
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
