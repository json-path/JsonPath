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
public class ArrayIndexFilter extends Filter {
    public ArrayIndexFilter(String condition) {
        super(condition);
    }

    @Override
    public Object filter(Object obj,JsonProvider jsonProvider) {

        List<Object> src = jsonProvider.toList(obj);
        List<Object> result = jsonProvider.createList();

        String trimmedCondition = trim(condition, 1, 1);

        if(trimmedCondition.contains("@.length")){
            trimmedCondition = trim(trimmedCondition, 1, 1);
            trimmedCondition = trimmedCondition.replace("@.length", "");
            trimmedCondition = trimmedCondition + ":";
        }


        if (trimmedCondition.startsWith(":")) {
            trimmedCondition = trim(trimmedCondition, 1, 0);
            int get = Integer.parseInt(trimmedCondition);
            for (int i = 0; i < get; i++) {
                result.add(src.get(i));
            }
            return result;

        } else if (trimmedCondition.endsWith(":")) {
            trimmedCondition = trim(trimmedCondition, 1, 1);
            int get = Integer.parseInt(trimmedCondition);
            return src.get(src.size() - get);

        } else {
            String[] indexArr = trimmedCondition.split(",");

            if (indexArr.length == 1) {

                return src.get(Integer.parseInt(indexArr[0]));

            } else {
                for (String idx : indexArr) {
                    result.add(src.get(Integer.parseInt(idx.trim())));
                }
                return result;
            }
        }
    }

    @Override
    public boolean isArrayFilter() {
        return true;
    }
}
