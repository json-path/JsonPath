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
package com.jayway.jsonpath.spi.mapper;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.TypeRef;

public class JacksonMappingProvider implements MappingProvider {

    private final ObjectMapper objectMapper;

    public JacksonMappingProvider() {
        this(new ObjectMapper());
    }

    public JacksonMappingProvider(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }


    @Override
    public <T> T map(Object source, Class<T> targetType, Configuration configuration) {
        if(source == null){
            return null;
        }
        try {
            return objectMapper.convertValue(source, targetType);
        } catch (Exception e) {
            throw new MappingException(e);
        }

    }

    @Override
    public <T> T map(Object source, final TypeRef<T> targetType, Configuration configuration) {
        if(source == null){
            return null;
        }
        JavaType type = objectMapper.getTypeFactory().constructType(targetType.getType());

        try {
            return (T)objectMapper.convertValue(source, type);
        } catch (Exception e) {
            throw new MappingException(e);
        }

    }
}
