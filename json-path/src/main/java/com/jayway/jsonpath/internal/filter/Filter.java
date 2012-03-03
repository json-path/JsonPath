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
public abstract class Filter {

    final String condition;

    Filter(String condition) {
        this.condition = condition;
    }


    String trim(String str, int front, int end) {
        String res = str;

        if (front > 0) {
            res = str.substring(front);
        }
        if (end > 0) {
            res = res.substring(0, res.length() - end);
        }
        return res;
    }

    public Object filter(Object obj, JsonProvider jsonProvider, boolean inArrayContext){
        return filter(obj, jsonProvider);
    }

    public abstract Object filter(Object obj, JsonProvider jsonProvider);



    public abstract boolean isArrayFilter();

}
