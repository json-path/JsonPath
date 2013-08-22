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

/**
 * @author Kalle Stenflo
 */
public class WildcardFilter extends PathTokenFilter {

    public WildcardFilter(String condition) {
        super(condition);
    }

    @Override
    public Object filter(Object obj, JsonProvider jsonProvider) {
        Object result = jsonProvider.createArray();

        if (jsonProvider.isArray(obj)) {
            for (Object current : jsonProvider.toIterable(obj)) {
                for (Object value : jsonProvider.toIterable(current)) {
                    jsonProvider.setProperty(result, jsonProvider.length(result), value);
                }
            }
        } else {
            for (Object value : jsonProvider.toIterable(obj)) {
                jsonProvider.setProperty(result, jsonProvider.length(result), value);
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
