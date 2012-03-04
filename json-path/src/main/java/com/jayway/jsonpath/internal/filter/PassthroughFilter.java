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
public class PassthroughFilter extends Filter {


    private boolean isArrayFilter;

    public PassthroughFilter(String condition, boolean isArrayFilter) {
        super(condition);
        this.isArrayFilter = isArrayFilter;
    }

    public Object filter(Object obj, JsonProvider jsonProvider) {
        return obj;
    }

    @Override
    public Object getRef(Object obj, JsonProvider jsonProvider) {
        return obj;
    }

    @Override
    public boolean isArrayFilter() {
        return isArrayFilter;
    }
}
