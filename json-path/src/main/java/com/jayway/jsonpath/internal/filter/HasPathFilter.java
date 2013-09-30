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

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;
import com.jayway.jsonpath.PathNotFoundException;
import com.jayway.jsonpath.spi.JsonProvider;

/**
 * @author Kalle Stenflo
 */
public class HasPathFilter extends PathTokenFilter {

    private final JsonPath path;

    public HasPathFilter(String condition) {
        super(condition);
        String trimmedCondition = condition;

        if(condition.contains("['")){
            trimmedCondition = trimmedCondition.replace("['", ".");
            trimmedCondition = trimmedCondition.replace("']", "");
        }

        this.path = JsonPath.compile(trim(trimmedCondition, 5, 2));
    }

    @Override
    public Object filter(Object obj, Configuration configuration) {
        JsonProvider jsonProvider = configuration.getProvider();

        //[?(@.isbn)]
        Iterable<Object> src = jsonProvider.toIterable(obj);
        Object result = jsonProvider.createArray();

        for (Object item : src) {
            if(jsonProvider.isMap(item)){
                try{
                    path.read(item, Configuration.builder().options(Option.THROW_ON_MISSING_PROPERTY).jsonProvider(jsonProvider).build());
                    jsonProvider.setProperty(result, jsonProvider.length(result), item);
                } catch (PathNotFoundException e){
                    // the path was not found in the item
                }
            }
        }
        return result;
    }

    @Override
    public Object getRef(Object obj, Configuration configuration) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isArrayFilter() {
        return true;
    }
}
