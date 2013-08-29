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

import com.jayway.jsonpath.Filter;
import com.jayway.jsonpath.PathNotFoundException;
import com.jayway.jsonpath.internal.PathToken;
import com.jayway.jsonpath.spi.JsonProvider;

import java.util.Collection;
import java.util.LinkedList;

/**
 * @author Kalle Stenflo
 */
public class FieldFilter extends PathTokenFilter {

    private final String[] split;
    private final PathToken pathToken;

    public FieldFilter(PathToken pathToken) {
        super(pathToken.getFragment());
        this.pathToken = pathToken;
        this.split = condition.split("','");
    }

    @Override
    public Object filter(Object obj, JsonProvider jsonProvider, LinkedList<Filter> filters, boolean inArrayContext) {
        if (jsonProvider.isArray(obj)) {
            if (!inArrayContext) {
                throw new PathNotFoundException("Path '" + condition + "' is being applied to an array. Arrays can not have attributes.");
            } else {
                Object result = jsonProvider.createArray();
                for (Object current : jsonProvider.toIterable(obj)) {
                    if (jsonProvider.isMap(current)) {
                        
                        Collection<String> keys = jsonProvider.getPropertyKeys(current);

                        if(split.length == 1){
                            if (keys.contains(condition)) {
                                Object o = jsonProvider.getProperty(current, condition);
                                if (jsonProvider.isArray(o)) {
                                    for(Object item : jsonProvider.toIterable(o)){
                                      jsonProvider.setProperty(result, jsonProvider.length(result), item);
                                    }
                                } else {
                                    jsonProvider.setProperty(result, jsonProvider.length(result), jsonProvider.getProperty(current, condition));
                                }
                            }
                        } else {
                            Object res = jsonProvider.createMap();
                            for (String prop : split) {
                                if (keys.contains(prop)) {
                                    jsonProvider.setProperty(res, prop, jsonProvider.getProperty(current, prop));
                                }
                            }
                            jsonProvider.setProperty(result, jsonProvider.length(result), res);
                        }
                    }
                }
                return result;
            }
        } else if (jsonProvider.isMap(obj)){

            Collection<String> keys = jsonProvider.getPropertyKeys(obj);
            if(!keys.contains(condition) && split.length == 1){
                //TODO: finalize behaviour
                //throw new PathNotFoundException("Path '" + condition + "' not found in the current context:\n" + jsonProvider.toJson(obj));
                if(pathToken.isEndToken()){
                    return null;
                } else {
                    throw new PathNotFoundException("Path '" + condition + "' not found in the current context:\n" + jsonProvider.toJson(obj));
                }
            } else {

                if(split.length == 1){
                    return jsonProvider.getProperty(obj, condition);
                } else {
                    Object res = jsonProvider.createMap();
                    for (String prop : split) {
                        if(keys.contains(prop)){
                          jsonProvider.setProperty(res, prop, jsonProvider.getProperty(obj, prop));
                        }
                    }
                    return res;
                }
            }
        } else {
            throw new PathNotFoundException("Failed to access property: '" + condition + "' on object " + obj);
        }
    }


    public Object filter(Object obj, JsonProvider jsonProvider) {
        if (jsonProvider.isArray(obj)) {
            return obj;
        } else {
            return jsonProvider.getProperty(obj, condition);
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
