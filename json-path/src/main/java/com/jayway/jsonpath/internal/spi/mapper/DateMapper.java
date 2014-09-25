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
import com.jayway.jsonpath.spi.mapper.MappingException;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;

public class DateMapper extends MapperBase {

    public DateMapper() {
        register(Long.class, Date.class);
        register(String.class, Date.class);
    }

    @Override
    public Object convert(Object src, Class<?> srcType, Class<?> targetType, Configuration conf) {

        assertValidConversion(src, srcType, targetType);

        if(src == null){
            return null;
        }
        if(Long.class.isAssignableFrom(srcType)){
            return new Date((Long) src);
        }
        else if(String.class.isAssignableFrom(srcType)){
            try {
                return DateFormat.getInstance().parse(src.toString());
            } catch (ParseException e) {
                throw new MappingException(e);
            }
        }

        throw new MappingException("Can not map: " + srcType.getName() + " to: " + targetType.getName());
    }
}
