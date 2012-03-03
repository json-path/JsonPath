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
package com.jayway.jsonpath;

import com.jayway.jsonpath.spi.JsonProviderFactory;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.type.CollectionType;

import java.util.List;
import java.util.Set;

/**
 * @author Kalle Stenflo
 */
public class JsonModel {

    private static ObjectMapper objectMapper;

    private Object jsonObject;

    // --------------------------------------------------------
    //
    // Constructors
    //
    // --------------------------------------------------------

    public JsonModel(String json) {
        this(JsonProviderFactory.getInstance().parse(json));
    }

    private JsonModel(Object jsonObject) {
        this.jsonObject = jsonObject;
    }

    @SuppressWarnings({"unchecked"})
    public <T> T get(String jsonPath) {
        JsonPath path = JsonPath.compile(jsonPath);
        return (T) get(path);
    }

    @SuppressWarnings({"unchecked"})
    public <T> T get(JsonPath jsonPath) {
        return (T) jsonPath.read(jsonObject);
    }

    public String getJson() {
        return JsonProviderFactory.getInstance().toJson(jsonObject);
    }

    public String getJson(String jsonPath) {
        return JsonProviderFactory.getInstance().toJson(get(jsonPath));
    }

    public String getJson(JsonPath jsonPath) {
        return JsonProviderFactory.getInstance().toJson(get(jsonPath));
    }


    public JsonModel getSubModel(String jsonPath) {
        JsonPath path = JsonPath.compile(jsonPath);
        return getSubModel(path);
    }

    public JsonModel getSubModel(JsonPath jsonPath) {
        Object subModel = jsonPath.read(jsonObject);
        return new JsonModel(subModel);
    }


    public MappingModelReader map(final String jsonPath) {

        return new MappingModelReader() {

            private ObjectMapper objectMapper = JsonModel.getObjectMapper();

            @Override
            public <T> List<T> toListOf(Class<T> targetClass) {
                Object model = JsonModel.this.get(jsonPath);
                CollectionType colType = objectMapper.getTypeFactory().constructCollectionType(List.class, targetClass);
                return objectMapper.convertValue(model, colType);
            }

            @Override
            public <T> Set<T> toSetOf(Class<T> targetClass) {
                Object model = JsonModel.this.get(jsonPath);
                CollectionType colType = objectMapper.getTypeFactory().constructCollectionType(Set.class, targetClass);
                return objectMapper.convertValue(model, colType);
            }

            @Override
            public <T> T to(Class<T> targetClass) {
                Object model = JsonModel.this.get(jsonPath);
                return objectMapper.convertValue(model, targetClass);
            }
        };
    }

    // --------------------------------------------------------
    //
    // Static factory methods
    //
    // --------------------------------------------------------
    public static JsonModel create(String json) {
        return new JsonModel(json);
    }

    public static JsonModel create(Object jsonObject) {
        return new JsonModel(jsonObject);
    }


    // --------------------------------------------------------
    //
    // Support interfaces
    //
    // --------------------------------------------------------
    public interface MappingModelReader {
        <T> List<T> toListOf(Class<T> targetClass);

        <T> Set<T> toSetOf(Class<T> targetClass);

        <T> T to(Class<T> targetClass);
    }

    // --------------------------------------------------------
    //
    // Private helpers
    //
    // --------------------------------------------------------
    private static ObjectMapper getObjectMapper() {
        if (JsonModel.objectMapper == null) {
            synchronized (JsonModel.class) {
                JsonModel.objectMapper = new ObjectMapper();
            }
        }
        return JsonModel.objectMapper;
    }


}
