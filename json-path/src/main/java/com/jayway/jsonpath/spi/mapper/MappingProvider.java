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

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.TypeRef;

/**
 * Maps object between different Types
 */
public interface MappingProvider {

    /**
     * @param source object to map
     * @param targetType the type the source object should be mapped to
     * @param configuration current configuration
     * @param <T> the mapped result type
     * @return return the mapped object
     */
    <T> T map(Object source, Class<T> targetType, Configuration configuration);

    /**
     * @param source object to map
     * @param targetType the type the source object should be mapped to
     * @param configuration current configuration
     * @param <T> the mapped result type
     * @return return the mapped object
     */
    <T> T map(Object source, TypeRef<T> targetType, Configuration configuration);
}
