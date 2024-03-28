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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPathException;
import com.jayway.jsonpath.TypeRef;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Callable;

public class GsonMappingProvider implements MappingProvider {

    private static final Logger logger = LoggerFactory.getLogger(GsonMappingProvider.class);

    private final Callable<Gson> factory;

    /**
     * Allows the caller to provide a GsonBuilder so that Gson will be created with the callers configuration rather than using the default one
     * @param builder a user defined GsonBuilder instance
     * @since 2.8.0
     */
    public GsonMappingProvider(final GsonBuilder builder) {
        this(builder::create);
    }

    /**
     * Allows the caller to provide a Gson instance rather than using the default one
     * @param gson a user defined Gson instance
     * @since 1.2.0
     */
    public GsonMappingProvider(final Gson gson) {
        this(() -> gson);
    }

    public GsonMappingProvider(Callable<Gson> factory) {
        this.factory = factory;
    }

    /**
     * Gson will be created using its default configuration
     * @since 1.2.0
     */
    public GsonMappingProvider() {
        super();
        try {
            Class.forName("com.google.gson.Gson");
            this.factory = new Callable<Gson>() {
                @Override
                public Gson call() {
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
        if(source == null){
            return null;
        }
        try {
            return factory.call().getAdapter(targetType).fromJsonTree((JsonElement) source);
        } catch (Exception e){
            throw new MappingException(e);
        }
    }

    @Override
    public <T> T map(Object source, TypeRef<T> targetType, Configuration configuration) {
        if(source == null){
            return null;
        }
        try {
            return (T) factory.call().getAdapter(TypeToken.get(targetType.getType())).fromJsonTree((JsonElement) source);
        } catch (Exception e){
            throw new MappingException(e);
        }
    }
}
