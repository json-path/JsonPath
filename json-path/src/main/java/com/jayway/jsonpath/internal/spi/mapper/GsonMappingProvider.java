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

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPathException;
import com.jayway.jsonpath.spi.mapper.MappingProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GsonMappingProvider implements MappingProvider {

    private static final Logger logger = LoggerFactory.getLogger(GsonMappingProvider.class);

    private final Factory<Gson> factory;

    public GsonMappingProvider(final Gson gson) {
        this(new Factory<Gson>() {
            @Override
            public Gson createInstance() {
                return gson;
            }
        });
    }

    public GsonMappingProvider(Factory<Gson> factory) {
        this.factory = factory;
    }

    public GsonMappingProvider() {
        super();
        try {
            Class.forName("com.google.gson.Gson");
            this.factory = new Factory<Gson>() {
                @Override
                public Gson createInstance() {
                    return new Gson();
                }
            };
        } catch (ClassNotFoundException e) {
            logger.error("Gson not found on class path. No converters configured.");
            throw new JsonPathException("Gson not found on path", e);
        }
    }

    @Override
    public <T> T map(Object source, Class<T> targetType, Configuration configuration) {
        return factory.createInstance().getAdapter(targetType).fromJsonTree((JsonElement) source);
    }
}
