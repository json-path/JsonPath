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
package com.jayway.jsonpath.spi.impl;

import com.jayway.jsonpath.InvalidJsonException;
import com.jayway.jsonpath.spi.JsonProvider;
import com.jayway.jsonpath.spi.Mode;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author Kalle Stenflo
 */
public class JacksonProvider extends AbstractJsonProvider {
    @Override
    public Mode getMode() {
        return Mode.STRICT;
    }

    @Override
    public Object parse(String json) throws InvalidJsonException {
        try {
            return new ObjectMapper().readValue(json, Object.class);
        } catch (IOException e) {
            throw new InvalidJsonException(e);
        }
    }

    @Override
    public Object parse(Reader jsonReader) throws InvalidJsonException {
        try {
            return new ObjectMapper().readValue(jsonReader, Object.class);
        } catch (IOException e) {
            throw new InvalidJsonException(e);
        }
    }

    @Override
    public Object parse(InputStream jsonStream) throws InvalidJsonException {
        try {
            return new ObjectMapper().readValue(jsonStream, Object.class);
        } catch (IOException e) {
            throw new InvalidJsonException(e);
        }
    }

    @Override
    public String toJson(Object obj) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Map<String, Object> createMap() {
        return new HashMap<String, Object>();
    }

    @Override
    public List<Object> createList() {
        return new LinkedList<Object>();
    }
}
