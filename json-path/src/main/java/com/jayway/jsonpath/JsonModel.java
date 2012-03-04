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
import org.apache.commons.io.IOUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.type.CollectionType;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.util.Arrays.asList;
import static org.apache.commons.lang.Validate.notEmpty;
import static org.apache.commons.lang.Validate.notNull;

/**
 * A JsonModel represents a parsed JSON document that provides easy and efficient read operations. In contrast to the
 * static read operations provided by {@link JsonPath} a JsonModel will only parse the document once.
 *
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

    /**
     * Creates a new JsonModel
     *
     * @param json json string
     */
    public JsonModel(String json) {
        this(JsonProviderFactory.getInstance().parse(json));
    }

    /**
     * Creates a new JsonModel based on a json document.
     * Note that the jsonObject must either a {@link List} or a {@link Map}
     *
     * @param jsonObject the json object
     */
    private JsonModel(Object jsonObject) {
        notNull(jsonObject, "json can not be null");

        if (!(jsonObject instanceof Map) && !(jsonObject instanceof List)) {
            throw new IllegalArgumentException("Invalid container object");
        }

        this.jsonObject = jsonObject;
    }

    /**
     * Creates a new JsonModel based on an {@link InputStream}
     *
     * @param jsonInputStream the input stream
     */
    private JsonModel(InputStream jsonInputStream) {
        notNull(jsonInputStream, "jsonInputStream can not be null");

        this.jsonObject = JsonProviderFactory.getInstance().parse(jsonInputStream);
    }

    /**
     * Creates a new JsonModel by fetching the content from the provided URL
     *
     * @param jsonURL the URL to read
     * @throws IOException
     */
    private JsonModel(URL jsonURL) throws IOException {
        notNull(jsonURL, "jsonURL can not be null");

        InputStream jsonInputStream = null;
        try {
            jsonInputStream = jsonURL.openStream();
            this.jsonObject = JsonProviderFactory.getInstance().parse(jsonInputStream);
        } finally {
            IOUtils.closeQuietly(jsonInputStream);
        }
    }

    // --------------------------------------------------------
    //
    // Model readers
    //
    // --------------------------------------------------------

    @SuppressWarnings({"unchecked"})
    public <T> T get(String jsonPath) {
        notEmpty(jsonPath, "jsonPath can not be null or empty");

        JsonPath path = JsonPath.compile(jsonPath);
        return (T) get(path);
    }

    @SuppressWarnings({"unchecked"})
    public <T> T get(JsonPath jsonPath) {
        notNull(jsonPath, "jsonPath can not be null");

        return (T) jsonPath.read(jsonObject);
    }


    // --------------------------------------------------------
    //
    // JSON extractors
    //
    // --------------------------------------------------------
    public String toJson() {
        return JsonProviderFactory.getInstance().toJson(jsonObject);
    }

    public String toJson(String jsonPath) {
        notEmpty(jsonPath, "jsonPath can not be null or empty");

        return JsonProviderFactory.getInstance().toJson(get(jsonPath));
    }

    public String toJson(JsonPath jsonPath) {
        notNull(jsonPath, "jsonPath can not be null");

        return JsonProviderFactory.getInstance().toJson(get(jsonPath));
    }

    // --------------------------------------------------------
    //
    // Sub model readers
    //
    // --------------------------------------------------------

    public JsonModel getSubModel(String jsonPath) {
        notEmpty(jsonPath, "jsonPath can not be null or empty");

        JsonPath path = JsonPath.compile(jsonPath);
        return getSubModel(path);
    }

    public JsonModel getSubModel(JsonPath jsonPath) {
        notNull(jsonPath, "jsonPath can not be null");

        Object subModel = jsonPath.read(jsonObject);
        
        if(!(subModel instanceof Map) && !(subModel instanceof List)){
            throw new InvalidModelPathException("The path " + jsonPath.getPath() + " returned an invalid model " + (subModel!=null?subModel.getClass():"null"));
        }
        
        return new JsonModel(subModel);
    }

    // --------------------------------------------------------
    //
    // Mapping model readers
    //
    // --------------------------------------------------------
    public MappingModelReader map(final String jsonPath) {
        notEmpty(jsonPath, "jsonPath can not be null or empty");

        return new DefaultMappingModelReader(JsonModel.this.get(jsonPath));
    }

    public MappingModelReader map(final JsonPath jsonPath) {
        notNull(jsonPath, "jsonPath can not be null");

        return new DefaultMappingModelReader(JsonModel.this.get(jsonPath));
    }

    // --------------------------------------------------------
    //
    // Static factory methods
    //
    // --------------------------------------------------------
    public static JsonModel create(String json) {
        notEmpty(json, "json can not be null or empty");

        return new JsonModel(json);
    }

    public static JsonModel create(Object jsonObject) {
        notNull(jsonObject, "jsonObject can not be null");

        return new JsonModel(jsonObject);
    }

    public static JsonModel create(URL url) throws IOException {
        notNull(url, "url can not be null");

        return new JsonModel(url);
    }

    public static JsonModel create(InputStream jsonInputStream) throws IOException {
        notNull(jsonInputStream, "jsonInputStream can not be null");

        return new JsonModel(jsonInputStream);
    }

    // --------------------------------------------------------
    //
    // Interfaces
    //
    // --------------------------------------------------------
    public interface MappingModelReader {

        ListMappingModelReader toList();

        <T> List<T> toListOf(Class<T> targetClass);

        <T> Set<T> toSetOf(Class<T> targetClass);

        <T> T to(Class<T> targetClass);
    }

    public interface ListMappingModelReader {
        <T> List<T> of(Class<T> targetClass);
    }

    private static class DefaultMappingModelReader implements MappingModelReader, ListMappingModelReader {
        private ObjectMapper objectMapper;
        private Object model;

        private DefaultMappingModelReader(Object model) {
            this.model = model;
            this.objectMapper = JsonModel.getObjectMapper();
        }

        @Override
        public ListMappingModelReader toList() {
            return this;
        }

        @Override
        public <T> List<T> of(Class<T> targetClass) {
            return toListOf(targetClass);
        }

        @Override
        public <T> List<T> toListOf(Class<T> targetClass) {
            if (!(model instanceof List)) {
                model = asList(model);
            }
            CollectionType colType = objectMapper.getTypeFactory().constructCollectionType(List.class, targetClass);
            return objectMapper.convertValue(model, colType);
        }

        @Override
        public <T> Set<T> toSetOf(Class<T> targetClass) {
            if (!(model instanceof List)) {
                Set setModel = new HashSet();
                setModel.add(model);
                model = setModel;
            }
            CollectionType colType = objectMapper.getTypeFactory().constructCollectionType(Set.class, targetClass);
            return objectMapper.convertValue(model, colType);
        }

        @Override
        public <T> T to(Class<T> targetClass) {
            return objectMapper.convertValue(model, targetClass);
        }


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
