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

/**
 * @author Kalle Stenflo
 */
public class ScanFilter extends PathTokenFilter {

    public ScanFilter(String condition) {
        super(condition);
    }

    @Override
    public Object filter(Object obj, JsonProvider jsonProvider) {
        List<Object> result = jsonProvider.createList();
        scan(obj, result, jsonProvider);

        return result;
    }

    @Override
    public boolean isArrayFilter() {
        return true;
    }

    @Override
    public Object getRef(Object obj, JsonProvider jsonProvider) {
        throw new UnsupportedOperationException();
    }


    private void scan(Object container, List<Object> result, JsonProvider jsonProvider) {

        if (jsonProvider.isMap(container)) {
            result.add(container);

            for (Object value : jsonProvider.toMap(container).values()) {
                if (jsonProvider.isContainer(value)) {
                    scan(value, result, jsonProvider);
                }
            }
        } else if (jsonProvider.isList(container)) {

            for (Object value : jsonProvider.toList(container)) {
                if (jsonProvider.isContainer(value)) {
                    scan(value, result, jsonProvider);
                }
            }
        }
    }
}
