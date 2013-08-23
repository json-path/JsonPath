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

import com.jayway.jsonpath.PathNotFoundException;
import com.jayway.jsonpath.spi.JsonProvider;

import java.util.regex.Pattern;

/**
 * @author Kalle Stenflo
 */
public class ArrayIndexFilter extends PathTokenFilter {

    private static final Pattern SINGLE_ARRAY_INDEX_PATTERN = Pattern.compile("\\[\\d+\\]");
    private static final Pattern COMMA = Pattern.compile(",");
    private static final Pattern SPACE = Pattern.compile(" ");
    private static final String OPERATOR = ":";

    private final String trimmedCondition;
    private boolean usesLenght;

    public ArrayIndexFilter(String condition) {
        super(condition);

        // remove '[' and ']'
        String trimmedCondition = trim(condition, 1, 1);

        this.usesLenght = trimmedCondition.contains("@.length");

        // resolve '@.length'
        if(usesLenght){
            trimmedCondition = trim(trimmedCondition, 1, 1);
            trimmedCondition = trimmedCondition.replace("@.length", "");
            trimmedCondition = trimmedCondition + OPERATOR;
        }
        this.trimmedCondition = trimmedCondition;
    }


    @Override
    public Object filter(Object obj, JsonProvider jsonProvider) {

        Object result = jsonProvider.createArray();

        if (trimmedCondition.contains(OPERATOR)) {
            if (trimmedCondition.startsWith(OPERATOR)) {
                String trimmedCondition = trim(this.trimmedCondition, 1, 0);
                int get = Integer.parseInt(trimmedCondition);
                for (int i = 0; i < get; i++) {
                    jsonProvider.setProperty(result, jsonProvider.length(result), jsonProvider.getProperty(obj, i));
                }
                return result;

            } else if (trimmedCondition.endsWith(OPERATOR)) {
                String trimmedCondition = trim(SPACE.matcher(this.trimmedCondition).replaceAll(""), 0, 1);
                int get = Integer.parseInt(trimmedCondition);
                if(get > 0 || usesLenght){
                    if(get > 0){
                        get = get * -1;
                    }
                    return jsonProvider.getProperty(obj, jsonProvider.length(obj) + get);
                } else {
                    int start = jsonProvider.length(obj) + get;
                    int stop = jsonProvider.length(obj);

                    for (int i = start; i < stop; i ++){
                        jsonProvider.setProperty(result, jsonProvider.length(result), jsonProvider.getProperty(obj, i));
                    }
                    return result;
                }

            } else {
                String[] indexes = this.trimmedCondition.split(OPERATOR);

                int start = Integer.parseInt(indexes[0]);
                int stop = Integer.parseInt(indexes[1]);

                for (int i = start; i < stop; i ++){
                    jsonProvider.setProperty(result, jsonProvider.length(result), jsonProvider.getProperty(obj, i));
                }
                return result;
            }
        } else {
            String[] indexArr = COMMA.split(trimmedCondition);

            if(obj == null || jsonProvider.length(obj) == 0){
                throw new PathNotFoundException("Failed to access array index: '" + condition + "' since the array is null or empty");
            }

            if (indexArr.length == 1) {
                return jsonProvider.getProperty(obj, indexArr[0]);

            } else {
                for (String idx : indexArr) {
                    jsonProvider.setProperty(result, jsonProvider.length(result), jsonProvider.getProperty(obj, idx.trim()));
                }
                return result;
            }
        }
    }

    @Override
    public Object getRef(Object obj, JsonProvider jsonProvider) {
        if(SINGLE_ARRAY_INDEX_PATTERN.matcher(condition).matches()){
            String trimmedCondition = trim(condition, 1, 1);
            return jsonProvider.getProperty(obj, trimmedCondition);

        } else {
            throw new UnsupportedOperationException();
        }
    }

    @Override
    public boolean isArrayFilter() {
        return true;
    }
}
