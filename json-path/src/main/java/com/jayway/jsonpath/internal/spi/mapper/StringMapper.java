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
package com.jayway.jsonpath.internal.spi.mapper;

import com.jayway.jsonpath.Configuration;

public class StringMapper extends MapperBase {

    public StringMapper() {
        register(Object.class, String.class);
    }

    @Override
    public Object convert(Object src, Class<?> srcType, Class<?> targetType, Configuration conf) {
        assertValidConversion(src, srcType, targetType);

        if (src == null) {
            return null;
        }
        return src.toString();
    }

    @Override
    boolean canConvert(Class<?> srcType, Class<?> targetType){
        return true;
    }
}
