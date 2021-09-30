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
package com.jayway.jsonpath.spi.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.jayway.jsonpath.InvalidJsonException;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

public class JacksonJsonProvider extends AbstractJsonProvider {

    private static final ObjectMapper defaultObjectMapper = new ObjectMapper();
    private static final ObjectReader defaultObjectReader = defaultObjectMapper.reader().forType(Object.class);

    protected ObjectMapper objectMapper;
    protected ObjectReader objectReader;

    public ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    /**
     * Initialize the JacksonProvider with the default ObjectMapper and ObjectReader
     */
    public JacksonJsonProvider() {
      this(defaultObjectMapper, defaultObjectReader);
    }

    /**
     * Initialize the JacksonProvider with a custom ObjectMapper.
     * @param objectMapper the ObjectMapper to use
     */
    public JacksonJsonProvider(ObjectMapper objectMapper) {
      this(objectMapper, objectMapper.reader().forType(Object.class));
    }

    /**
     * Initialize the JacksonProvider with a custom ObjectMapper and ObjectReader.
     * @param objectMapper the ObjectMapper to use
     * @param objectReader the ObjectReader to use
     */
    public JacksonJsonProvider(ObjectMapper objectMapper, ObjectReader objectReader) {
      this.objectMapper = objectMapper;
      this.objectReader = objectReader;
    }

    @Override
    public Object parse(String json) throws InvalidJsonException {
        try {
            return objectReader.readValue(json);
        } catch (IOException e) {
            throw new InvalidJsonException(e, json);
        }
    }

    @Override
    public Object parse(InputStream jsonStream, String charset) throws InvalidJsonException {
        try {
            return objectReader.readValue(new InputStreamReader(jsonStream, charset));
        } catch (IOException e) {
            throw new InvalidJsonException(e);
        }
    }

    @Override
    public String toJson(Object obj) {
        StringWriter writer = new StringWriter();
        try {
            JsonGenerator generator = objectMapper.getFactory().createGenerator(writer);
            objectMapper.writeValue(generator, obj);
            writer.flush();
            writer.close();
            generator.close();
            return writer.getBuffer().toString();
        } catch (IOException e) {
            throw new InvalidJsonException(e);
        }
    }

    @Override
    public List<Object> createArray() {
        return new LinkedList<Object>();
    }

    @Override
    public Object createMap() {
        return new LinkedHashMap<String, Object>();
    }
}
