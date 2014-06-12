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
package com.jayway.jsonpath.internal.spi.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.jayway.jsonpath.InvalidJsonException;
import com.jayway.jsonpath.spi.json.Mode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringWriter;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class JacksonProvider extends AbstractJsonProvider {

    private static final Logger logger = LoggerFactory.getLogger(JacksonProvider.class);

    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final ObjectReader objectReader = objectMapper.reader().withType(Object.class);

    public Mode getMode() {
        return Mode.STRICT;
    }

    @Override
    public Object parse(String json) throws InvalidJsonException {
        try {
            return objectReader.readValue(json);
        } catch (IOException e) {
            logger.debug("Invalid JSON: \n" + json);
            throw new InvalidJsonException(e);
        }
    }

    @Override
    public Object parse(Reader jsonReader) throws InvalidJsonException {
        try {
            return objectReader.readValue(jsonReader);
        } catch (IOException e) {
            throw new InvalidJsonException(e);
        }
    }

    @Override
    public Object parse(InputStream jsonStream) throws InvalidJsonException {
        try {
            return objectReader.readValue(jsonStream);
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
            throw new InvalidJsonException();
        }
    }

    @Override
    public Map<String, Object> createMap() {
        return new LinkedHashMap<String, Object>();
    }

    @Override
    public List<Object> createArray() {
        return new LinkedList<Object>();
    }


}
