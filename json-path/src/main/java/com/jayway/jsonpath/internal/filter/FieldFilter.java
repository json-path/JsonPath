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
public class FieldFilter extends Filter {

    public FieldFilter(String condition) {
        super(condition);
    }

    @Override
    public Object filter(Object obj, JsonProvider jsonProvider, boolean inArrayContext) {
        if (jsonProvider.isList(obj)) {
            if (!inArrayContext) {
                return null;
            } else {
                List<Object> result = jsonProvider.createList();
                for (Object current : jsonProvider.toList(obj)) {
                    if (jsonProvider.isMap(current)) {
                        Map<String, Object> map = jsonProvider.toMap(current);
                        if (map.containsKey(condition)) {
                            Object o = map.get(condition);
                            if (jsonProvider.isList(o)) {
                                result.addAll(jsonProvider.toList(o));
                            } else {
                                result.add(map.get(condition));
                            }
                        }
                    }
                }
                return result;
            }
        } else {
            return jsonProvider.getMapValue(obj, condition);
        }
    }


    public Object filter(Object obj, JsonProvider jsonProvider) {
        if (jsonProvider.isList(obj)) {
            return obj;
        } else {
            return jsonProvider.getMapValue(obj, condition);
        }
    }

    @Override
    public Object getRef(Object obj, JsonProvider jsonProvider) {
        return filter(obj, jsonProvider);
    }

    @Override
    public boolean isArrayFilter() {
        return false;
    }


}
